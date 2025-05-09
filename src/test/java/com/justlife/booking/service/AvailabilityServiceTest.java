package com.justlife.booking.service;

import com.justlife.booking.dto.ProfessionalAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import com.justlife.booking.repository.BookingRepository;
import com.justlife.booking.repository.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    @Mock
    private ProfessionalRepository professionalRepo;

    @Mock
    private BookingRepository bookingRepo;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Professional professional1;
    private Professional professional2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        professional1 = new Professional();
        professional1.setId(1L);
        professional1.setName("Pro-1");

        professional2 = new Professional();
        professional2.setId(2L);
        professional2.setName("Pro-2");
    }

    @Test
    void testGetProfessionalsAvailabilityForDay() {
        LocalDate date = LocalDate.of(2025, 5, 12);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        when(professionalRepo.findAll()).thenReturn(List.of(professional1, professional2));
        when(bookingRepo.findByProfessionals_IdAndStartTimeBetween(1L, start, end)).thenReturn(List.of());
        when(bookingRepo.findByProfessionals_IdAndStartTimeBetween(2L, start, end)).thenReturn(List.of());

        List<ProfessionalAvailabilityDTO> result = availabilityService.getProfessionalsAvailabilityForDay(date);

        assertEquals(2, result.size());
        verify(professionalRepo, times(1)).findAll();
        verify(bookingRepo, times(1)).findByProfessionals_IdAndStartTimeBetween(1L, start, end);
        verify(bookingRepo, times(1)).findByProfessionals_IdAndStartTimeBetween(2L, start, end);
    }
}
