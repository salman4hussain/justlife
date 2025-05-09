package com.justlife.booking.dto;

import java.util.List;

public record VehicleBookingInfo(Long vehicleId, List<Long> cleaners) {}
