package com.app.bookingservice.service;

import com.app.bookingservice.entity.Booking;
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

    public BookingService(BookingRepository bookingRepository, PropertyClient propertyClient) {
        this.bookingRepository = bookingRepository;
        this.propertyClient = propertyClient;
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
        booking.setStatus("PENDING");
        booking.setUserId(userId);  // Le userId injecté depuis JWT

        // 3. Sauvegarder dans la DB
        Booking saved = bookingRepository.save(booking);

        // 4. Convertir Entity → ResponseDTO
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
            booking.setPropertyId(bookingDetails.getPropertyId());
            booking.setUserId(bookingDetails.getUserId());
            booking.setBookingDate(bookingDetails.getBookingDate());
            booking.setStatus(bookingDetails.getStatus());
            return bookingRepository.save(booking);
        }).orElseThrow(() -> new RuntimeException("Booking not found with id " + id));
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }
}
