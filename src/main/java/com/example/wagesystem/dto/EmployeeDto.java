package com.example.wagesystem.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
public class EmployeeDto {

    private Long EmployeeId;

    private String loginId;

    private String name;

    private Position position;

    private String phoneNumber;

    private String emPhoneNumber;

    private LocalDate hireDate;

    private Date resignationDate;

    private String store;

    private BigDecimal hourWage;

    @QueryProjection
    public EmployeeDto(Long EmployeeId, String loginId, String name, Position position, Date resignationDate, String store, String phoneNumber
            , LocalDate hireDate, BigDecimal hourWage, String emPhoneNumber) {
        this.EmployeeId = EmployeeId;
        this.loginId = loginId;
        this.name = name;
        this.position = position;
        this.resignationDate = resignationDate;
        this.store = store;
        this.phoneNumber = phoneNumber;
        this.emPhoneNumber = emPhoneNumber;
        this.hourWage = hourWage;
        this.hireDate = hireDate;
    }

}

