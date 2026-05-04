package edu.sjsu.cmpe172.starterdemo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Availability_Slot {
    private String server_id;
    private LocalDate date;
    private Boolean status; // True = Available, False = Booked
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public Availability_Slot(String server_id, LocalDate date, Boolean status,
                              LocalDateTime start_time, LocalDateTime end_time) {
        this.server_id = server_id;
        this.date = date;
        this.status = status;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getServer_id() { return server_id; }
    public void setServer_id(String server_id) { this.server_id = server_id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public LocalDateTime getStart_time() { return start_time; }
    public void setStart_time(LocalDateTime start_time) { this.start_time = start_time; }

    public LocalDateTime getEnd_time() { return end_time; }
    public void setEnd_time(LocalDateTime end_time) { this.end_time = end_time; }
}
