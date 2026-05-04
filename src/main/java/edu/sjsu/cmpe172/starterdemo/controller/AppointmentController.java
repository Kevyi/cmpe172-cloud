package edu.sjsu.cmpe172.starterdemo.controller;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping("/getAll")
    public List<Appointment> getAppointments() {
        return service.getAllAppointments();
    }

    @GetMapping("/get/{emailID}")
    public List<Appointment> getUserAppointments(@PathVariable String emailID) {
        return service.getAllAppointments(emailID);
    }

    @PostMapping("/create")
    public int createAppointment(@RequestBody Appointment app) {
        return service.addAppointment(app);
    }

    @PostMapping("/{appId}/cancel")
    public int cancelAppointment(@PathVariable String appId) {
        return service.cancelAppointment(appId);
    }
}
