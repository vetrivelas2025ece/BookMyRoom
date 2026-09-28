
package com.bookmyroom.bookmyroom.report;

import com.bookmyroom.bookmyroom.booking.*;
import com.bookmyroom.bookmyroom.room.RoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public OccupancyReportDto getMonthlyReport(
            int year, int month) {

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.plusMonths(1).atDay(1);

        long totalRooms = roomRepository.count();

        long availableRoomNights =
                totalRooms * yearMonth.lengthOfMonth();

        List<Booking> bookings =
                bookingRepository.findBookingsForMonthlyReport(
                        monthStart,
                        monthEnd,
                        List.of(
                                BookingStatus.CONFIRMED,
                                BookingStatus.COMPLETED
                        )
                );

        long occupiedRoomNights = 0;

        BigDecimal bookingRevenue = BigDecimal.ZERO;

        for (Booking booking : bookings) {

            LocalDate effectiveStart =
                    booking.getCheckInDate()
                            .isAfter(monthStart)
                            ? booking.getCheckInDate()
                            : monthStart;

            LocalDate effectiveEnd =
                    booking.getCheckOutDate()
                            .isBefore(monthEnd)
                            ? booking.getCheckOutDate()
                            : monthEnd;

            long nights = ChronoUnit.DAYS.between(
                    effectiveStart,
                    effectiveEnd
            );

            occupiedRoomNights += nights;

            long totalNights = ChronoUnit.DAYS.between(
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()
            );

            BigDecimal monthlyRevenue =
                    booking.getTotalCost()
                            .multiply(BigDecimal.valueOf(nights))
                            .divide(
                                    BigDecimal.valueOf(totalNights),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            bookingRevenue =
                    bookingRevenue.add(monthlyRevenue);
        }

        double occupancyPercentage = 0;

        if (availableRoomNights > 0) {
            occupancyPercentage =
                    (occupiedRoomNights * 100.0)
                            / availableRoomNights;
        }

        return new OccupancyReportDto(
                year,
                month,
                totalRooms,
                bookings.size(),
                availableRoomNights,
                occupiedRoomNights,
                Math.round(occupancyPercentage * 100.0) / 100.0,
                bookingRevenue
        );
    }
}