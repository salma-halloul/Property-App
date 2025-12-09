package com.app.notificationservice.event;

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
    private String status;
    private String eventType;
    private LocalDateTime timestamp;
}