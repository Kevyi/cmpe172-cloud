package edu.sjsu.cmpe172.starterdemo.repository;

import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository — kept for reference/fallback.
 * Production code uses AppointmentMapper (JDBC).
 */
@Repository
public class AppointmentRepository {

    private final Map<String, Appointment> store = new HashMap<>();
    private long nextId = 1L;

    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Appointment> findAll(String email) {
        return store.values().stream()
                .filter(appointment -> email.equals(appointment.getEmail()))
                .collect(Collectors.toList());
    }

    public Appointment save(Appointment item) {
        if (item.getApp_id() == null) {
            item.setApp_id(String.valueOf(nextId++));
        }
        store.put(item.getApp_id() + nextId, item);
        nextId++;
        return item;
    }
}
