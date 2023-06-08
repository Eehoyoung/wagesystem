package com.example.absenteeismerp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal dailyWage;

    private BigDecimal weeklyAllowance;
}