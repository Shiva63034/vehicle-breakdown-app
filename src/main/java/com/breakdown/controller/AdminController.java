package com.breakdown.controller;
 
import java.util.List;

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

import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
 
    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final UserRepository userRepository;
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
    public ResponseEntity<?> getStats() {
        long totalUsers = userRepository.count();
        long totalBookings = bookingService.getAllBookings().size();
        long totalMechanics = mechanicService.getAllMechanics().size();
        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("totalUsers", totalUsers);
            put("totalBookings", totalBookings);
            put("totalMechanics", totalMechanics);
        }});
    }
}