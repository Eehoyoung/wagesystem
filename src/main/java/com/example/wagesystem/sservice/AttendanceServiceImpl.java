package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.AttendanceDto;
import com.example.wagesystem.dto.AttendanceInfoDto;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceServiceImpl(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public Long createAttendance(AttendanceInfoDto attendanceInfoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Long employeeId = employeeRepository.findByEmployeeId(loginId);

        Employee employee = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = new AttendanceInfoDto(attendanceInfoDto.getAttendanceId(),
                employeeId,
                attendanceInfoDto.getStartTime(),
                attendanceInfoDto.getEndTime())
                .toEntity(employee);

        attendanceRepository.save(attendance);
        return attendance.getAttendanceId();
    }

    @Override
    public Long createAttendance(AttendanceDto attendanceDto) {
        return null;
    }

    @Transactional
    public AttendanceDto getAttendanceById(Long attendanceId) {
        Optional<Attendance> attendanceOptional = attendanceRepository.findById(attendanceId);

        if (attendanceOptional.isPresent()) {
            Attendance attendance = attendanceOptional.get();

            return new AttendanceDto(attendance.getAttendanceId(),
                    attendance.getEmployee().getEmployeeId(),
                    attendance.getStartTime(), attendance.getEndTime(),attendance.getEmployee());
        } else {
            throw new RuntimeException("Attendance not found");
        }
    }

    @Transactional
    public List<AttendanceDto> getAllAttendances() {
        List<Attendance> attendances = attendanceRepository.findAll();
        Employee employee = new Employee();
        return attendances.stream().map(attendance -> {
            Long employeeId = (attendance.getEmployee() != null) ? attendance.getEmployee().getEmployeeId() : null;

            return new AttendanceDto(attendance.getAttendanceId(),
                    Objects.requireNonNull(attendance.getEmployee()).getEmployeeId(),
                    attendance.getStartTime(),
                    attendance.getEndTime(),attendance.getEmployee());
        }).collect(Collectors.toList());

    }

    @Transactional
    public void updateAttendance(Long attendanceId, AttendanceDto attendanceDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        Employee employee = employeeRepository.findById(attendanceDto.getEmployee().getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        attendance.setEmployee(employee);
        attendance.setStartTime(attendanceDto.getStartTime());
        attendance.setEndTime(attendanceDto.getEndTime());

        attendanceRepository.save(attendance);
    }

    @Transactional
    public void deleteAttendance(Long attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }

    @Transactional
    public Optional<Attendance> getLatestAttendanceByEmployeeId(Long employeeId) {
        List<Attendance> attendances = attendanceRepository.findByEmployee_EmployeeIdOrderByStartTimeDesc(employeeId);
        return attendances.isEmpty() ? Optional.empty() : Optional.of(attendances.get(0));
    }
}
