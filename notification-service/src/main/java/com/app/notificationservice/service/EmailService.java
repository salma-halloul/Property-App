package com.app.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.admin.email:salmahalloul02@gmail.com}")
    private String adminEmail;

    @Value("${notification.from.email:noreply@property-app.com}")
    private String fromEmail;

    public void sendBookingNotificationToAdmin(Long bookingId, Long propertyId, String userId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("Nouvelle réservation - Property App");
            
            String emailBody = String.format(
                "Bonjour,\n\n" +
                "Une nouvelle réservation vient d'être créée dans l'application Property App.\n\n" +
                "Détails de la réservation :\n" +
                "- ID de la réservation : %d\n" +
                "- ID de la propriété : %d\n" +
                "- ID utilisateur : %s\n\n" +
                "Veuillez vérifier et traiter cette réservation.\n\n" +
                "Cordialement,\n" +
                "L'équipe Property App",
                bookingId, propertyId, userId
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Email de notification envoyé avec succès à l'admin pour la réservation ID: {}", bookingId);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de notification pour la réservation ID: {}", bookingId, e);
        }
    }
}