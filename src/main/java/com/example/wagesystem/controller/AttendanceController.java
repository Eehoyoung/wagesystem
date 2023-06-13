package com.example.wagesystem.controller;


import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.AttendanceDto;
import com.example.wagesystem.dto.AttendanceInfoDto;
import com.example.wagesystem.repository.EmployeeRepository;
import com.example.wagesystem.sservice.AttendanceServiceImpl;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final EmployeeRepository employeeRepository;
    private final AttendanceServiceImpl attendanceService;

    @Autowired
    public AttendanceController(EmployeeRepository employeeRepository, AttendanceServiceImpl attendanceService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
    }

//    @PostMapping
//    public ResponseEntity<AttendanceDto> createAttendance(@RequestBody @Valid AttendanceDto attendanceDto) {
//        Long createdAttendanceId = attendanceService.createAttendance(attendanceDto);
//        AttendanceDto createdAttendance = attendanceService.getAttendanceById(createdAttendanceId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttendance);
//    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceDto> getAttendanceById(@PathVariable Long attendanceId) {
        AttendanceDto attendanceDto = attendanceService.getAttendanceById(attendanceId);
        return ResponseEntity.ok(attendanceDto);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDto>> getAllAttendances() {
        List<AttendanceDto> attendances = attendanceService.getAllAttendances();
        return ResponseEntity.ok(attendances);
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<Void> updateAttendance(@PathVariable Long attendanceId, @RequestBody @Valid AttendanceDto attendanceDto) {
        attendanceService.updateAttendance(attendanceId, attendanceDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/api/login")
    public ResponseEntity<String> getLoginEmployeeLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        return ResponseEntity.ok(loginId);
    }

    @PostMapping("/start/{employeeId}")
    public ResponseEntity<Void> startAttendance(@PathVariable Long employeeId) throws ObjectNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            // 적절한 예외 처리 및 오류 메시지를 반환하십시오.
            throw new ObjectNotFoundException("Employee not found");
        }
        LocalDateTime startTime = LocalDateTime.now();

        AttendanceInfoDto attendanceInfoDto = new AttendanceInfoDto();

        attendanceInfoDto.setEmployeeId(employeeRepository.findByEmployeeId(loginId));
        attendanceInfoDto.setStartTime(startTime);
        attendanceInfoDto.setEndTime(null);

        attendanceService.createAttendance(attendanceInfoDto);
        log.info("attendanceDto: {}", attendanceInfoDto);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> endAttendance(@RequestBody AttendanceDto attendanceDto) {
        Long employeeId = attendanceDto.getEmployeeId();
        if (employeeId != null) {
            Optional<Attendance> latestAttendance = attendanceService.getLatestAttendanceByEmployeeId(employeeId);
            if (latestAttendance.isPresent()) {
                Attendance attendance = latestAttendance.get();

                if (attendance.getEndTime() == null) {
                    LocalDateTime endTime = LocalDateTime.now();
                    attendanceDto.setAttendanceId(attendance.getAttendanceId());
                    attendanceDto.setEndTime(endTime);
                    attendanceService.updateAttendance(attendance.getAttendanceId(), attendanceDto);
                    return ResponseEntity.created(null).build();
                } else {
                    throw new RuntimeException("이미 퇴근 처리된 출근 기록입니다.");
                }
            } else {
                throw new RuntimeException("해당 사원의 출근 기록이 없습니다.");
            }
        } else {
            throw new RuntimeException("요청에 유효하지 않은 사원 ID가 포함되어 있습니다.");
        }
    }

}