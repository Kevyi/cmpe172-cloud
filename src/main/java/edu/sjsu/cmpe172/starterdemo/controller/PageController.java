package edu.sjsu.cmpe172.starterdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.service.AppServiceService;
import edu.sjsu.cmpe172.starterdemo.service.ServerService;

@Controller
public class PageController {

    private final AvailabilitySlotMapper slotMapper;
    private final AppServiceService appServiceService;
    private final ServerService serverService;

    public PageController(AvailabilitySlotMapper slotMapper,
                          AppServiceService appServiceService,
                          ServerService serverService) {
        this.slotMapper = slotMapper;
        this.appServiceService = appServiceService;
        this.serverService = serverService;
    }

    @GetMapping("/")
    public String home() {
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
        if (!model.containsAttribute("confirmedAppointment")) {
            return "redirect:/appointments";
        }
        return "confirmation";
    }
}
