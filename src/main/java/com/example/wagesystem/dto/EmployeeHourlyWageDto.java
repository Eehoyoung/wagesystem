package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeHourlyWageDto {

    private Long id;

    private BigDecimal hourWage;
}
