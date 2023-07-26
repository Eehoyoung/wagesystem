package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    BigDecimal calculateMonthWage(Long employeeId);

    AttendancePageDto getWagePagingDto(String loginId, Pageable pageable);

    BigDecimal calculattionDailyWage(Duration workTime, BigDecimal hourWage);

    Time calculationSetWorkTime(LocalDateTime startTime, LocalDateTime endTime);

    Duration calculationWorkTime(LocalDateTime startTime, LocalDateTime endTime);

    boolean findAllEmployeeId(Long employeeId);

    boolean hasAttendanceToday(Long employeeId);

    boolean checkLoginEmployee(String loginId, Long employeeId);

    Long createAttendance(AttendanceInfoDto attendanceInfoDto);

    List<AttendanceInfoDto> getAllAttendances();

    void updateAttendance(Long attendanceId, AttendanceInfoDto attendanceInfoDto);

    Optional<Attendance> getLatestAttendanceByEmployeeId(Long employeeId);

}