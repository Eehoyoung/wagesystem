package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(value = "SELECT * FROM attendance WHERE employee_id = ?1 ORDER BY start_time DESC LIMIT 1", nativeQuery = true)
    Optional<Attendance> findLatestAttendance(Long employeeId);
}

