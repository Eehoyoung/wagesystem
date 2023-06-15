package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AttendanceInfoDto {

    private Long attendanceId;

    private Long employeeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Time workTime;

    private BigDecimal dailyWage;

    private LocalDate workDay;

    @Builder
    public AttendanceInfoDto(Long attendanceId, Long employeeId, LocalDateTime startTime, LocalDateTime endTime, Time workTime, BigDecimal dailyWage, LocalDate workDay) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workTime = workTime;
        this.dailyWage = dailyWage;
        this.workDay = workDay;
    }

    public Attendance toEntity(Employee employee) {
        return Attendance.builder()
                .attendanceId(attendanceId)
                .employee(employee)
                .startTime(startTime)
                .endTime(endTime)
                .workTime(workTime)
                .dailyWage(dailyWage)
                .workDay(workDay)
                .build();
    }
}
