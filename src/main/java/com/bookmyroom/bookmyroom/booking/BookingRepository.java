
package com.bookmyroom.bookmyroom.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    // Find bookings for a particular guest
    List<Booking> findByGuestGuestId(Long guestId);

    // Find bookings for a particular room
    List<Booking> findByRoomRoomId(Long roomId);

    // Check whether a room has any booking history
    boolean existsByRoomRoomId(Long roomId);

    // Check whether a guest has any booking history
    boolean existsByGuestGuestId(Long guestId);

    // Check for overlapping confirmed bookings
    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.room.roomId = :roomId
            AND b.status = :status
            AND b.checkInDate < :checkOut
            AND b.checkOutDate > :checkIn
            """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("status") BookingStatus status
    );

    // Retrieve bookings overlapping a particular month
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.status IN :statuses
            AND b.checkInDate < :monthEnd
            AND b.checkOutDate > :monthStart
            """)
    List<Booking> findBookingsForMonthlyReport(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd,
            @Param("statuses") List<BookingStatus> statuses
    );
}