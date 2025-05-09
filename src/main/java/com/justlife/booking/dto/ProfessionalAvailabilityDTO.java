package com.justlife.booking.dto;

import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalAvailabilityDTO {

    private Long professionalId;
    private String name;
    private List<TimeRange> unavailableTimes;

    public ProfessionalAvailabilityDTO(Professional professional, List<Booking> bookings) {
        this.professionalId = professional.getId();
        this.name = professional.getName();
        this.unavailableTimes = bookings.stream()
                .map(b -> new TimeRange(b.getStartTime(), b.getEndTime()))
                .toList();
    }
    public ProfessionalAvailabilityDTO(Professional professional, List<Booking> bookings, LocalDate date) {
        this.professionalId = professional.getId();
        this.name = professional.getName();
        this.unavailableTimes = bookings.stream()
                .map(b -> new TimeRange(b.getStartTime(), b.getEndTime()))
                .filter(t -> t.from().toLocalDate().equals(date)) // Filter only for the given date
                .toList();
    }

}

