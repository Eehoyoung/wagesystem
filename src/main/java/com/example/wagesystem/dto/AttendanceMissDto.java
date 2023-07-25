package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceMissDto {

    private Long employeeId;//


    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;//

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;//

    private Time workTime;

    private BigDecimal dailyWage;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDay;
}
