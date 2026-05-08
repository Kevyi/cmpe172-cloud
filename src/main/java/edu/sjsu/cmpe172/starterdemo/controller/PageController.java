package edu.sjsu.cmpe172.starterdemo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.service.AppServiceService;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentService;
import edu.sjsu.cmpe172.starterdemo.service.ServerService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PageController {

    private final AvailabilitySlotMapper slotMapper;
    private final AppServiceService appServiceService;
    private final ServerService serverService;
    private final AppointmentService appointmentService;

    public PageController(AvailabilitySlotMapper slotMapper,
                          AppServiceService appServiceService,
                          ServerService serverService,
                          AppointmentService appointmentService) {
        this.slotMapper = slotMapper;
        this.appServiceService = appServiceService;
        this.serverService = serverService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String role  = (String) session.getAttribute("user_role");
        String email = (String) session.getAttribute("user_email");

        if ("admin".equals(role)) {
            return "redirect:/admin/dashboard";
        }

        if (email != null) {
            List<Appointment> appointments = appointmentService.getAllAppointments(email);

            long active  = appointments.stream().filter(a -> "RUNNING".equals(a.getStatus())).count();
            long pending = appointments.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
            long past    = appointments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) || "CANCELLED".equals(a.getStatus()))
                .count();

            List<Appointment> upcoming = appointments.stream()
                .filter(a -> List.of("PENDING", "CONFIRMED", "RUNNING").contains(a.getStatus()))
                .sorted(Comparator.comparing(Appointment::getStart_time))
                .collect(Collectors.toList());

            Map<String, String> serviceNames = appServiceService.getAll().stream()
                .collect(Collectors.toMap(
                    svc -> String.valueOf(svc.getService_id()),
                    svc -> svc.getName()
                ));

            long onlineServers = serverService.getAll().stream()
                .filter(s -> "online".equals(s.getStatus()))
                .count();

            model.addAttribute("activeSlots", active);
            model.addAttribute("pendingCount", pending);
            model.addAttribute("pastBookings", past);
            model.addAttribute("upcomingAppointments", upcoming);
            model.addAttribute("serviceNames", serviceNames);
            model.addAttribute("onlineServers", onlineServers);
        }

        return "index";
    }

    @GetMapping("/slots")
    public String browseSlots(Model model) {
        model.addAttribute("slots", slotMapper.findAllAvailable());
        model.addAttribute("servers", serverService.getAll());
        model.addAttribute("activePage", "slots");
        return "slots";
    }

    @GetMapping("/booking")
    public String bookingForm(@RequestParam(required = false) String serverId,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("services", appServiceService.getAll());
        model.addAttribute("servers", serverService.getAll());
        model.addAttribute("preselectedServerId", serverId);
        model.addAttribute("preselectedStartTime", startTime);
        model.addAttribute("preselectedEndTime", endTime);
        model.addAttribute("activePage", "booking");
        return "booking";
    }

    @GetMapping("/confirmation")
    public String confirmationPage(Model model) {

        //Protector if user decides to go to /confirmation.
        if (!model.containsAttribute("confirmedAppointment")) {
            return "redirect:/unavailable";
        }
        return "confirmation";
    }

    @GetMapping("/unavailable")
    public String unavailablePage() {
        return "unavailable";
    }
}
