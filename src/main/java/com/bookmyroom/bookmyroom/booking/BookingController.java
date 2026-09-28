
package com.bookmyroom.bookmyroom.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 1. Create a new booking
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    // 2. Get all bookings
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings());
    }

    // 3. Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.getBookingById(id));
    }

    // 4. Cancel a booking
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(id));
    }

    // Complete a confirmed booking on or after its checkout date
    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingResponseDto> completeBooking(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.completeBooking(id));
    }
}