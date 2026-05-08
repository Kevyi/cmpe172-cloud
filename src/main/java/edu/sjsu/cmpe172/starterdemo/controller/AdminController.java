package edu.sjsu.cmpe172.starterdemo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import edu.sjsu.cmpe172.starterdemo.model.Availability_Slot;
import edu.sjsu.cmpe172.starterdemo.service.AppointmentService;
import edu.sjsu.cmpe172.starterdemo.service.CloudService;
import edu.sjsu.cmpe172.starterdemo.service.ServerService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentService appointmentService;
    private final AvailabilitySlotMapper slotMapper;
    private final ServerService serverService;
    private final CloudService cloudService;

    public AdminController(AppointmentService appointmentService,
                           AvailabilitySlotMapper slotMapper,
                           ServerService serverService,
                           CloudService cloudService) {
        this.appointmentService = appointmentService;
        this.slotMapper = slotMapper;
        this.serverService = serverService;
        this.cloudService = cloudService;
    }

    private boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("user_role"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        var all = appointmentService.getAllAppointments();
        long pending = all.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
        model.addAttribute("totalAppointments", all.size());
        model.addAttribute("pendingCount", pending);
        model.addAttribute("serverCount", serverService.getAll().size());
        model.addAttribute("activePage", "admin-dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/appointments")
    public String allAppointments(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("activePage", "admin-appointments");
        return "admin/appointments";
    }

    @PostMapping("/appointments/{appId}/approve")
    public String approveAppointment(@PathVariable String appId, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        appointmentService.updateStatus(appId, "CONFIRMED");
        appointmentService.findById(appId).ifPresent(apt ->
            cloudService.notifyUser(apt.getEmail(), "Your appointment " + appId + " has been approved."));
        ra.addFlashAttribute("successMsg", "Appointment approved.");
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/{appId}/deny")
    public String denyAppointment(@PathVariable String appId, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        appointmentService.cancelAppointment(appId);
        appointmentService.findById(appId).ifPresent(apt ->
            cloudService.notifyUser(apt.getEmail(), "Your appointment " + appId + " was denied."));
        ra.addFlashAttribute("successMsg", "Appointment denied.");
        return "redirect:/admin/appointments";
    }

    @GetMapping("/slots")
    public String manageSlots(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("slots", slotMapper.findAll());
        model.addAttribute("servers", serverService.getAll());
        model.addAttribute("newSlot", new Availability_Slot(null, null, true, null, null));
        model.addAttribute("activePage", "admin-slots");
        return "admin/slots";
    }

    @PostMapping("/slots/add")
    public String addSlot(@RequestParam String serverId,
                          @RequestParam String startTime,
                          @RequestParam String endTime,
                          HttpSession session,
                          RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        Availability_Slot slot = new Availability_Slot(serverId, start.toLocalDate(), true, start, end);
        slotMapper.insert(slot);
        ra.addFlashAttribute("successMsg", "Slot added.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/slots/delete")
    public String deleteSlot(@RequestParam String serverId,
                             @RequestParam String startTime,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        LocalDateTime ldt = LocalDateTime.parse(startTime);

        // Hard-delete any appointments referencing this slot (FK would otherwise block the delete).
        appointmentService.findBySlot(serverId, ldt).forEach(apt -> {
            cloudService.notifyUser(apt.getEmail(),
                "Your appointment " + apt.getApp_id() + " was cancelled because the slot was removed by an admin.");
            appointmentService.deleteAppointment(apt.getApp_id());
        });

        slotMapper.delete(serverId, ldt.toLocalDate(), ldt);
        ra.addFlashAttribute("successMsg", "Slot deleted.");
        return "redirect:/admin/slots";
    }

    @GetMapping("/servers")
    public String manageServers(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("servers", serverService.getAll());
        model.addAttribute("activePage", "admin-servers");
        return "admin/servers";
    }

    @PostMapping("/servers/{serverId}/status")
    public String updateServerStatus(@PathVariable String serverId,
                                     @RequestParam String status,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        serverService.updateStatus(serverId, status);
        ra.addFlashAttribute("successMsg", "Server status updated.");
        return "redirect:/admin/servers";
    }
}
