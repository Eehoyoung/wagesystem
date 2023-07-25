package com.example.wagesystem.dto.employee;

import com.example.wagesystem.dto.Position;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class EmployeeDto {

    private Long EmployeeId;

    private String name;

    private Position position;

    private String phoneNumber;

    private String emPhoneNumber;

    private LocalDate hireDate;

    private String store;

    private BigDecimal hourWage;

    @QueryProjection
    public EmployeeDto(Long EmployeeId, String name, Position position, String store, String phoneNumber
            , LocalDate hireDate, BigDecimal hourWage, String emPhoneNumber) {
        this.EmployeeId = EmployeeId;
        this.name = name;
        this.position = position;
        this.store = store;
        this.phoneNumber = phoneNumber;
        this.emPhoneNumber = emPhoneNumber;
        this.hourWage = hourWage;
        this.hireDate = hireDate;
    }

}

