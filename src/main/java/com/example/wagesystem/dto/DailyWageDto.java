package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailyWageDto {

    private LocalDate date;
    private String dayOfWeek;
    private BigDecimal dailyWage;
}
