package com.justlife.booking.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleBookingLoad {
    private final Map<Long, List<Long>> vehicleToProfessionals = new HashMap<>();

    public void add(Long vehicleId, List<Long> profIds) {
        vehicleToProfessionals.put(vehicleId, profIds);
    }

    public Map<Long, List<Long>> getVehicleToProfessionals() {
        return vehicleToProfessionals;
    }
}