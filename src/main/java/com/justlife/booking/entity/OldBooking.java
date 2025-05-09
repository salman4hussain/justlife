package com.justlife.booking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "old_booking")
public class OldBooking {

    @Id
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes;
    private int professionalCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "old_booking_professionals",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "professional_id"))
    private List<Professional> professionals;
}
