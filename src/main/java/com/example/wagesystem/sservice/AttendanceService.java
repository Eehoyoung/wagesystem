package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.dto.AttendanceDto;
import com.example.wagesystem.dto.AttendanceInfoDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Long createAttendance(AttendanceDto attendanceDto);

    AttendanceDto getAttendanceById(Long attendanceId);

    List<AttendanceDto> getAllAttendances();

    void updateAttendance(Long attendanceId, AttendanceDto attendanceDto);

    void deleteAttendance(Long attendanceId);

    Optional<Attendance> getLatestAttendanceByEmployeeId(Long employeeId);
}