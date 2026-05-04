package edu.sjsu.cmpe172.starterdemo.service;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.mapper.AppointmentMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class AppointmentService {

    private static final Logger log = Logger.getLogger(AppointmentService.class.getName());
    private final AppointmentMapper repo;

    public AppointmentService(AppointmentMapper repo) {
        this.repo = repo;
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    public List<Appointment> getAllAppointments(String email) {
        return repo.findByEmail(email);
    }

    public int addAppointment(Appointment item) {
        int rows = repo.insert(item);
        if (rows > 0) {
            log.info("Appointment successfully inserted — AppID: " + item.getApp_id() + " Email: " + item.getEmail());
        } else {
            log.warning("Appointment insert returned 0 rows — Email: " + item.getEmail() + " ServerID: " + item.getServer_id());
        }
        return rows;
    }

    public int cancelAppointment(String appId) {
        return repo.delete(appId);
    }
}
