package com.justlife.booking.repository;

import com.justlife.booking.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    List<Professional> findByVehicleId(Long vehicleId);
}
