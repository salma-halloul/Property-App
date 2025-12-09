package com.app.bookingservice.event;

import com.app.bookingservice.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    
    private Long bookingId;
    private Long propertyId;
    private String userId;
    private LocalDateTime bookingDate;
    private LocalDateTime userDefinedDate;
    private BookingStatus status;
    private String eventType = "BOOKING_CREATED";
    private LocalDateTime timestamp = LocalDateTime.now();
}