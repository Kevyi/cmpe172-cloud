package edu.sjsu.cmpe172.starterdemo.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CloudService {

    private static final Logger log = Logger.getLogger(CloudService.class.getName());

    @Value("${homelab.ip}")
    private String homelabIp;

    // ── Homelab API request/response types ────────────────────────────────────

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

    public record StopAppRequest(String appointmentId) {}

    public record PodResponse(boolean success, String message, int podsCreated) {}

    // ── Homelab operations ─────────────────────────────────────────────────────

    public PodResponse startApp(StartAppRequest req) {
        log.info("Starting app for appointmentId: " + req.appointmentId());
        try {
            PodResponse response = RestClient.create()
                .post()
                .uri(homelabIp + "apps/start")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(PodResponse.class);
            log.info("App started: " + (response != null ? response.message() : "no response"));
            return response;
        } catch (Exception e) {
            log.warning("Failed to start app for " + req.appointmentId() + ": " + e.getMessage());
            return new PodResponse(false, "Homelab unreachable: " + e.getMessage(), 0);
        }
    }

    public PodResponse stopApp(String appointmentId) {
        log.info("Stopping app for appointmentId: " + appointmentId);
        try {
            PodResponse response = RestClient.create()
                .method(HttpMethod.DELETE)
                .uri(homelabIp + "apps/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new StopAppRequest(appointmentId))
                .retrieve()
                .body(PodResponse.class);
            log.info("App stopped: " + (response != null ? response.message() : "no response"));
            return response;
        } catch (Exception e) {
            log.warning("Failed to stop app for " + appointmentId + ": " + e.getMessage());
            return new PodResponse(false, "Homelab unreachable: " + e.getMessage(), 0);
        }
    }

    //Mock notification (log only) ───────────────────────────────────────────
    public void notifyUser(String email, String message) {
        log.info("[NOTIFY] To: " + email + " | " + message);
    }
}
