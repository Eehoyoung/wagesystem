package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.AttendanceDto;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// AttendanceService 인터페이스를 구현하는 Service 클래스 정의
@Service
public class AttendanceServiceImpl implements AttendanceService {

    // AttendanceRepository 주입
    @Autowired
    private AttendanceRepository attendanceRepository;

    // EmployeeRepository 주입
    @Autowired
    private EmployeeRepository employeeRepository;

    // 출근을 시작하는데 필요한 Service 메서드 정의
    @Override
    public AttendanceDto startWork(Long employeeId) {
        // 주어진 employeeId를 사용하여 Employee 객체 조회
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new IllegalArgumentException("Invalid employee Id:" + employeeId)
        );

        // Attendance 객체 생성 및 Employee 객체와 시작 시간 설정
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setStartTime(LocalDateTime.now());

        // Attendance 객체 저장
        attendanceRepository.save(attendance);

        // Attendance 객체를 AttendanceDto 객체로 변환
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setAttendanceId(attendance.getAttendanceId());
        attendanceDto.setEmployeeId(employeeId);
        attendanceDto.setStartTime(attendance.getStartTime());

        // AttendanceDto 객체 반환
        return attendanceDto;
    }

    // 퇴근하는데 필요한 Service 메서드 정의
    @Override
    public AttendanceDto endWork(Long employeeId) {

        // 주어진 employeeId를 사용하여 Employee 객체 조회
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + employeeId));

        // 최근 출근 기록을 찾아 Attendance 객체 조회
        Attendance attendance = attendanceRepository.findLatestAttendance(employeeId).orElseThrow(() -> new IllegalStateException("No attendance record found for employee with Id:" + employeeId));

        // 퇴근 시간 및 일일 임금, 월간 임금 설정
        attendance.setEndTime(LocalDateTime.now());
        attendance.setDailyWage(employee.getDailyWage());
        attendance.setMonthWage(employee.getMonthWage());

        // Attendance 객체 저장
        attendanceRepository.save(attendance);

        // 일한 시간 계산
        long hours = attendance.getStartTime().until(attendance.getEndTime(), ChronoUnit.HOURS);

        // Attendance 객체를 AttendanceDto 객체로 계산 및 변환
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setAttendanceId(attendance.getAttendanceId());
        attendanceDto.setEmployeeId(employeeId);
        attendanceDto.setStartTime(attendance.getStartTime());
        attendanceDto.setEndTime(attendance.getEndTime());
        attendanceDto.setTime(hours + " hours");
        attendanceDto.setDailyWage(attendance.getDailyWage());
        attendanceDto.setMonthWage(attendance.getDailyWage().multiply(BigDecimal.valueOf(hours)));

        // AttendanceDto 객체 반환
        return attendanceDto;
    }
}