package edu.sjsu.cmpe172.starterdemo.service;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.mapper.AppointmentMapper;
import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AppointmentService {

    private static final Logger log = Logger.getLogger(AppointmentService.class.getName());
    private final AppointmentMapper repo;
    private final AvailabilitySlotMapper slotMapper;

    public AppointmentService(AppointmentMapper repo, AvailabilitySlotMapper slotMapper) {
        this.repo = repo;
        this.slotMapper = slotMapper;
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    public List<Appointment> getAllAppointments(String email) {
        return repo.findByEmail(email);
    }

    public Optional<Appointment> findById(String appId) {
        return repo.findById(appId);
    }

    public int addAppointment(Appointment item) {
        int rows = repo.insert(item);
        if (rows > 0) {
            log.info("Appointment inserted — AppID: " + item.getApp_id() + " Email: " + item.getEmail());
        } else {
            log.warning("Appointment insert returned 0 rows — Email: " + item.getEmail());
        }
        return rows;
    }

    public int updateStatus(String appId, String status) {
        return repo.findById(appId).map(apt -> {
            apt.setStatus(status);
            return repo.update(apt);
        }).orElse(0);
    }

    public List<Appointment> getByStatus(String status) {
        return repo.findByStatus(status);
    }

    public int cancelAppointment(String appId) {
        return repo.findById(appId).map(apt -> {
            apt.setStatus("CANCELLED");
            repo.update(apt);
            slotMapper.releaseSlot(
                apt.getServer_id(),
                apt.getStart_time().toLocalDate(),
                apt.getStart_time()
            );
            log.info("Appointment cancelled and slot released — AppID: " + appId);
            return 1;
        }).orElse(0);
    }
}
