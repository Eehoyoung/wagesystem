package com.example.wagesystem.dto.employee;

import com.example.wagesystem.dto.Position;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfileDto {

    private Long employeeId;

    private String loginId;

    private String loginPw;

    private String name;

    private Position position;

    private String[] phoneNumber;

    private String[] EmPhoneNumber;

    private String[] birthday;

    private BigDecimal hourWage;


}
