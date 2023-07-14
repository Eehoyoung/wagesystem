package com.example.wagesystem.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ResignationEmpDto {

    private Long rEmployeeId;

    private String name;

    private LocalDate hireDate;

    private LocalDate resignationDate;

    private String phoneNumber;

    @QueryProjection
    public ResignationEmpDto(Long rEmployeeId, String name, LocalDate hireDate, LocalDate resignationDate, String phoneNumber) {
        this.rEmployeeId = rEmployeeId;
        this.name = name;
        this.hireDate = hireDate;
        this.resignationDate = resignationDate;
        this.phoneNumber = phoneNumber;
    }
}
