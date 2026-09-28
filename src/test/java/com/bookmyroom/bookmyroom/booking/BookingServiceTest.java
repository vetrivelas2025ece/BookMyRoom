package com.bookmyroom.bookmyroom.booking;

import com.bookmyroom.bookmyroom.exceptions.BookingConflictException;
import com.bookmyroom.bookmyroom.guest.GuestRepository;
import com.bookmyroom.bookmyroom.room.Room;
import com.bookmyroom.bookmyroom.room.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    private BookingRepository bookings;
    private RoomRepository rooms;
    private GuestRepository guests;
    private BookingService service;

    @BeforeEach
    void setup() {
        bookings = mock(BookingRepository.class);
        rooms = mock(RoomRepository.class);
        guests = mock(GuestRepository.class);
        service = new BookingService(bookings, rooms, guests);
    }

    @Test
    void rejectsCheckoutOnCheckinDay() {
        BookingDto dto = new BookingDto();
        dto.setRoomId(1L);
        dto.setGuestId(1L);
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(dto.getCheckInDate());
        assertThrows(IllegalArgumentException.class, () -> service.createBooking(dto));
        verifyNoInteractions(rooms, guests, bookings);
    }

    @Test
    void rejectsOverlappingBooking() {
        BookingDto dto = new BookingDto();
        dto.setRoomId(1L);
        dto.setGuestId(2L);
        dto.setCheckInDate(LocalDate.now().plusDays(2));
        dto.setCheckOutDate(LocalDate.now().plusDays(4));
        Room room = new Room();
        room.setRoomId(1L);
        room.setRatePerNight(new BigDecimal("1000.00"));
        when(rooms.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(guests.findByIdForUpdate(2L)).thenReturn(Optional.of(new com.bookmyroom.bookmyroom.guest.Guest()));
        when(bookings.existsOverlappingBooking(eq(1L), any(), any(), eq(BookingStatus.CONFIRMED)))
                .thenReturn(true);
        assertThrows(BookingConflictException.class, () -> service.createBooking(dto));
        verify(bookings, never()).save(any());
    }
}
