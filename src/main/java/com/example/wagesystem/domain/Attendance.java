package com.example.wagesystem.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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


    public void setEmployee(Employee employee){
        this.employee = employee;
        employee.getPayList().add(this);
    }

    @Builder
    public Attendance(Long attendanceId, Employee employee, LocalDateTime startTime, LocalDateTime endTime) {
        this.attendanceId = attendanceId;
        this.employee = employee;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}