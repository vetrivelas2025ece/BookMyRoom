
package com.bookmyroom.bookmyroom.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BookingResponseDto {

    private Long bookingId;
    private Long roomId;
    private String roomNumber;
    private Long guestId;
    private String guestName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalCost;
    private BookingStatus status;
}