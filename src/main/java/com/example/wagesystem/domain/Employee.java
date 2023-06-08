package com.example.absenteeismerp.model;

import com.example.absenteeismerp.model.type.Position;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String store;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column
    private LocalDate resignationDate;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> payList = new ArrayList<>();

    public Employee(String name, String loginId, String loginPw) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

    @Builder
    public Employee(Long employeeId, String loginId, String name, String loginPw, String store, String phoneNumber, Position position
    , LocalDate hireDate, LocalDate resignationDate) {
        this.employeeId = employeeId;
        this.loginId = loginId;
        this.name = name;
        this.loginPw = loginPw;
        this.store = store;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.hireDate = hireDate;
        this.resignationDate = resignationDate;
    }
}
