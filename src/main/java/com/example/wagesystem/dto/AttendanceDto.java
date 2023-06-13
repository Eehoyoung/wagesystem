package com.example.wagesystem.dto;

import com.example.wagesystem.domain.Employee;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
public class AttendanceDto {

    private Employee employee;

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public AttendanceDto(Long attendanceId, Long employeeId,LocalDateTime startTime, LocalDateTime endTime, Employee employee) {
        this.attendanceId = attendanceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
//        this.employeeId = employeeId;
    }

}