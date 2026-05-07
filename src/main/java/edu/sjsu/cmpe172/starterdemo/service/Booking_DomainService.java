package edu.sjsu.cmpe172.starterdemo.service;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.model.Availability_Slot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sjsu.cmpe172.starterdemo.mapper.AppointmentMapper;
import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;

@Service
public class Booking_DomainService {

    private final AppointmentMapper appointmentMapper;
    private final AvailabilitySlotMapper availabilitySlotMapper;
    private static final Logger log = Logger.getLogger(Booking_DomainService.class.getName());

    public Booking_DomainService(AppointmentMapper appointmentMapper, AvailabilitySlotMapper availabilitySlotMapper) {
        this.appointmentMapper = appointmentMapper;
        this.availabilitySlotMapper = availabilitySlotMapper;
    }

    /**
     * Books an appointment using optimistic concurrency control.
     * The UPDATE only succeeds if status = TRUE at the moment of the write,
     * preventing double-bookings without pessimistic locking.
     */
    @Transactional
    public boolean bookAppointment(Availability_Slot slot, Appointment appointment) {
        Availability_Slot recentSlot = availabilitySlotMapper.findAppointment(
                slot.getServer_id(), slot.getDate(), slot.getStart_time());

        if (!recentSlot.getStatus()) return false;

        int rowsAffected = availabilitySlotMapper.update(slot, false);

        if (rowsAffected == 0) {
            log.info("Optimistic Lock prevented update — slot was concurrently booked.");
            return false;
        }

        appointmentMapper.insert(appointment);
        return true;
    }

    /**
     * Reschedules an appointment to a new slot using optimistic concurrency control.
     * Atomically claims the new slot, releases the old slot, and updates the appointment.
     */
    @Transactional
    public boolean rescheduleAppointment(String appId, String newServerId, LocalDateTime newStart) {
        Optional<Appointment> opt = appointmentMapper.findById(appId);
        if (opt.isEmpty()) return false;
        Appointment apt = opt.get();

        LocalDate newDate = newStart.toLocalDate();
        Availability_Slot newSlot;
        try {
            newSlot = availabilitySlotMapper.findAppointment(newServerId, newDate, newStart);
        } catch (Exception e) {
            log.warning("New slot not found for reschedule: " + newServerId + " " + newStart);
            return false;
        }

        if (!newSlot.getStatus()) return false;

        int rows = availabilitySlotMapper.update(newSlot, false);
        if (rows == 0) {
            log.info("Optimistic lock prevented reschedule — new slot was concurrently booked.");
            return false;
        }

        availabilitySlotMapper.releaseSlot(
            apt.getServer_id(),
            apt.getStart_time().toLocalDate(),
            apt.getStart_time()
        );

        apt.setServer_id(newServerId);
        apt.setStart_time(newSlot.getStart_time());
        apt.setEnd_time(newSlot.getEnd_time());
        apt.setStatus("PENDING");
        appointmentMapper.update(apt);
        return true;
    }
}
