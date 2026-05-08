package edu.sjsu.cmpe172.starterdemo.service;

import edu.sjsu.cmpe172.starterdemo.mapper.AvailabilitySlotMapper;
import edu.sjsu.cmpe172.starterdemo.model.Appointment;
import edu.sjsu.cmpe172.starterdemo.service.CloudService.Container;
import edu.sjsu.cmpe172.starterdemo.service.CloudService.PodResponse;
import edu.sjsu.cmpe172.starterdemo.service.CloudService.StartAppRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Component
public class AppointmentScheduler {

    private static final Logger log = Logger.getLogger(AppointmentScheduler.class.getName());

    private final AppointmentService appointmentService;
    private final AppServiceService appServiceService;
    private final CloudService cloudService;
    private final AvailabilitySlotMapper slotMapper;

    public AppointmentScheduler(AppointmentService appointmentService,
                                AppServiceService appServiceService,
                                CloudService cloudService,
                                AvailabilitySlotMapper slotMapper) {
        this.appointmentService = appointmentService;
        this.appServiceService = appServiceService;
        this.cloudService = cloudService;
        this.slotMapper = slotMapper;
    }

    @Scheduled(fixedRate = 60_000)
    public void syncDeployments() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[Scheduler] Running deployment sync at " + now);

        startDueAppointments(now);
        stopExpiredAppointments(now);
        purgeExpiredSlots(now);
    }

    private void startDueAppointments(LocalDateTime now) {
        List<Appointment> due = appointmentService.getByStatus("CONFIRMED").stream()
            .filter(apt -> !apt.getStart_time().isAfter(now))
            .toList();

        for (Appointment apt : due) {
            String image = resolveDockerImage(apt.getService_id());
            StartAppRequest req = new StartAppRequest(
                apt.getApp_id(),
                1,
                List.of(new Container(image, "app", List.of(), List.of(), 80))
            );

            PodResponse response = cloudService.startApp(req);
            if (response.success()) {
                appointmentService.updateStatus(apt.getApp_id(), "RUNNING");
                log.info("[Scheduler] Started app for appointment " + apt.getApp_id());
            } else {
                log.warning("[Scheduler] Failed to start app for " + apt.getApp_id() + ": " + response.message());
            }
        }
    }

    private void stopExpiredAppointments(LocalDateTime now) {
        List<Appointment> expired = appointmentService.getByStatus("RUNNING").stream()
            .filter(apt -> !apt.getEnd_time().isAfter(now))
            .toList();

        for (Appointment apt : expired) {
            PodResponse response = cloudService.stopApp(apt.getApp_id());
            if (response.success()) {
                appointmentService.updateStatus(apt.getApp_id(), "COMPLETED");
                log.info("[Scheduler] Stopped app for appointment " + apt.getApp_id());
            } else {
                log.warning("[Scheduler] Failed to stop app for " + apt.getApp_id() + ": " + response.message());
            }
        }
    }

    private void purgeExpiredSlots(LocalDateTime now) {
        int removed = slotMapper.deleteExpiredAvailable();
        if (removed > 0) {
            log.info("[Scheduler] Purged " + removed + " expired available slot(s)");
        }
    }

    private String resolveDockerImage(String serviceId) {
        try {
            return appServiceService.getById(Integer.parseInt(serviceId))
                .map(svc -> svc.getDocker_image())
                .orElse("nginx:latest");
        } catch (NumberFormatException e) {
            return "nginx:latest";
        }
    }
}
