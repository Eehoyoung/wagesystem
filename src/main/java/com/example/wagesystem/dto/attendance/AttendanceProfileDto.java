package com.example.wagesystem.dto.attendance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceProfileDto {

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Time workTime;

    private BigDecimal dailyWage;

}
