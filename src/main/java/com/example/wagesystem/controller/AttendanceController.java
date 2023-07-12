package com.example.wagesystem.controller;


import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.EmployeeInfoDto;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.exception.AttendanceException;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import com.example.wagesystem.service.AttendanceServiceImpl;
import com.example.wagesystem.service.EmployeeServiceImpl;
import io.swagger.annotations.ApiOperation;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final EmployeeRepository employeeRepository;
    private final AttendanceServiceImpl attendanceService;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public AttendanceController(EmployeeRepository employeeRepository, AttendanceServiceImpl attendanceService, AttendanceRepository attendanceRepository, EmployeeServiceImpl employeeService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
        this.attendanceRepository = attendanceRepository;
        this.employeeService = employeeService;
    }

    @ApiOperation("근태기록 조회(attendanceId 기반")
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceInfoDto> getAttendanceById(@PathVariable Long attendanceId) {
        AttendanceInfoDto attendanceInfoDto = attendanceService.getAttendanceById(attendanceId);
        return ResponseEntity.ok(attendanceInfoDto);
    }

    @ApiOperation("근태기록 리스트 출력")
    @GetMapping("/attendance/list")
    public ResponseEntity<List<AttendanceInfoDto>> getAllAttendances() {
        List<AttendanceInfoDto> attendances = attendanceService.getAllAttendances();
        return ResponseEntity.ok(attendances);
    }

    @ApiOperation("근태기록 변경")
    @PutMapping("/update/{attendanceId}")
    public ResponseEntity<Void> updateAttendance(@PathVariable Long attendanceId, @RequestBody @Valid AttendanceInfoDto attendanceInfoDto) {
        attendanceService.updateAttendance(attendanceId, attendanceInfoDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("근태기록 삭제")
    @DeleteMapping("/delete/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @ApiOperation("출근")
    @PostMapping("/start/{employeeId}")
    public ResponseEntity<Void> startAttendance(@PathVariable Long employeeId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Employee employee = validateEmployee(employeeId); // 중복 코드 분리

        if (attendanceService.hasAttendanceToday(employeeId)) {
            throw new AttendanceException("오늘 이미 출근 처리가 되어 있습니다.");
        }

        LocalDateTime startTime = LocalDateTime.now();

        AttendanceInfoDto attendanceInfoDto = new AttendanceInfoDto();

        attendanceInfoDto.setEmployeeId(employeeRepository.findByEmployeeId(loginId));
        attendanceInfoDto.setStartTime(startTime);
        attendanceInfoDto.setEndTime(null);
        attendanceInfoDto.setWorkTime(null);
        attendanceInfoDto.setWorkDay(startTime.toLocalDate());

        attendanceService.createAttendance(attendanceInfoDto);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("퇴근")
    @PostMapping("/end/{employeeId}")
    public ResponseEntity<Void> endAttendance(@PathVariable Long employeeId, AttendanceInfoDto attendanceInfoDto, EmployeeInfoDto employeeInfoDto)
            throws ObjectNotFoundException {
        Employee employee = validateEmployee(employeeId);

        Optional<Attendance> latestAttendance = attendanceService.getLatestAttendanceByEmployeeId(employeeId);
        if (latestAttendance.isPresent()) {
            Attendance attendance = latestAttendance.get();
            if (attendance.getEndTime() == null) {
                LocalDateTime endTime = LocalDateTime.now();

                attendanceInfoDto.setAttendanceId(attendance.getAttendanceId());

                attendanceInfoDto.setEndTime(endTime); // 퇴근 시간 입력
                attendanceInfoDto.setStartTime(attendance.getStartTime()); //출근 시간 set
                attendanceInfoDto.setWorkTime(attendanceService.calculationSetWorkTime(attendance.getStartTime(), endTime)); // 총 근무 시간 입력

                attendanceInfoDto.setDailyWage(attendanceService.calculattionDailyWage(
                        attendanceService.calculationWorkTime(attendance.getStartTime(), endTime),
                        attendanceRepository.findHourWageByEmployeeId(employeeId)));

                attendanceService.updateAttendance(attendance.getAttendanceId(), attendanceInfoDto);

                BigDecimal monthWage = attendanceService.calculateMonthWage(employeeId);
                employeeInfoDto.setMonthWage(monthWage);
                employeeService.updateMonthWage(employeeId, employeeInfoDto);
                return ResponseEntity.noContent().build();
            } else {
                throw new AttendanceException("이미 퇴근 처리된 출근 기록입니다.");
            }
        } else {
            throw new AttendanceException("해당 사원의 출근 기록이 없습니다.");
        }
    }

    //중복 코드 분리
    private Employee validateEmployee(Long employeeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (!attendanceService.checkLoginEmployee(loginId, employeeId)) {
            throw new AttendanceException("현재 로그인된 사용자의 사번이 아닙니다.");
        }
        if (!optionalEmployee.isPresent()) {
            throw new AttendanceException("Employee not found");
        }
        if (!attendanceService.findAllEmployeeId(employeeId)) {
            throw new AttendanceException("존재하지 않는 사번입니다.");
        }
        return optionalEmployee.get();
    }

}