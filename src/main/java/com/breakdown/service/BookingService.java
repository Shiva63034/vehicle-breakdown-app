package com.breakdown.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.breakdown.dto.BookingDTOs;
import com.breakdown.dto.MechanicDTOs;
import com.breakdown.entity.Booking;
import com.breakdown.entity.Mechanic;
import com.breakdown.entity.User;
import com.breakdown.entity.Vehicle;
import com.breakdown.repository.BookingRepository;
import com.breakdown.repository.MechanicRepository;
import com.breakdown.repository.UserRepository;
import com.breakdown.repository.VehicleRepository;

@Service
public class BookingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final VehicleRepository vehicleRepo;
    private final MechanicRepository mechanicRepo;
    private final S3Service s3Service;

    public BookingService(
            BookingRepository bookingRepo,
            UserRepository userRepo,
            VehicleRepository vehicleRepo,
            MechanicRepository mechanicRepo,
            S3Service s3Service,
            SimpMessagingTemplate messagingTemplate) {
        this.bookingRepo   = bookingRepo;
        this.userRepo      = userRepo;
        this.vehicleRepo   = vehicleRepo;
        this.mechanicRepo  = mechanicRepo;
        this.s3Service     = s3Service;
        this.messagingTemplate = messagingTemplate;
    }

    // ── USER BOOKING HISTORY ─────────────────────────────────────────────────
    public List<Booking> getUserBookingHistory(Long userId) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ── SUBMIT FEEDBACK ──────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse submitFeedback(Long bookingId, int rating, String comment) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getMechanic() != null) {
            Mechanic mechanic = booking.getMechanic();
            double newRating = ((mechanic.getRating() * mechanic.getTotalJobs()) + rating)
                    / (mechanic.getTotalJobs() + 1);
            mechanic.setRating(newRating);
            mechanic.setTotalJobs(mechanic.getTotalJobs() + 1);
            mechanicRepo.save(mechanic);
        }
        return mapToResponse(bookingRepo.save(booking));
    }

    // ── CREATE BOOKING ───────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse createBooking(Long userId, BookingDTOs.BookingRequest dto, MultipartFile image) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setIssueType(dto.getIssueType());
        booking.setIssueDescription(dto.getIssueDescription());
        booking.setUserLatitude(dto.getUserLatitude());
        booking.setUserLongitude(dto.getUserLongitude());
        booking.setUserAddress(dto.getUserAddress());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadFile(image, "issue-images");
                booking.setIssueImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        Booking saved = bookingRepo.save(booking);

        // Notify ALL mechanics about new booking
        messagingTemplate.convertAndSend(
                "/topic/new-booking",
                "New " + saved.getIssueType().name().replace("_", " ") + " request!"
        );

        System.out.println("📢 Broadcast new booking to /topic/new-booking");

        return mapToResponse(saved);
    }

    // ── GET USER BOOKINGS ────────────────────────────────────────────────────
    public List<Booking> getUserBookings(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // ── GET MECHANIC BOOKINGS ────────────────────────────────────────────────
    public List<Booking> getMechanicBookings(Long mechanicId) {
        return bookingRepo.findByMechanicIdOrderByCreatedAtDesc(mechanicId);
    }

    // ── ACCEPT BOOKING ───────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse acceptBooking(Long bookingId, Long userId) {
        Mechanic mechanic = mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setMechanic(mechanic);
        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        booking.setAcceptedAt(LocalDateTime.now());
        Booking saved = bookingRepo.save(booking);

        Long bookingUserId = saved.getUser().getId();
        System.out.println("📢 Broadcasting ACCEPTED to /topic/user/" + bookingUserId);

        // ✅ Notify the USER who raised the booking
        messagingTemplate.convertAndSend("/topic/user/" + bookingUserId, mapToResponse(saved));
        // Notify mechanics this job is taken
        messagingTemplate.convertAndSend("/topic/new-booking", "ACCEPTED:" + saved.getId());

        return mapToResponse(saved);
    }

    // ── START JOB ────────────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse startJob(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        Booking saved = bookingRepo.save(booking);

        Long bookingUserId = saved.getUser().getId();
        System.out.println("📢 Broadcasting IN_PROGRESS to /topic/user/" + bookingUserId);

        // ✅ Notify the USER who raised the booking
        messagingTemplate.convertAndSend("/topic/user/" + bookingUserId, mapToResponse(saved));
        messagingTemplate.convertAndSend("/topic/new-booking", "IN_PROGRESS:" + saved.getId());

        return mapToResponse(saved);
    }

    // ── COMPLETE JOB ─────────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse completeJob(Long bookingId, MechanicDTOs.CompleteJobRequest request, MultipartFile image, Long userId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Mechanic mechanic = mechanicRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        if (booking.getMechanic() == null || !booking.getMechanic().getId().equals(mechanic.getId())) {
            throw new RuntimeException("Unauthorized: Not your booking");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        booking.setFinalAmount(request.getFinalAmount());

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadFile(image, "completion-images");
                booking.setCompletionImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        Booking saved = bookingRepo.save(booking);

        Long bookingUserId = saved.getUser().getId();
        System.out.println("📢 Broadcasting COMPLETED to /topic/user/" + bookingUserId);

        // ✅ Notify the USER who raised the booking
        messagingTemplate.convertAndSend("/topic/user/" + bookingUserId, mapToResponse(saved));
        messagingTemplate.convertAndSend("/topic/new-booking", "COMPLETED:" + saved.getId());

        return mapToResponse(saved);
    }

    // ── CANCEL BOOKING ───────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking saved = bookingRepo.save(booking);

        Long bookingUserId = saved.getUser().getId();
        System.out.println("📢 Broadcasting CANCELLED to /topic/user/" + bookingUserId);

        // ✅ Notify the USER who raised the booking
        messagingTemplate.convertAndSend("/topic/user/" + bookingUserId, mapToResponse(saved));

        return mapToResponse(saved);
    }

    // ── GET BOOKING BY ID ────────────────────────────────────────────────────
    public BookingDTOs.BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }

    // ── GET ALL BOOKINGS ─────────────────────────────────────────────────────
    public List<Booking> getAllBookings() {
        return bookingRepo.findAllByOrderByCreatedAtDesc();
    }

    // ── GET PENDING BOOKINGS ─────────────────────────────────────────────────
    public List<Booking> getPendingBookings() {
        return bookingRepo.findByStatusOrderByCreatedAtDesc(Booking.BookingStatus.PENDING);
    }

    // ── MAP TO RESPONSE ──────────────────────────────────────────────────────
    private BookingDTOs.BookingResponse mapToResponse(Booking booking) {
        BookingDTOs.BookingResponse response = new BookingDTOs.BookingResponse();
        response.setId(booking.getId());
        response.setIssueType(booking.getIssueType().name());
        response.setIssueDescription(booking.getIssueDescription());
        response.setIssueImageUrl(booking.getIssueImageUrl());
        response.setStatus(booking.getStatus().name());
        response.setUserLatitude(booking.getUserLatitude());
        response.setUserLongitude(booking.getUserLongitude());
        response.setUserAddress(booking.getUserAddress());
        response.setCreatedAt(booking.getCreatedAt());
        response.setCompletedAt(booking.getCompletedAt());
        response.setFinalAmount(booking.getFinalAmount());
        response.setEstimatedCost(booking.getEstimatedCost());
        response.setPaymentDone(booking.getPaymentDone() != null && booking.getPaymentDone());
        if (booking.getMechanic() != null) {
            response.setMechanic(booking.getMechanic().getUser().getName());
        }
        return response;
    }
}