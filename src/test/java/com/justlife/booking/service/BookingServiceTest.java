package com.justlife.booking.service;

import com.justlife.booking.dto.VehicleAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import com.justlife.booking.entity.Vehicle;
import com.justlife.booking.repository.BookingRepository;
import com.justlife.booking.repository.ProfessionalRepository;
import com.justlife.booking.repository.VehicleRepository;
import com.justlife.booking.service.BookingService;
import com.justlife.booking.util.BookingLockManager;
import com.justlife.booking.validation.BookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private BookingRepository bookingRepo;
    private VehicleRepository vehicleRepo;
    private ProfessionalRepository professionalRepo;
    private BookingValidator validator;
    private BookingLockManager lockManager;
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingRepo = mock(BookingRepository.class);
        vehicleRepo = mock(VehicleRepository.class);
        professionalRepo = mock(ProfessionalRepository.class);
        validator = mock(BookingValidator.class);
        lockManager = mock(BookingLockManager.class);

        bookingService = new BookingService(bookingRepo, vehicleRepo, professionalRepo, validator, lockManager);
    }

    @Test
    void shouldThrowIfNoVehicles() {
        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
        when(vehicleRepo.findAll()).thenReturn(Collections.emptyList());
        when(bookingRepo.existsByStartTimeBeforeAndEndTimeAfter(any(), any())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () ->
                bookingService.createBooking(now, 2, 2)
        );
        assertTrue(ex.getMessage().contains("No vehicles registered"));
    }

    @Test
    void shouldThrowIfNotEnoughProfessionals() {
        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
        Vehicle v = new Vehicle();
        v.setId(1L);
        when(vehicleRepo.findAll()).thenReturn(List.of(v));
        when(bookingRepo.existsByStartTimeBeforeAndEndTimeAfter(any(), any())).thenReturn(false);
        when(professionalRepo.findByVehicleId(1L)).thenReturn(List.of(new Professional()));

        Exception ex = assertThrows(Exception.class, () ->
                bookingService.createBooking(now, 2, 2)
        );
        assertTrue(ex.getMessage().contains("Not enough professionals available"));
    }

    @Test
    void shouldReturnAvailableVehicles() {
        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
        when(bookingRepo.findVehicleAvailability(any(), any(), anyInt())).thenReturn(List.of(mock(VehicleAvailabilityDTO.class)));
        List<VehicleAvailabilityDTO> results = bookingService.findAvailableVehicles(now, 2, 1);
        assertEquals(1, results.size());
    }

    @Test
    void shouldCreateBookingWhenAvailable() {
        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
        Vehicle v = new Vehicle();
        v.setId(1L);
        Professional p1 = new Professional(); p1.setId(1L);
        Professional p2 = new Professional(); p2.setId(2L);

        when(vehicleRepo.findAll()).thenReturn(List.of(v));
        when(bookingRepo.existsByStartTimeBeforeAndEndTimeAfter(any(), any())).thenReturn(false);
        when(professionalRepo.findByVehicleId(1L)).thenReturn(List.of(p1, p2));
        when(bookingRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(now, 2, 2);
        assertEquals(2, booking.getProfessionalCount());
        assertEquals(120, booking.getDurationMinutes());
        assertEquals(2, booking.getProfessionals().size());
    }

    @Test
    void shouldThrowIfNoAvailableVehicleInConflictScenario() {
        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
        when(bookingRepo.existsByStartTimeBeforeAndEndTimeAfter(any(), any())).thenReturn(true);
        when(bookingRepo.findVehicleAvailability(any(), any(), anyInt())).thenReturn(Collections.emptyList());

        Exception ex = assertThrows(Exception.class, () ->
                bookingService.createBooking(now, 2, 1)
        );
        assertTrue(ex.getMessage().contains("No vehicle with sufficient available professionals"));
    }
}
