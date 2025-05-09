package com.justlife.booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingRequest(LocalDateTime dateTime, int duration, int requiredCount) {}

