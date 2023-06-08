package com.example.absenteeismerp.dto;

import com.example.absenteeismerp.model.Employee;
import com.example.absenteeismerp.model.type.Position;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeDto {

    private Long EmployeeId;

    private String loginId;

    private String loginPw;

    private String name;

    private Position position;

    private String phoneNumber;

    private LocalDateTime hireDate;

    private LocalDate resignationDate;

    private String store;

    private int wage;
}

