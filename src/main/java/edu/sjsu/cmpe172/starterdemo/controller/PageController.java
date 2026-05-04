package edu.sjsu.cmpe172.starterdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentService;

import java.util.List;

@Controller
public class PageController {

    private final AppointmentService appointmentService;

    public PageController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/appointments")
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("activePage", "appointments");
        return "appointments";
    }

    @GetMapping("/booking")
    public String bookingForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("services", List.of(
            new ServiceOption("svc-nginx",    "Nginx Web Server"),
            new ServiceOption("svc-postgres", "PostgreSQL Database"),
            new ServiceOption("svc-redis",    "Redis Cache")
        ));
        model.addAttribute("activePage", "booking");
        return "booking";
    }

    @PostMapping("/appointments")
    public String create(@ModelAttribute Appointment apt) {
        // TODO: wire to Booking_DomainService for full slot validation
        return "redirect:/appointments?success=true";
    }

    /** Simple DTO for the services dropdown. */
    public record ServiceOption(String id, String name) {}
}
