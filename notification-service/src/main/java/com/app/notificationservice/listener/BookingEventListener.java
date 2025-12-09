package com.app.notificationservice.listener;

import com.app.notificationservice.event.BookingCreatedEvent;
import com.app.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topics.booking-events:booking-events}", groupId = "${kafka.consumer.group-id:notification-service}")
    public void handleBookingCreatedEvent(
            @Payload BookingCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Événement reçu du topic: {} [partition: {}, offset: {}] - Booking ID: {}", 
                topic, partition, offset, event.getBookingId());

        try {
            if ("BOOKING_CREATED".equals(event.getEventType())) {
                log.info("Traitement de l'événement BOOKING_CREATED pour la réservation ID: {}", 
                        event.getBookingId());
                
                // Envoyer l'email de notification à l'admin
                emailService.sendBookingNotificationToAdmin(
                    event.getBookingId(), 
                    event.getPropertyId(), 
                    event.getUserId()
                );
                
                log.info("Notification email envoyée avec succès pour la réservation ID: {}", 
                        event.getBookingId());
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement BOOKING_CREATED pour la réservation ID: {}", 
                    event.getBookingId(), e);
            // Dans un environnement de production, vous pourriez implémenter une logique de retry
            // ou envoyer l'événement vers une Dead Letter Queue
        }
    }
}