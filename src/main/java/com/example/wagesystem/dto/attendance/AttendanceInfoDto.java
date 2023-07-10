package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import lombok.*;

import javax.persistence.Column;
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

    private BigDecimal weeklyAllowance;

    private BigDecimal bonus;

    @Builder
    public AttendanceInfoDto(Long attendanceId, Long employeeId, LocalDateTime startTime, LocalDateTime endTime, Time workTime, BigDecimal dailyWage, LocalDate workDay, BigDecimal weeklyAllowance, BigDecimal bonus) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workTime = workTime;
        this.dailyWage = dailyWage;
        this.workDay = workDay;
        this.weeklyAllowance = weeklyAllowance;
        this.bonus = bonus;
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
                .weeklyAllowance(weeklyAllowance)
                .bonus(bonus)
                .build();
    }
}
