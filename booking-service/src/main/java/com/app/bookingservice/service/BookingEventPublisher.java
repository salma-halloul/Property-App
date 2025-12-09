package com.app.bookingservice.service;

import com.app.bookingservice.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.booking-events:booking-events}")
    private String bookingEventsTopic;

    public void publishBookingCreatedEvent(BookingCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(bookingEventsTopic, event.getBookingId().toString(), event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Événement BOOKING_CREATED publié avec succès: bookingId={}, topic={}", 
                            event.getBookingId(), bookingEventsTopic);
                } else {
                    log.error("Échec de la publication de l'événement BOOKING_CREATED: bookingId={}", 
                            event.getBookingId(), exception);
                }
            });
        } catch (Exception e) {
            log.error("Erreur lors de la publication de l'événement: {}", e.getMessage(), e);
        }
    }
}