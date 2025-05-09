package com.justlife.booking.controller;

import com.justlife.booking.dto.BookingResponse;
import com.justlife.booking.dto.ProfessionalAvailabilityDTO;
import com.justlife.booking.dto.VehicleAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.service.AvailabilityService;
import com.justlife.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "Booking", description = "Booking related endpoints")
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AvailabilityService availabilityService;
    @GetMapping
    @Operation(summary = "Get available professionals")
    public ResponseEntity<List<ProfessionalAvailabilityDTO>> getAvailableProfessionals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) Integer durationInHour
    ) {
        if (startTime != null && durationInHour != null) {
            return ResponseEntity.ok(
                    availabilityService.getProfessionalsAvailableForTimeRange(date, startTime, durationInHour)
            );
        } else {
            return ResponseEntity.ok(availabilityService.getProfessionalsAvailabilityForDay(date));
        }
    }


    @PostMapping
    @Operation(summary = "Make booking")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        log.info("Received create booking request: startTime={}, durationHours={}, professionalCount={}",
                request.getStartTime(), request.getDurationHour(), request.getProfessionalCount());

        Booking booking = bookingService.createBooking(
                request.getStartTime(),
                request.getDurationHour(),
                request.getProfessionalCount()
        );

        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setDurationMinutes(booking.getDurationMinutes());
        response.setProfessionalCount(booking.getProfessionalCount());
        response.setVehicleId(booking.getVehicle().getId());
        response.setVehicleName(booking.getVehicle().getName());
        response.setProfessionalIds(
                booking.getProfessionals().stream()
                        .map(p -> p.getId())
                        .toList()
        );

        log.info("Booking created successfully with id={} for vehicleId={}", response.getBookingId(), response.getVehicleId());
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update booking")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id, @RequestBody BookingRequest request) {
        Booking updated = bookingService.updateBooking(id, request.getStartTime(), request.getDurationHour(), request.getProfessionalCount());

        BookingResponse response = new BookingResponse();
        response.setBookingId(updated.getId());
        response.setStartTime(updated.getStartTime());
        response.setEndTime(updated.getEndTime());
        response.setDurationMinutes(updated.getDurationMinutes());
        response.setProfessionalCount(updated.getProfessionalCount());
        response.setVehicleId(updated.getVehicle().getId());
        response.setVehicleName(updated.getVehicle().getName());
        response.setProfessionalIds(updated.getProfessionals().stream().map(p -> p.getId()).toList());

        return ResponseEntity.ok(response);
    }

    @Data
    public static class BookingRequest {
        private LocalDateTime startTime;
        private int durationHour;
        private int professionalCount;
    }
}