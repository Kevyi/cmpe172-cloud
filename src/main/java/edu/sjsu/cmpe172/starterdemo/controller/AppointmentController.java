package edu.sjsu.cmpe172.starterdemo.controller;

import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.model.Availability_Slot;
import edu.sjsu.cmpe172.starterdemo.service.AppServiceService;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentScheduler;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentService;
import edu.sjsu.cmpe172.starterdemo.service.Booking_DomainService;
import edu.sjsu.cmpe172.starterdemo.service.CloudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final Booking_DomainService bookingDomainService;
    private final CloudService cloudService;
    private final AvailabilitySlotMapper slotMapper;
    private final AppServiceService appServiceService;

    private static final Logger log = Logger.getLogger(AppointmentController.class.getName());

    public AppointmentController(AppointmentService service,
                                 Booking_DomainService bookingDomainService,
                                 CloudService cloudService,
                                 AvailabilitySlotMapper slotMapper,
                                 AppServiceService appServiceService) {
        this.service = service;
        this.bookingDomainService = bookingDomainService;
        this.cloudService = cloudService;
        this.slotMapper = slotMapper;
        this.appServiceService = appServiceService;
    }

    // ── MVC page mappings ─────────────────────────────────────────────────────

    @GetMapping
    public String list(Model model, HttpSession session) {
        String email = (String) session.getAttribute("user_email");
        String role  = (String) session.getAttribute("user_role");
        List<Appointment> apts = "admin".equals(role)
            ? service.getAllAppointments()
            : service.getAllAppointments(email);
        model.addAttribute("appointments", apts);
        model.addAttribute("activePage", "appointments");
        return "appointments";
    }

    @PostMapping
    public String create(@RequestParam String serverId,
                         @RequestParam String startTime,
                         @RequestParam String endTime,
                         @RequestParam String serviceId,
                         @RequestParam(required = false) String customName,
                         @RequestParam(required = false) String customDescription,
                         @RequestParam(required = false) String customDockerImage,
                         HttpSession session,
                         RedirectAttributes ra) {
        String email = (String) session.getAttribute("user_email");

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end   = LocalDateTime.parse(endTime);

        String resolvedServiceId = serviceId;
        String resolvedServiceName;

        if ("custom".equals(serviceId)) {
            if (customName == null || customName.isBlank()
                    || customDockerImage == null || customDockerImage.isBlank()) {
                ra.addFlashAttribute("errorMsg", "Custom service requires a name and Docker image.");
                ra.addFlashAttribute("preselectedServerId", serverId);
                ra.addFlashAttribute("preselectedStartTime", startTime);
                ra.addFlashAttribute("preselectedEndTime", endTime);
                return "redirect:/booking";
            }
            var created = appServiceService.create(
                customName.trim(),
                customDescription != null ? customDescription.trim() : "",
                customDockerImage.trim()
            );
            resolvedServiceId   = String.valueOf(created.getService_id());
            resolvedServiceName = created.getName();
        } else {
            resolvedServiceName = appServiceService.getById(Integer.parseInt(serviceId))
                .map(svc -> svc.getName())
                .orElse(serviceId);
        }

        Availability_Slot slot = slotMapper.findAppointment(serverId, start.toLocalDate(), start);

        Appointment apt = new Appointment(
            UUID.randomUUID().toString(),
            serverId,
            resolvedServiceId,
            email,
            "PENDING",
            LocalDate.now(),
            start,
            end
        );

        if (!slotMapper.isSlotInFuture(serverId, start)) {
            log.info("Failed to make appointment due to start-time before current-time.");
            return "redirect:/unavailable";
        }
        
        boolean booked = bookingDomainService.bookAppointment(slot, apt);
        
        if (!booked) {
            log.info("Can't make appointment due to slot being taken.");
            return "redirect:/unavailable";
        }


        cloudService.notifyUser(email, "Your appointment " + apt.getApp_id() + " is booked and pending admin approval.");
        ra.addFlashAttribute("confirmedAppointment", apt);
        ra.addFlashAttribute("confirmedServiceName", resolvedServiceName);
        return "redirect:/confirmation";
    }

    @GetMapping("/{appId}/reschedule")
    public String rescheduleForm(@PathVariable String appId, Model model, HttpSession session) {
        String email = (String) session.getAttribute("user_email");
        Optional<Appointment> opt = service.findById(appId);
        if (opt.isEmpty() || !opt.get().getEmail().equals(email)) {
            return "redirect:/appointments";
        }
        model.addAttribute("appointment", opt.get());
        model.addAttribute("availableSlots", slotMapper.findAllAvailable());
        model.addAttribute("activePage", "appointments");
        return "reschedule";
    }

    @PostMapping("/{appId}/reschedule")
    public String reschedule(@PathVariable String appId,
                             @RequestParam String slotKey,
                             HttpSession session,
                             RedirectAttributes ra) {
        String email = (String) session.getAttribute("user_email");
        Optional<Appointment> opt = service.findById(appId);
        if (opt.isEmpty() || !opt.get().getEmail().equals(email)) {
            return "redirect:/appointments";
        }

        String[] parts = slotKey.split("\\|", 2);
        if (parts.length != 2) {
            ra.addFlashAttribute("errorMsg", "Invalid slot selection.");
            return "redirect:/appointments/" + appId + "/reschedule";
        }
        String newServerId = parts[0];
        LocalDateTime newStart = LocalDateTime.parse(parts[1]);

        boolean ok = bookingDomainService.rescheduleAppointment(appId, newServerId, newStart);
        if (!ok) {
            ra.addFlashAttribute("errorMsg", "That slot is no longer available. Please choose another.");
            return "redirect:/appointments/" + appId + "/reschedule";
        }

        cloudService.notifyUser(email, "Your appointment " + appId + " has been rescheduled.");
        ra.addFlashAttribute("successMsg", "Appointment rescheduled successfully.");
        return "redirect:/appointments";
    }

    // ── REST API ──────────────────────────────────────────────────────────────

    @ResponseBody
    @GetMapping("/all")
    public List<Appointment> getAppointments() {
        return service.getAllAppointments();
    }

    @ResponseBody
    @GetMapping("/user/{emailID}")
    public List<Appointment> getUserAppointments(@PathVariable String emailID) {
        return service.getAllAppointments(emailID);
    }

    @ResponseBody
    @PostMapping("/{appId}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable String appId) {
        service.findById(appId).ifPresent(apt ->
            cloudService.notifyUser(apt.getEmail(), "Your appointment " + appId + " has been cancelled."));
        int rows = service.cancelAppointment(appId);
        return rows > 0 ? ResponseEntity.ok("cancelled") : ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping(value = "/{appId}/reschedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rescheduleAppointment(@PathVariable String appId,
                                                        @RequestBody RescheduleRequest req) {
        LocalDateTime newStart = LocalDateTime.parse(req.newStartTime());
        boolean ok = bookingDomainService.rescheduleAppointment(appId, req.newServerId(), newStart);
        if (!ok) return ResponseEntity.status(409).body("slot unavailable");
        service.findById(appId).ifPresent(apt ->
            cloudService.notifyUser(apt.getEmail(), "Your appointment " + appId + " has been rescheduled."));
        return ResponseEntity.ok("rescheduled");
    }

    public record RescheduleRequest(String newServerId, String newStartTime) {}
}
