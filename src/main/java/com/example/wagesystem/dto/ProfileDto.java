package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;

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

    private int hourWage;


}
