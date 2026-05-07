package edu.sjsu.cmpe172.starterdemo.controller;

import org.springframework.web.bind.annotation.*;

import edu.sjsu.cmpe172.starterdemo.service.CloudService;
import edu.sjsu.cmpe172.starterdemo.service.CloudService.PodResponse;
import edu.sjsu.cmpe172.starterdemo.service.CloudService.StartAppRequest;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cloud")
public class CloudController {

    private static final Logger log = Logger.getLogger(CloudController.class.getName());
    private final CloudService service;

    private static int launchFailures = 0;
    private static int launchSuccess  = 0;

    public CloudController(CloudService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public PodResponse startService(@RequestBody StartAppRequest req) {
        PodResponse response = service.startApp(req);
        if (response.success()) launchSuccess++;
        else launchFailures++;
        log.info("Cloud success rate: " + getSuccessRate() + "%");
        return response;
    }

    @DeleteMapping("/stop")
    public PodResponse stopService(@RequestParam String appointmentId) {
        return service.stopApp(appointmentId);
    }

    @GetMapping("/successRate")
    public int getSuccessRate() {
        int total = launchFailures + launchSuccess;
        return total == 0 ? 0 : launchSuccess * 100 / total;
    }
}
