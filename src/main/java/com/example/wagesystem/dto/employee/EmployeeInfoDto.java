package com.example.wagesystem.dto.employee;

import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.Position;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private String EmPhoneNumber;

    private String store;

    private String birthday;

    private BigDecimal hourWage;

    private LocalDate startWeeklyAllowance;

    private LocalDate endWeeklyAllowance;

    private BigDecimal monthWage;

    @Builder
    public EmployeeInfoDto(Position position, String store, Long employeeId, String loginId, String loginPw, String name, String phoneNumber, String birthday, String EmPhoneNumber, BigDecimal hourWage, LocalDate startWeeklyAllowance, LocalDate endWeeklyAllowance, BigDecimal monthWage) {
        this.employeeId = employeeId;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.EmPhoneNumber = EmPhoneNumber;
        this.store = store;
        this.position = position;
        this.hourWage = hourWage;
        this.startWeeklyAllowance = startWeeklyAllowance;
        this.endWeeklyAllowance = endWeeklyAllowance;
        this.monthWage = monthWage;
    }

    public Employee toEntity() {
        return Employee.builder()
                .employeeId(employeeId)
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .EmPhoneNumber(EmPhoneNumber)
                .store(store)
                .position(position)
                .hourwage(hourWage)
                .startWeeklyAllowance(startWeeklyAllowance)
                .endWeeklyAllowance(endWeeklyAllowance)
                .monthWage(monthWage)
                .build();
    }
}
