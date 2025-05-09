package com.justlife.booking.dto;

import lombok.Data;

public interface VehicleAvailabilityDTO {
    Long getVehicleId();
    int getAssignedCount();
    int getTotalProfessionals();
}
