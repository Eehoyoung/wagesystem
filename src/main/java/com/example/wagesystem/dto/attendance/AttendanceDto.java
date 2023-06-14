package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Employee;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDto {

    private Employee employee;

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Time workTime;

    private BigDecimal dailyWage;


    public AttendanceDto(Long attendanceId, Time workTime, LocalDateTime startTime, LocalDateTime endTime, Employee employee, BigDecimal dailyWage) {
        this.attendanceId = attendanceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
        this.workTime = workTime;
        this.dailyWage = dailyWage;
    }

}