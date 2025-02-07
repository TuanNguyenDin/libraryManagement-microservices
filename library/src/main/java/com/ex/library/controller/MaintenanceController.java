package com.ex.library.controller;

import com.ex.library.dto.request.MaintenanceRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.response.MaintenanceResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final ApplicationEventPublisher eventPublisher;

    private final ApplicationAvailability availability;

    public MaintenanceController(ApplicationEventPublisher eventPublisher, ApplicationAvailability applicationAvailability) {
        this.eventPublisher = eventPublisher;
        this.availability = applicationAvailability;
    }

    @GetMapping
    public APIResponse<MaintenanceResponse> checkSystemInMaintenance() {
        var lastChangeEvent = availability.getLastChangeEvent(ReadinessState.class);
        log.info("Check System In Maintenance!");
        return APIResponse.<MaintenanceResponse>builder()
                .response(
                        MaintenanceResponse.builder()
                                .maintenanceMode(lastChangeEvent.getState().equals(ReadinessState.REFUSING_TRAFFIC))
                                .from(new Date(lastChangeEvent.getTimestamp()))
                                .build()
                )
                .build();
    }

    @PutMapping
    public APIResponse<Void> initInMaintenance(@NotNull @RequestBody MaintenanceRequest request) {
        AvailabilityChangeEvent.publish(eventPublisher, this, request.isMaintenanceMode() ? ReadinessState.REFUSING_TRAFFIC : ReadinessState.ACCEPTING_TRAFFIC);
        log.info("log maintenance :" + availability.getReadinessState());
        return APIResponse.<Void>builder()
                .message("Server in Maintenance: " + availability.getReadinessState() + " !")
                .build();
    }
}
