package com.breakdown.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.breakdown.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByMechanicIdOrderByCreatedAtDesc(Long mechanicId);

    List<Booking> findByStatusOrderByCreatedAtDesc(Booking.BookingStatus status);

    List<Booking> findAllByOrderByCreatedAtDesc();
   
}