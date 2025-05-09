package com.justlife.booking.repository;

import com.justlife.booking.dto.VehicleAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = """
        SELECT 
            v.id AS vehicleId,
            COUNT(DISTINCT p.id) AS totalProfessionals,
            COUNT(DISTINCT CASE 
                WHEN b.start_time < :endTime AND b.end_time > :startTime
                THEN bp.professional_id
                ELSE NULL
            END) AS assignedCount
        FROM vehicle v
        JOIN professional p ON p.vehicle_id = v.id
        LEFT JOIN booking_professionals bp ON bp.professional_id = p.id
        LEFT JOIN booking b ON b.id = bp.booking_id
        GROUP BY v.id
        HAVING (COUNT(DISTINCT p.id) - 
                COUNT(DISTINCT CASE 
                    WHEN b.start_time < :endTime AND b.end_time > :startTime
                    THEN bp.professional_id
                    ELSE NULL
                END)) >= :requestedCount
        ORDER BY assignedCount ASC
        """, nativeQuery = true)
    List<VehicleAvailabilityDTO> findVehicleAvailability(@Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime,
                                                         @Param("requestedCount") int requestedCount);

    List<Booking> findByProfessionals_IdAndStartTimeBetween(Long professionalId, LocalDateTime from, LocalDateTime to);

    boolean existsByStartTimeBeforeAndEndTimeAfter(LocalDateTime endTime, LocalDateTime startTime);

}