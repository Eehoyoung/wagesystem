package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, QuerydslPredicateExecutor<Attendance> {
    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId ORDER BY a.startTime DESC")
    List<Attendance> findByEmployee_EmployeeIdOrderByStartTimeDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(a.attendanceId) > 0 FROM Attendance a WHERE a.employee.employeeId = :employeeId AND a.startTime BETWEEN :start AND :end")
    boolean existsByEmployeeIdAndStartTimeBetween(@Param("employeeId") Long employeeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select e.hourwage from Employee e where e.employeeId = :employeeId")
    BigDecimal findHourWageByEmployeeId(@Param("employeeId") Long employeeId);

    Page<Attendance> findAllByEmployee(Employee employee, Pageable pageable);

    @Query("SELECT a FROM Attendance a JOIN a.employee e WHERE e.employeeId = :employeeId AND a.workDay >= e.startWeeklyAllowance AND a.workDay <= e.endWeeklyAllowance")
    List<Attendance> findByEmployeeIdAndWorkDayBetweenStartAndEndWeeklyAllowance(@Param("employeeId") Long employeeId);

    List<Attendance> findAttendancesByWorkDayBetween(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth);

    @Query("Select a FROM Attendance a where a.workDay = :currentDate")
    Page<Attendance> findAttendancesByWorkDay(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    @Query("SELECT SUM(a.dailyWage) FROM Attendance a WHERE a.workDay >= :startDate AND a.workDay < :endDate")
    BigDecimal getTotalWageByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select a from Attendance a where a.employee.employeeId = :employeeId")
    List<Attendance> findAttendanceByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT a FROM Attendance a WHERE a.endTime IS NULL")
    List<Attendance> findAttendanceWithoutEndTime();

    @Query("SELECT a.employee FROM Attendance a WHERE a.attendanceId = :attendanceId")
    Employee findEmployeeByAttendanceId(@Param("attendanceId") Long attendanceId);

}

