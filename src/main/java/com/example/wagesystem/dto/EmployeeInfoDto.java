package com.example.absenteeismerp.dto;

import com.example.absenteeismerp.model.Employee;
import com.example.absenteeismerp.model.type.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmployeeInfoDto {

    private Long employeeId;

    private String loginId;

    private String loginPw;

    private String name;

    private Position position;

    private String phoneNumber;

    private LocalDateTime hireDate;

    private String store;

    public Employee toEntity() {
        return Employee.builder()
                .employeeId(employeeId)
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }
}
