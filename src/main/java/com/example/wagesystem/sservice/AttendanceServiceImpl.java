package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import com.example.wagesystem.exeption.LoginIdNotFoundException;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Override
    public AttendancePageDto getWagePagingDto(String loginId, Pageable pageable) {

        AttendancePageDto attendancePageDto = new AttendancePageDto();

        Employee findEmployee = employeeRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하느느 회원이 존재하지 않습니다.")
        );

        Page<Attendance> attendanceBoard = attendanceRepository.findAllByEmployee(findEmployee, pageable);
        int homeStartPage = Math.max(1, attendanceBoard.getPageable().getPageNumber()-4);
        int homeEndPage = Math.min(attendanceBoard.getTotalPages(), attendanceBoard.getPageable().getPageNumber() + 4);

        attendancePageDto.setPayboards(attendanceBoard);
        attendancePageDto.setHomeStartPage(homeStartPage);
        attendancePageDto.setHomeEndPage(homeEndPage);

        return attendancePageDto;
    }

    @Transactional
    @Override
    public BigDecimal calculattionDailyWage(Time workTime, BigDecimal hourWage) {
        BigDecimal minHour = BigDecimal.valueOf(workTime.getMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal hour = BigDecimal.valueOf(workTime.getHours()).add(minHour);
        BigDecimal dailyWage = hourWage.multiply(hour);
        System.out.println("시간: " + hour + "시급: " + hourWage + "일급: " + dailyWage + "분: " + minHour);
        return dailyWage;
    }

    @Transactional
    @Override
    public Time calculationWorkTime(LocalDateTime startTime, LocalDateTime endTime) {

        Duration duration = Duration.between(startTime, endTime);

        // Duration 객체를 통해 시간과 분 계산
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;


        return Time.valueOf(LocalTime.of((int) hours, (int) minutes));

    }

    @Override
    public boolean findAllEmployeeId(Long employeeId) {
        return employeeRepository.findBySeachAllId(employeeId) != null;
    }

    @Override
    public boolean hasAttendanceToday(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        return attendanceRepository
                .existsByEmployeeIdAndStartTimeBetween(employeeId, todayStart, todayEnd);
    }

    @Override
    public boolean checkLoginEmployee(String loginId, Long employeeId) {
        Long nowEmployeeId = employeeRepository.findByEmployeeId(loginId); //현재 로그인된 사원의 사번
        return Objects.equals(nowEmployeeId, employeeId);
    }

    @Transactional
    @Override
    public Long createAttendance(AttendanceInfoDto attendanceInfoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Long employeeId = employeeRepository.findByEmployeeId(loginId);

        Employee employee = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = new AttendanceInfoDto(attendanceInfoDto.getAttendanceId(),
                employeeId,
                attendanceInfoDto.getStartTime(),
                attendanceInfoDto.getEndTime(),
                attendanceInfoDto.getWorkTime(),
                attendanceInfoDto.getDailyWage(),
                attendanceInfoDto.getWorkDay())
                .toEntity(employee);

        attendanceRepository.save(attendance);
        return attendance.getAttendanceId();
    }

    @Transactional
    @Override
    public AttendanceInfoDto getAttendanceById(Long attendanceId) {
        Optional<Attendance> attendanceOptional = attendanceRepository.findById(attendanceId);

        if (attendanceOptional.isPresent()) {
            Attendance attendance = attendanceOptional.get();

            return new AttendanceInfoDto(attendance.getAttendanceId(), attendance.getEmployee().getEmployeeId()
                    , attendance.getStartTime(), attendance.getEndTime(), attendance.getWorkTime(), attendance.getDailyWage(), attendance.getWorkDay());
        } else {
            throw new RuntimeException("Attendance not found");
        }
    }

    @Transactional
    @Override
    public List<AttendanceInfoDto> getAllAttendances() {
        List<Attendance> attendances = attendanceRepository.findAll();
        return attendances.stream().map(
                        attendance -> new AttendanceInfoDto(
                                attendance.getAttendanceId(), attendance.getEmployee().getEmployeeId(),
                                attendance.getStartTime(), attendance.getEndTime(), attendance.getWorkTime(), attendance.getDailyWage(), attendance.getWorkDay()))
                .collect(Collectors.toList()
                );
    }

    @Transactional
    @Override
    public void updateAttendance(Long attendanceId, AttendanceInfoDto AttendanceInfoDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        Employee employee = employeeRepository.findById(AttendanceInfoDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        attendance.setEmployee(employee);
        attendance.setStartTime(AttendanceInfoDto.getStartTime());
        attendance.setEndTime(AttendanceInfoDto.getEndTime());
        attendance.setWorkTime(AttendanceInfoDto.getWorkTime());
        attendance.setDailyWage(AttendanceInfoDto.getDailyWage());

        attendanceRepository.save(attendance);
    }

    @Transactional
    @Override
    public void deleteAttendance(Long attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }

    @Transactional
    @Override
    public Optional<Attendance> getLatestAttendanceByEmployeeId(Long employeeId) {
        List<Attendance> attendances = attendanceRepository.findByEmployee_EmployeeIdOrderByStartTimeDesc(employeeId);
        return attendances.isEmpty() ? Optional.empty() : Optional.of(attendances.get(0));
    }
}
