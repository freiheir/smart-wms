package com.smart.wmserp.api;

import com.smart.wmserp.common.dto.DashboardReport;
import com.smart.wmserp.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 경영 대시보드 리포트 API
     * 
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReport> getDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        DashboardReport report = reportService.getDashboardReport(start, end);
        return ResponseEntity.ok(report);
    }
}
