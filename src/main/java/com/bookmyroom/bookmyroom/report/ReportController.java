
package com.bookmyroom.bookmyroom.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<OccupancyReportDto> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {

        if (year < 2000 || year > 2100
                || month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                reportService.getMonthlyReport(year, month)
        );
    }
}