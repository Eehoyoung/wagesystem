package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, QuerydslPredicateExecutor<Attendance> {
    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId ORDER BY a.startTime DESC")
    List<Attendance> findByEmployee_EmployeeIdOrderByStartTimeDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(a.attendanceId) > 0 FROM Attendance a WHERE a.employee.employeeId = :employeeId AND a.startTime BETWEEN :start AND :end")
    boolean existsByEmployeeIdAndStartTimeBetween(@Param("employeeId") Long employeeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select e.hourwage from Employee e where e.employeeId = :employeeId")
    BigDecimal findHourWageByEmployeeId(@Param("employeeId") Long employeeId);
}
