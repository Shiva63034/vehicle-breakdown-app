package com.breakdown.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.breakdown.entity.Mechanic;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {

    Optional<Mechanic> findByUserId(Long userId);

    @Query(value = """
        SELECT * FROM mechanics m
        WHERE m.is_available = true
        AND (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(m.current_latitude))
                * cos(radians(m.current_longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(m.current_latitude))
            )
        ) < :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(m.current_latitude))
                * cos(radians(m.current_longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(m.current_latitude))
            )
        ) ASC
        """, nativeQuery = true)
    List<Mechanic> findNearbyAvailableMechanics(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm
    );
}