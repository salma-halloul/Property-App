package com.app.bookingservice.service;

import com.app.bookingservice.entity.Booking;
import com.app.bookingservice.enums.BookingStatus;
import com.app.bookingservice.event.BookingCreatedEvent;
import com.app.bookingservice.model.BookingRequestDTO;
import com.app.bookingservice.model.BookingResponseDTO;
import com.app.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;
import com.app.bookingservice.client.PropertyClient;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {


    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;
    private final BookingEventPublisher eventPublisher;

    public BookingService(BookingRepository bookingRepository, PropertyClient propertyClient, BookingEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.propertyClient = propertyClient;
        this.eventPublisher = eventPublisher;
    }

    public ResponseEntity<BookingResponseDTO> createBooking(BookingRequestDTO request, String userId) {

        // 1. Vérifier que la propriété existe via le property-service
        boolean propertyExists = propertyClient.checkPropertyExists(request.getPropertyId());

        if (!propertyExists) {
            return ResponseEntity.status(404)
                    .body(null); // Retourner une réponse 404 si la propriété n'existe pas
        }

        // 2. Convertir DTO → Entity
        Booking booking = new Booking();
        booking.setPropertyId(request.getPropertyId());
        booking.setUserDefinedDate(request.getUserDefinedDate());
        booking.setBookingDate(java.time.LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(userId);  // Le userId injecté depuis JWT

        // 3. Sauvegarder dans la DB
        Booking saved = bookingRepository.save(booking);

        // 4. Publier l'événement Kafka
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(saved.getId());
        event.setPropertyId(saved.getPropertyId());
        event.setUserId(saved.getUserId());
        event.setBookingDate(saved.getBookingDate());
        event.setUserDefinedDate(saved.getUserDefinedDate());
        event.setStatus(saved.getStatus());
        
        eventPublisher.publishBookingCreatedEvent(event);

        // 5. Convertir Entity → ResponseDTO
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(saved.getId());
        response.setPropertyId(saved.getPropertyId());
        response.setBookingDate(saved.getBookingDate());
        response.setUserDefinedDate(saved.getUserDefinedDate());
        response.setStatus(saved.getStatus());
        response.setUserId(saved.getUserId());

        return ResponseEntity.ok(response);
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        return bookingRepository.findById(id).map(booking -> {
            // Mettre à jour uniquement le statut
            booking.setStatus(bookingDetails.getStatus());
            return bookingRepository.save(booking);
        }).orElseThrow(() -> new RuntimeException("Booking not found with id " + id));
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }
}
