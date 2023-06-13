package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, QuerydslPredicateExecutor<Attendance> {
    List<Attendance> findByEmployee_EmployeeIdOrderByStartTimeDesc(Long employeeId);
}
