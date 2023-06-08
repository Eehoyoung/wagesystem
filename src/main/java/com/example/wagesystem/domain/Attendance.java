package com.example.absenteeismerp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private BigDecimal dailyWage;

    @Column(nullable = false)
    private BigDecimal weeklyAllowance;

    public Attendance(Employee employee, LocalDateTime startTime, LocalDateTime endTime, BigDecimal dailyWage, BigDecimal weeklyAllowance) {
        this.employee = employee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dailyWage = dailyWage;
        this.weeklyAllowance = weeklyAllowance;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
        employee.getPayList().add(this);
    }
}
