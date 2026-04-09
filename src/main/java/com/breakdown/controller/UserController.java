package com.breakdown.controller;

import java.security.Principal;
import java.util.List;

import com.breakdown.dto.BookingDTOs.BookingRequest;
import com.breakdown.entity.Vehicle;
import com.breakdown.repository.UserRepository;
import com.breakdown.repository.VehicleRepository;
import com.breakdown.service.BookingService;
import com.breakdown.service.MechanicService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ObjectMapper objectMapper;

    public UserController(BookingService bookingService,
                          MechanicService mechanicService,
                          UserRepository userRepository,
                          VehicleRepository vehicleRepository,
                          ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.mechanicService = mechanicService;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        return ResponseEntity.ok(
                userRepository.findByEmail(principal.getName()));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> addVehicle(
            Principal principal,
            @RequestBody Vehicle vehicle) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        vehicle.setUser(user);
        return ResponseEntity.ok(vehicleRepository.save(vehicle));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getMyVehicles(Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(vehicleRepository.findByUserId(user.getId()));
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/bookings", consumes = "multipart/form-data")
    public ResponseEntity<?> createBooking(
            Principal principal,
            @RequestPart("booking") String bookingJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        BookingRequest request = objectMapper.readValue(bookingJson, BookingRequest.class);

        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(bookingService.createBooking(user.getId(), request, image));
    }
        
    
    @GetMapping("/bookings/history")
    public ResponseEntity<?> getBookingHistory(Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bookingService.getUserBookingHistory(user.getId()));
    }
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBooking(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping("/bookings/{id}/feedback")
    public ResponseEntity<?> submitFeedback(
            @PathVariable Long id,
            Principal principal,
            @RequestBody java.util.Map<String, Object> feedback) {
        int rating = (int) feedback.get("rating");
        String comment = (String) feedback.getOrDefault("comment", "");
        return ResponseEntity.ok(bookingService.submitFeedback(id, rating, comment));
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
    @GetMapping("/mechanics/nearby")
    public ResponseEntity<?> getNearbyMechanics(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10") Double radius) {
        return ResponseEntity.ok(mechanicService.getNearbyMechanics(lat, lng));
    }
}