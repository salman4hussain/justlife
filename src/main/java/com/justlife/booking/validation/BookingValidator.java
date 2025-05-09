package com.justlife.booking.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class BookingValidator {

    public void validateRequest(LocalDateTime startTime, Integer durationHours, int requestedCount) {
        if (durationHours == null || (durationHours != 2 && durationHours != 4)) {
            throw new ResponseStatusException(HttpStatus.OK, "Duration must be either 2 or 4 hours");
        }

        if (requestedCount < 1 || requestedCount > 3) {
            throw new ResponseStatusException(HttpStatus.OK, "Professional count must be between 1 and 3");
        }

        if (startTime.getDayOfWeek() == DayOfWeek.FRIDAY) {
            throw new ResponseStatusException(HttpStatus.OK, "Bookings are not allowed on Fridays");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.OK, "Cannot book in the past");
        }

        int minute = startTime.getMinute();
        if (!(minute == 0 || minute == 30) || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            throw new ResponseStatusException(HttpStatus.OK, "Booking must start at either :00 or :30 minute mark (e.g., 10:00, 10:30, 14:00)");
        }

        LocalTime bookingStart = startTime.toLocalTime();
        LocalTime bookingEnd = bookingStart.plusHours(durationHours);

        if (bookingStart.isBefore(LocalTime.of(8, 0)) || bookingEnd.isAfter(LocalTime.of(22, 0))) {
            throw new ResponseStatusException(HttpStatus.OK, "Booking time must be between 08:00 and 22:00");
        }
    }
}
