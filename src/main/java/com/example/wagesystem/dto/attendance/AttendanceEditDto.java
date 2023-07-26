package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceEditDto {

    private Long attendanceId;

    private Employee employee;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    private BigDecimal dailyWage;

    private BigDecimal weeklyAllowance;

    private BigDecimal bonus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDay;

}
