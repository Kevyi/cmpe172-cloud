package edu.sjsu.cmpe172.starterdemo.service;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.model.Availability_Slot;

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

        // Re-read the slot from DB to get the freshest state
        Availability_Slot recentSlot = availabilitySlotMapper.findAppointment(
                slot.getServer_id(), slot.getDate(), slot.getStart_time());

        if (!recentSlot.getStatus()) return false; // already booked

        int rowsAffected = availabilitySlotMapper.update(slot, false);

        if (rowsAffected == 0) {
            log.info("Optimistic Lock prevented update — slot was concurrently booked.");
            return false;
        }

        appointmentMapper.insert(appointment);
        return true;
    }
}
