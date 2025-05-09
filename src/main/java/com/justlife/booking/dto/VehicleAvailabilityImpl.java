package com.justlife.booking.dto;

public class VehicleAvailabilityImpl implements VehicleAvailabilityDTO {
    private final Long vehicleId;
    private final int assignedCount;
    private final int totalProfessionals;

    public VehicleAvailabilityImpl(Long vehicleId, int assignedCount, int totalProfessionals) {
        this.vehicleId = vehicleId;
        this.assignedCount = assignedCount;
        this.totalProfessionals = totalProfessionals;
    }

    @Override
    public Long getVehicleId() {
        return vehicleId;
    }

    @Override
    public int getAssignedCount() {
        return assignedCount;
    }

    @Override
    public int getTotalProfessionals() {
        return totalProfessionals;
    }
}
