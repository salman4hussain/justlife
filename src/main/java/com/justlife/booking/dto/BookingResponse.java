package com.justlife.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {
    private Long bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes;
    private int professionalCount;
    private Long vehicleId;
    private String vehicleName;
    private List<Long> professionalIds;
}
