package com.justlife.booking.service;

import com.justlife.booking.dto.VehicleAvailabilityDTO;
import com.justlife.booking.entity.Booking;
import com.justlife.booking.entity.Professional;
import com.justlife.booking.entity.Vehicle;
import com.justlife.booking.repository.BookingRepository;
import com.justlife.booking.repository.ProfessionalRepository;
import com.justlife.booking.repository.VehicleRepository;
import com.justlife.booking.util.BookingLockManager;
import com.justlife.booking.validation.BookingValidator;
import com.justlife.booking.util.TimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final VehicleRepository vehicleRepo;
    private final ProfessionalRepository professionalRepo;
    private final BookingValidator bookingValidator;
    private final BookingLockManager bookingLockManager;

    @Transactional(readOnly = true)
    public List<VehicleAvailabilityDTO> findAvailableVehicles(LocalDateTime startTime, int durationHours, int requestedCount) {
        LocalDateTime endTime = startTime.plusHours(durationHours);
        log.info("Checking vehicle availability from {} to {} for {} professionals", startTime, endTime, requestedCount);
        return bookingRepo.findVehicleAvailability(startTime, endTime, requestedCount);
    }

    @Transactional
    public Booking createBooking(LocalDateTime startTime, int durationHours, int requestedCount) {
        Booking booking = prepareBooking(startTime, durationHours, requestedCount);
        return bookingRepo.save(booking);
    }

    @Transactional
    public Booking updateBooking(Long bookingId, LocalDateTime newStart, int newDuration, int newProCount) {
        Booking existing = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Booking updated = prepareBooking(newStart, newDuration, newProCount);

        bookingRepo.delete(existing);
        return bookingRepo.save(updated);
    }

    private Booking prepareBooking(LocalDateTime startTime, int durationHours, int requestedCount) {
        bookingValidator.validateRequest(startTime, durationHours, requestedCount);

        LocalDateTime endTime = startTime.plusHours(durationHours);
        LocalDateTime adjustedEndTime = endTime.plusMinutes(30);
        TimeRange range = new TimeRange(startTime.minusMinutes(30), adjustedEndTime.plusMinutes(30));

        bookingLockManager.acquireLock(range);

        try {
            Vehicle selectedVehicle = null;
            List<Professional> selectedProfessionals = null;

            boolean hasConflicts = bookingRepo.existsByStartTimeBeforeAndEndTimeAfter(adjustedEndTime, startTime);

            if (!hasConflicts) {
                selectedVehicle = vehicleRepo.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No vehicles registered"));

                List<Professional> allPros = professionalRepo.findByVehicleId(selectedVehicle.getId());
                if (allPros.size() < requestedCount) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Not enough professionals available");
                }

                selectedProfessionals = allPros.subList(0, requestedCount);
            } else {
                List<VehicleAvailabilityDTO> availableVehicles = bookingRepo.findVehicleAvailability(startTime, adjustedEndTime, requestedCount);

                for (VehicleAvailabilityDTO v : availableVehicles) {
                    Vehicle vehicle = vehicleRepo.findById(v.getVehicleId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

                    List<Professional> team = professionalRepo.findByVehicleId(vehicle.getId());

                    List<Professional> available = team.stream().filter(p -> {
                        List<Booking> conflicts = bookingRepo.findByProfessionals_IdAndStartTimeBetween(
                                p.getId(), startTime.minusMinutes(30), adjustedEndTime.plusMinutes(30));
                        return conflicts.isEmpty();
                    }).toList();

                    if (available.size() >= requestedCount) {
                        selectedVehicle = vehicle;
                        selectedProfessionals = available.subList(0, requestedCount);
                        break;
                    }
                }

                if (selectedVehicle == null || selectedProfessionals == null) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No vehicle with sufficient available professionals found");
                }
            }

            Booking booking = new Booking();
            booking.setStartTime(startTime);
            booking.setEndTime(adjustedEndTime);
            booking.setDurationMinutes(durationHours * 60);
            booking.setProfessionalCount(requestedCount);
            booking.setVehicle(selectedVehicle);
            booking.setProfessionals(selectedProfessionals);

            return booking;

        } finally {
            bookingLockManager.releaseLock(range);
        }
    }

}
