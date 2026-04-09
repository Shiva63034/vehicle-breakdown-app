package com.breakdown.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.breakdown.entity.Booking;
import com.breakdown.entity.Mechanic;
import com.breakdown.entity.User;
import com.breakdown.repository.UserRepository;
import com.breakdown.service.BookingService;
import com.breakdown.service.MechanicService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final UserRepository userRepository;

    // MANUAL constructor
    public AdminController(BookingService bookingService,
                           MechanicService mechanicService,
                           UserRepository userRepository) {
        this.bookingService = bookingService;
        this.mechanicService = mechanicService;
        this.userRepository = userRepository;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/mechanics")
    public ResponseEntity<List<Mechanic>> getAllMechanics() {
        return ResponseEntity.ok(mechanicService.getAllMechanics());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        long totalUsers = userRepository.count();
        long totalBookings = bookingService.getAllBookings().size();
        long totalMechanics = mechanicService.getAllMechanics().size();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalBookings", totalBookings);
        stats.put("totalMechanics", totalMechanics);

        return ResponseEntity.ok(stats);
    }
}