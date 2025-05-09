package com.justlife.booking.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justlife.booking.controller.BookingController.BookingRequest;
import com.justlife.booking.dto.BookingResponse;
import com.justlife.booking.dto.VehicleAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import com.justlife.booking.entity.Vehicle;
import com.justlife.booking.service.AvailabilityService;
import com.justlife.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private AvailabilityService availabilityService;
    @Autowired
    private ObjectMapper objectMapper;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(booking.getStartTime().plusHours(2));
        booking.setDurationMinutes(120);
        booking.setProfessionalCount(2);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(101L);
        vehicle.setName("Van 1");
        booking.setVehicle(vehicle);
        booking.setProfessionals(Collections.emptyList());
    }

    @Test
    void shouldReturnAvailableVehicles() throws Exception {
        VehicleAvailabilityDTO dto = new VehicleAvailabilityDTO() {
            public Long getVehicleId() { return 1L; }
            public int getTotalProfessionals() { return 5; }
            public int getAssignedCount() { return 2; }
        };

        Mockito.when(bookingService.findAvailableVehicles(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bookings/available-vehicles")
                        .param("startTime", "2025-05-10T10:00:00")
                        .param("duration", "2")
                        .param("professionalCount", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehicleId").value(1));
    }
    @Test
    void shouldCreateBookingSuccessfully() throws Exception {
        Booking booking = new Booking();
        booking.setId(123L);
        booking.setStartTime(LocalDateTime.of(2025, 5, 10, 10, 0));
        booking.setEndTime(LocalDateTime.of(2025, 5, 10, 12, 30));
        booking.setDurationMinutes(120);
        booking.setProfessionalCount(2);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("Vehicle A");
        booking.setVehicle(vehicle);

        Professional p1 = new Professional();
        p1.setId(1L);
        Professional p2 = new Professional();
        p2.setId(2L);
        booking.setProfessionals(List.of(p1, p2));

        Mockito.when(bookingService.createBooking(any(LocalDateTime.class),
                        anyInt(),
                        anyInt()))
                .thenReturn(booking);



        BookingRequest request = new BookingRequest();
        request.setStartTime(LocalDateTime.of(2025, 5, 10, 10, 0));
        request.setDurationHour(2);
        request.setProfessionalCount(2);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(123))
                .andExpect(jsonPath("$.vehicleId").value(1))
                .andExpect(jsonPath("$.professionalIds.length()").value(2));
    }
    @Test
    void shouldUpdateBookingSuccessfully() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setStartTime(LocalDateTime.now().plusDays(2));
        request.setDurationHour(3);
        request.setProfessionalCount(2);

        Mockito.when(bookingService.updateBooking(any(Long.class), any(LocalDateTime.class),
                anyInt(),
                anyInt())).thenReturn(booking);

        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenBookingNotExists() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setStartTime(LocalDateTime.now().plusDays(2));
        request.setDurationHour(3);
        request.setProfessionalCount(2);

        Mockito.when(bookingService.updateBooking(any(Long.class), any(LocalDateTime.class),
                        anyInt(),
                        anyInt()))
                .thenThrow(new RuntimeException("Booking not found"));

        mockMvc.perform(put("/api/bookings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

}

