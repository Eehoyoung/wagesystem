package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.dto.WeeklyAllowanceResult;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    BigDecimal calculateMonthWage(Long employeeId);

    AttendancePageDto getWagePagingDto(String loginId, Pageable pageable);

    BigDecimal calculattionDailyWage(Time workTime, BigDecimal hourWage);

    Time calculationWorkTime(LocalDateTime startTime, LocalDateTime endTime);

    boolean findAllEmployeeId(Long employeeId);

    boolean hasAttendanceToday(Long employeeId);

    boolean checkLoginEmployee(String loginId, Long employeeId);

    Long createAttendance(AttendanceInfoDto attendanceInfoDto);

    AttendanceInfoDto getAttendanceById(Long attendanceId);

    List<AttendanceInfoDto> getAllAttendances();

    void updateAttendance(Long attendanceId, AttendanceInfoDto attendanceInfoDto);

    void deleteAttendance(Long attendanceId);

    Optional<Attendance> getLatestAttendanceByEmployeeId(Long employeeId);

}