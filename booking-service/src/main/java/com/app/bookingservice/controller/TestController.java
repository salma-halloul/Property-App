package com.app.bookingservice.controller;

import com.app.bookingservice.event.BookingCreatedEvent;
import com.app.bookingservice.service.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final BookingEventPublisher eventPublisher;

    @PostMapping("/kafka-event")
    public String testKafkaEvent(@RequestBody BookingCreatedEvent event) {
        eventPublisher.publishBookingCreatedEvent(event);
        return "Événement Kafka envoyé avec succès !";
    }
}