package com.example.absenteeismerp.dto;

import com.example.absenteeismerp.model.type.Position;
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
}
