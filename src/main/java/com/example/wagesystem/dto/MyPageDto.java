package com.example.absenteeismerp.dto;

import com.example.absenteeismerp.model.type.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class myPageDto {

    private String name;

    private Position position;

    private double pay;
}
