package com.app.bookingservice.model;

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
public class BookingResponseDTO {

    private Long id;
    private Long propertyId;
    private String userId;
    private LocalDateTime bookingDate;
    private LocalDateTime userDefinedDate;
    private BookingStatus status;
}
