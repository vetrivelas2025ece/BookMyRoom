
package com.bookmyroom.bookmyroom.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OccupancyReportDto {

    private int year;
    private int month;

    private long totalRooms;
    private long totalBookings;

    private long availableRoomNights;
    private long occupiedRoomNights;

    private double occupancyPercentage;

    private BigDecimal bookingRevenue;
}