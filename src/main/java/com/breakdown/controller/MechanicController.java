package com.breakdown.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.breakdown.dto.BookingDTOs.BookingResponse;
import com.breakdown.dto.MechanicDTOs;
import com.breakdown.dto.MechanicDTOs.CompleteJobRequest;
import com.breakdown.dto.MechanicDTOs.MechanicProfileRequest;
import com.breakdown.dto.MechanicDTOs.UpdateLocationRequest;
import com.breakdown.dto.BookingDTOs.CompleteBookingRequest;
import com.breakdown.entity.Booking;
import com.breakdown.entity.Mechanic;
import com.breakdown.entity.User;
import com.breakdown.repository.MechanicRepository;
import com.breakdown.repository.UserRepository;
import com.breakdown.service.BookingService;
import com.breakdown.service.MechanicService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/mechanic")
public class MechanicController {

    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final MechanicRepository mechanicRepository;
    private final UserRepository userRepository;

    public MechanicController(
            BookingService bookingService,
            MechanicService mechanicService,
            MechanicRepository mechanicRepository,
            UserRepository userRepository) {
        this.bookingService = bookingService;
        this.mechanicService = mechanicService;
        this.mechanicRepository = mechanicRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(
            Principal principal,
            @RequestBody MechanicProfileRequest request) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Mechanic mechanic = new Mechanic();
        mechanic.setUser(user);
        mechanic.setSkills(request.getSkills());
        mechanic.setExperienceYears(request.getExperienceYears());
        return ResponseEntity.ok(mechanicRepository.save(mechanic));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(mechanicService.getMechanicProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @RequestBody MechanicProfileRequest request) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        mechanicService.updateProfile(user.getId(), request);
        return ResponseEntity.ok("Profile updated");
    }

    @PutMapping("/location")
    public ResponseEntity<?> updateLocation(
            Principal principal,
            @RequestBody UpdateLocationRequest request) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        mechanicService.updateLocation(user.getId(), request);
        return ResponseEntity.ok("Location updated");
    }

    @PatchMapping("/availability")
    public ResponseEntity<?> toggleAvailability(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        mechanicService.toggleAvailability(user.getId());
        return ResponseEntity.ok("Availability updated");
    }

    @GetMapping("/bookings/pending")
    public ResponseEntity<List<Booking>> getPendingBookings() {
        return ResponseEntity.ok(bookingService.getPendingBookings());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Mechanic mechanic = mechanicRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));
        return ResponseEntity.ok(bookingService.getMechanicBookings(mechanic.getId()));
    }

    @PatchMapping("/bookings/{id}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long id,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookingResponse response = bookingService.acceptBooking(id, user.getId());
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/bookings/{id}/start")
    public ResponseEntity<?> startJob(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(bookingService.startJob(id));
    }
    @PatchMapping(value = "/bookings/{id}/complete", consumes = {"multipart/form-data"})
    public ResponseEntity<BookingResponse> completeJob(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MechanicDTOs.CompleteJobRequest request =
                mapper.readValue(data, MechanicDTOs.CompleteJobRequest.class);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bookingService.completeJob(id, request, image, user.getId()));
    }
}
