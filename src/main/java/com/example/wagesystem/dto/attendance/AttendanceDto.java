package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Employee;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDto {

    private String employeeName;

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Time workTime;

    private BigDecimal dailyWage;

    private LocalDate workDay;

    private BigDecimal weeklyAllowance;

    private BigDecimal bonus;

    private LocalDate date;

    private String dayOfWeek;

    private Time durationOfWorkTime;
}