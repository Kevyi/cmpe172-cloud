package edu.sjsu.cmpe172.starterdemo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {
    private String app_id;
    private String server_id;
    private String service_id;
    private String email;
    private String status;
    private LocalDate created_at;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public Appointment() {}

    public Appointment(String app_id, String server_id, String service_id, String email,
                       String status, LocalDate created_at, LocalDateTime start_time, LocalDateTime end_time) {
        this.app_id = app_id;
        this.server_id = server_id;
        this.service_id = service_id;
        this.email = email;
        this.status = status;
        this.created_at = created_at;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getApp_id() { return app_id; }
    public void setApp_id(String app_id) { this.app_id = app_id; }

    public String getServer_id() { return server_id; }
    public void setServer_id(String server_id) { this.server_id = server_id; }

    public String getService_id() { return service_id; }
    public void setService_id(String service_id) { this.service_id = service_id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreated_at() { return created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }

    public LocalDateTime getStart_time() { return start_time; }
    public void setStart_time(LocalDateTime start_time) { this.start_time = start_time; }

    public LocalDateTime getEnd_time() { return end_time; }
    public void setEnd_time(LocalDateTime end_time) { this.end_time = end_time; }
}
