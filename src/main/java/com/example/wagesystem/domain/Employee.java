package com.example.wagesystem.domain;

import com.example.wagesystem.dto.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Column(nullable = false)
    private String birthday;

    @Column
    private BigDecimal hourwage;

    @Column
    private BigDecimal monthWage;

    @Column
    private BigDecimal incompleteWeekAllowance;

    @Column
    private LocalDate startWeeklyAllowance;

    @Column
    private LocalDate endWeeklyAllowance;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Attendance> payList = new ArrayList<>();

    public Employee(String name, String loginId, String loginPw) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

    @Builder
    public Employee(Long employeeId, String loginId, String name, String loginPw, String store, String phoneNumber, Position position
            , String birthday, String EmPhoneNumber, BigDecimal hourwage, BigDecimal incompleteWeekAllowance, LocalDate startWeeklyAllowance, LocalDate endWeeklyAllowance, BigDecimal monthWage) {
        this.employeeId = employeeId;
        this.loginId = loginId;
        this.name = name;
        this.loginPw = loginPw;
        this.store = store;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.birthday = birthday;
        this.EmPhoneNumber = EmPhoneNumber;
        this.hourwage = hourwage;
        this.incompleteWeekAllowance = incompleteWeekAllowance;
        this.startWeeklyAllowance = startWeeklyAllowance;
        this.endWeeklyAllowance = endWeeklyAllowance;
        this.monthWage = monthWage;
    }

    public BigDecimal calculateWeeklyAllowance(BigDecimal weeklyHours) {
        if (weeklyHours.compareTo(new BigDecimal(15)) >= 0) {
            return weeklyHours.multiply(this.getHourwage()).multiply(BigDecimal.valueOf(0.2));
        } else {
            return BigDecimal.ZERO;
        }
    }
}
