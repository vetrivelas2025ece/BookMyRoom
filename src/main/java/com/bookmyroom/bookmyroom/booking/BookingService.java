
package com.bookmyroom.bookmyroom.booking;

import com.bookmyroom.bookmyroom.room.Room;
import com.bookmyroom.bookmyroom.room.RoomRepository;

import com.bookmyroom.bookmyroom.guest.Guest;
import com.bookmyroom.bookmyroom.guest.GuestRepository;

import com.bookmyroom.bookmyroom.exceptions.ResourceNotFoundException;
import com.bookmyroom.bookmyroom.exceptions.BookingConflictException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    // Convert Booking entity into response DTO
    private BookingResponseDto toDto(Booking booking) {

        return new BookingResponseDto(
                booking.getBookingId(),
                booking.getRoom().getRoomId(),
                booking.getRoom().getRoomNumber(),
                booking.getGuest().getGuestId(),
                booking.getGuest().getGuestName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalCost(),
                booking.getStatus()
        );
    }

    // 1. Create a booking
    @Transactional
    public BookingResponseDto createBooking(BookingDto request) {

        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();

        // Validate check-in date
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past"
            );
        }

        // Validate check-out date
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date"
            );
        }

        // Lock the room to prevent simultaneous double bookings
        Room room = roomRepository
                .findByIdForUpdate(request.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: "
                                        + request.getRoomId()
                        )
                );

        // Find the guest
        Guest guest = guestRepository
                .findByIdForUpdate(request.getGuestId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Guest not found: "
                                        + request.getGuestId()
                        )
                );

        // Check for overlapping bookings
        boolean occupied =
                bookingRepository.existsOverlappingBooking(
                        room.getRoomId(),
                        checkIn,
                        checkOut,
                        BookingStatus.CONFIRMED
                );

        if (occupied) {
            throw new BookingConflictException(
                    "Room is already booked for these dates"
            );
        }

        // Calculate total nights
        long nights = ChronoUnit.DAYS.between(
                checkIn,
                checkOut
        );

        // Calculate total booking cost
        BigDecimal totalCost =
                room.getRatePerNight()
                        .multiply(BigDecimal.valueOf(nights));

        // Create booking entity
        Booking booking = new Booking();

        booking.setRoom(room);
        booking.setGuest(guest);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalCost(totalCost);
        booking.setStatus(BookingStatus.CONFIRMED);

        // Save booking
        Booking savedBooking =
                bookingRepository.save(booking);

        return toDto(savedBooking);
    }

    // 2. Get all bookings
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {

        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 3. Get booking by ID
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + id
                        )
                );

        return toDto(booking);
    }

    // 4. Cancel a booking
    @Transactional
    public BookingResponseDto cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + id
                        )
                );

        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new BookingConflictException(
                    "Only confirmed bookings can be cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return toDto(savedBooking);
    }

    // 5. Complete a booking after checkout
    @Transactional
    public BookingResponseDto completeBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + id
                        )
                );

        // Only confirmed bookings can be completed
        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new BookingConflictException(
                    "Only confirmed bookings can be completed"
            );
        }

        // Prevent completion before checkout
        if (LocalDate.now()
                .isBefore(booking.getCheckOutDate())) {

            throw new BookingConflictException(
                    "Booking cannot be completed before checkout date"
            );
        }

        booking.setStatus(BookingStatus.COMPLETED);

        Booking savedBooking =
                bookingRepository.save(booking);

        return toDto(savedBooking);
    }
}