package edu.sjsu.cmpe172.starterdemo.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import edu.sjsu.cmpe172.starterdemo.service.CloudService;

@RestController
@RequestMapping("/api/cloud")
public class CloudController {

    private static final Logger log = Logger.getLogger(CloudController.class.getName());
    private final CloudService service;

    @Value("${homelab.ip}")
    private String ip;

    private static int launchFailures = 0;
    private static int launchSuccess = 0;

    public record StartAppRequest(
        String appointmentId,
        int replicas,
        List<Container> containers
    ) {}

    public record Container(
        String image,
        String name,
        List<String> envVars,
        List<String> secrets,
        int port
    ) {}

    public CloudController(CloudService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public String startService() {
        String address = ip + "apps/start";
        RestClient client = RestClient.create();
        String appID = "temp";

        StartAppRequest request = new StartAppRequest(
            "abc123",
            1,
            List.of(new Container("nginx:latest", "web", List.of(), List.of(), 80))
        );

        try {
            String response = client.post()
                .uri(address)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

            launchSuccess++;
            return response;
        } catch (Exception e) {
            log.warning("Couldn't deploy instance for appID: " + appID + " — " + e.getMessage());
            launchFailures++;
        }

        log.info("Cloud deployment success rate: " + (launchSuccess * 100 / Math.max(1, launchFailures + launchSuccess)) + "%");
        return null;
    }

    @PostMapping("/successRate")
    public int getSuccessRate() {
        int total = launchFailures + launchSuccess;
        return total == 0 ? 0 : launchSuccess * 100 / total;
    }
}
