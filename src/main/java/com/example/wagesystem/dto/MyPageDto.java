package com.example.wagesystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MyPageDto {

    private String name;

    private Position position;

    private BigDecimal pay;

    private BigDecimal hourWage;
}
