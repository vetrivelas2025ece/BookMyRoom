
package com.bookmyroom.bookmyroom.booking;

import com.bookmyroom.bookmyroom.room.Room;
import com.bookmyroom.bookmyroom.guest.Guest;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "room_id",
            nullable = false
    )
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "guest_id",
            nullable = false
    )
    private Guest guest;

    @NotNull
    @Column(nullable = false)
    private LocalDate checkInDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}