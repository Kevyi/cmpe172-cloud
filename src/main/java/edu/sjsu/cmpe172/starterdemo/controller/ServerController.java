package edu.sjsu.cmpe172.starterdemo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/server")
public class ServerController {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());

    @Value("${homelab.ip}")
    private String ip;

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        String address = ip + "health";
        RestClient client = RestClient.create();

        Map<String, String> msResponse = null;
        try {
            msResponse = client.get()
                .uri(address)
                .retrieve()
                .body(Map.class);
        } catch (Exception e) {
            log.warning("Service-Creation Microservice is down: " + e.getMessage());
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("Springboot", true);
        result.put("Service-Creation-MS", msResponse != null);
        return result;
    }
}
