package com.example.wagesystem.dto;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import lombok.*;

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

    @Builder
    public AttendanceInfoDto(Long attendanceId, Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Attendance toEntity(Employee employee) {
        return Attendance.builder()
                .attendanceId(attendanceId)
                .employee(employee)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
