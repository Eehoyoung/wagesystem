package com.example.wagesystem.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "resignation")
public class Resignation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_employee_id")
    private Long rEmployeeId;

    @Column
    private String name;

    @Column
    private LocalDate hireDate;

    @Column
    private LocalDate resignationDate;

    @Column
    private String phoneNumber;

    @Builder
    public Resignation(Long rEmployeeId, String name, String phoneNumber, LocalDate hireDate, LocalDate resignationDate) {
        this.rEmployeeId = rEmployeeId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hireDate = hireDate;
        this.resignationDate = resignationDate;
    }
}
