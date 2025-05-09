package com.justlife.booking.service;

import com.justlife.booking.dto.ProfessionalAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import com.justlife.booking.repository.BookingRepository;
import com.justlife.booking.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ProfessionalRepository professionalRepo;
    private final BookingRepository bookingRepo;

    public List<ProfessionalAvailabilityDTO> getProfessionalsAvailabilityForDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        List<Professional> professionals = professionalRepo.findAll();
        return professionals.stream()
                .map(professional -> {
                    List<Booking> bookings = bookingRepo.findByProfessionals_IdAndStartTimeBetween(
                            professional.getId(), startOfDay, endOfDay
                    );
                    return new ProfessionalAvailabilityDTO(professional, bookings, date);
                }).toList();
    }

    public List<ProfessionalAvailabilityDTO> getProfessionalsAvailableForTimeRange(LocalDate date, LocalTime startTime, int durationInHour) {
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = startDateTime.plusHours(durationInHour);

        return professionalRepo.findAll().stream()
                .filter(professional -> bookingRepo
                        .findByProfessionals_IdAndStartTimeBetween(professional.getId(), startDateTime, endDateTime)
                        .isEmpty()
                )
                .map(p -> new ProfessionalAvailabilityDTO(p.getId(), p.getName(), List.of()))
                .toList();
    }

}

