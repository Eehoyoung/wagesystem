package com.example.wagesystem.dto;

import com.example.wagesystem.domain.Resignation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResignationDto {

    private Long rEmployeeId;

    private String name;

    private LocalDate hireDate;

    private LocalDate resignationDate;

    private String phoneNumber;

    public Resignation toEntity() {
        return Resignation.builder()
                .rEmployeeId(rEmployeeId)
                .name(name)
                .hireDate(hireDate)
                .resignationDate(resignationDate)
                .phoneNumber(phoneNumber)
                .build();
    }
}
