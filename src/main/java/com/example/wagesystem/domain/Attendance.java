package com.example.wagesystem.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Time workTime;

    @Column
    private BigDecimal dailyWage;

    @Column
    private LocalDate workDay;

    @Column
    private BigDecimal weeklyAllowance;

    @Column
    private BigDecimal bonus;


    @Builder
    public Attendance(Long attendanceId, Employee employee, LocalDateTime startTime, LocalDateTime endTime, Time workTime,
                      BigDecimal dailyWage, LocalDate workDay, BigDecimal weeklyAllowance, BigDecimal bonus) {
        this.attendanceId = attendanceId;
        this.employee = employee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workTime = workTime;
        this.dailyWage = dailyWage;
        this.workDay = workDay;
        this.weeklyAllowance = weeklyAllowance;
        this.bonus = bonus;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employee.getPayList().add(this);
    }
}