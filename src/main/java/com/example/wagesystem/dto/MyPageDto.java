package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MyPageDto {

    private String name;

    private Position position;

    private BigDecimal pay;

    private int hourWage;
}
