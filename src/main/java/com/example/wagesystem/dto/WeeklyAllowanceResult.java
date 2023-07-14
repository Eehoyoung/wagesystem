package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WeeklyAllowanceResult {
    private final BigDecimal totalAllowance;
    private final BigDecimal incompleteWeekHours;

    public WeeklyAllowanceResult(BigDecimal totalAllowance, BigDecimal incompleteWeekHours) {
        this.totalAllowance = totalAllowance;
        this.incompleteWeekHours = incompleteWeekHours;
    }

    public BigDecimal getTotalAllowance() {
        return totalAllowance;
    }

    public BigDecimal getIncompleteWeekHours() {
        return incompleteWeekHours;
    }
}
