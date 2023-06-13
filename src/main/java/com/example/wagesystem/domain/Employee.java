package com.example.wagesystem.domain;

import com.example.wagesystem.dto.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employee")
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String EmPhoneNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column
    private Date resignationDate;

    @Column(nullable = false)
    private String birthday;

    @Column
    private int hourwage;

    @Column
    private BigDecimal dailyWage;

    @Column
    private BigDecimal monthWage;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> payList = new ArrayList<>();

    public Employee(String name, String loginId, String loginPw) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

    @Builder
    public Employee(Long employeeId, String loginId, String name, String loginPw, String store, String phoneNumber, Position position
            , Date resignationDate, String birthday, String EmPhoneNumber, int hourwage) {
        this.employeeId = employeeId;
        this.loginId = loginId;
        this.name = name;
        this.loginPw = loginPw;
        this.store = store;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.resignationDate = resignationDate;
        this.birthday = birthday;
        this.EmPhoneNumber = EmPhoneNumber;
        this.hourwage = hourwage;
    }
}
