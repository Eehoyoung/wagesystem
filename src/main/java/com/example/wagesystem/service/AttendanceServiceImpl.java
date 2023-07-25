package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import com.example.wagesystem.exception.LoginIdNotFoundException;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.time.*;
import java.util.Comparator;
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
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다.")
        );

        Page<Attendance> attendanceBoard = attendanceRepository.findAllByEmployee(findEmployee, pageable);
        int homeStartPage = Math.max(1, attendanceBoard.getPageable().getPageNumber() - 4);
        int homeEndPage = Math.min(attendanceBoard.getTotalPages(), attendanceBoard.getPageable().getPageNumber() + 4);

        attendancePageDto.setPayboards(attendanceBoard);
        attendancePageDto.setHomeStartPage(homeStartPage);
        attendancePageDto.setHomeEndPage(homeEndPage);

        return attendancePageDto;
    }

    @Transactional
    @Override
    public BigDecimal calculattionDailyWage(Duration workTime, BigDecimal hourWage) {
        BigDecimal dailyWage = BigDecimal.ZERO;
        int overtimeThreshold = 8 * 60; // 8시간,480분 (8 시간 * 60 분)

        // 원래 시간에 대한 계산
        int regularTime = 0;
        if (workTime.toMinutes() >= overtimeThreshold) {
            regularTime = overtimeThreshold;
        } else {
            regularTime = (int) workTime.toMinutes();
        }

        dailyWage = dailyWage.add(hourWage.multiply(BigDecimal.valueOf(regularTime)).divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP));

        // 초과 근무에 대한 계산
        if (workTime.toMinutes() > overtimeThreshold) {
            int extraTime = (int) workTime.toMinutes() - overtimeThreshold;
            BigDecimal extraWage = hourWage.multiply(new BigDecimal("1.5"));
            dailyWage = dailyWage.add(extraWage.multiply(BigDecimal.valueOf(extraTime)).divide(BigDecimal.valueOf(60), RoundingMode.HALF_UP));
        }

        return dailyWage;
    }

    @Transactional
    @Override
    public Duration calculationWorkTime(LocalDateTime startTime, LocalDateTime endTime) {

        return Duration.between(startTime, endTime);
    }

    @Transactional
    @Override
    public Time calculationSetWorkTime(LocalDateTime startTime, LocalDateTime endTime) {

        Duration duration = Duration.between(startTime, endTime);

        // Duration 객체를 통해 시간과 분 계산
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        LocalTime totalTime = LocalTime.of((int) hours, (int) minutes);

        // LocalTime 객체를 Time 타입으로 변환
        return Time.valueOf(totalTime);
    }

    @Override
    public boolean findAllEmployeeId(Long employeeId) {
        return employeeRepository.findBySearchAllId(employeeId) != null;
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
                attendanceInfoDto.getWorkDay(),
                attendanceInfoDto.getWeeklyAllowance(),
                attendanceInfoDto.getBonus())
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
                    , attendance.getStartTime(), attendance.getEndTime(), attendance.getWorkTime(), attendance.getDailyWage(), attendance.getWorkDay(), attendance.getWeeklyAllowance(), attendance.getBonus());
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
                                attendance.getStartTime(), attendance.getEndTime(), attendance.getWorkTime(), attendance.getDailyWage(), attendance.getWorkDay(), attendance.getWeeklyAllowance(), attendance.getBonus()))
                .collect(Collectors.toList()
                );
    }

    @Transactional
    @Override
    public void updateAttendance(Long attendanceId, AttendanceInfoDto AttendanceInfoDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("해당 근무기록을 찾을 수 없습니다."));

        Employee employee = employeeRepository.findById(AttendanceInfoDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("해당 사원을 찾을 수 없습니다."));

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

    @Transactional
    @Override
    public BigDecimal calculateMonthWage(Long employeeId) {
        // 1. 사원 정보 찾기
        Employee findMember = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다"));

        // 2. 사원의 payList 가져오기
        List<Attendance> payList = findMember.getPayList();

        // 3. 월급 계산하기 위한 변수 초기화
        BigDecimal monthWage = BigDecimal.ZERO;

        // 4. payList를 반복하며 dailyWage와 weeklyAllowance를 합산
        for (Attendance attendance : payList) {
            BigDecimal dailyWage = attendance.getDailyWage();
            BigDecimal weeklyAllowance = attendance.getWeeklyAllowance();

            if (dailyWage != null) {
                monthWage = monthWage.add(dailyWage);
            }

            if (weeklyAllowance != null) {
                monthWage = monthWage.add(weeklyAllowance);
            }
        }

        // 5. IncompleteWeekAllowance이 0이 아니면 월급에서 차감하여 반환
        BigDecimal incompleteWeekAllowance = findMember.getIncompleteWeekAllowance();
        if (incompleteWeekAllowance != null && !Objects.equals(incompleteWeekAllowance, BigDecimal.ZERO)) {
            return monthWage.subtract(incompleteWeekAllowance);
        }

        // 6. 최종 월급 반환
        return monthWage;
    }

    // 주휴 수당을 계산하는 스케줄러
    @Scheduled(cron = "0 40 23 * * ?")
    @Transactional
    public void calculateWeeklyAllowances() throws ObjectNotFoundException {
        List<Employee> employees = employeeRepository.findAll();

        //모든 사원을 조회
        for (Employee employee : employees) {
            calculateAndUpdateWeeklyAllowance(employee);
            employeeRepository.save(employee);
        }
    }

    // 주휴 수당 계산 및 업데이트
    private void calculateAndUpdateWeeklyAllowance(Employee employee) throws ObjectNotFoundException {
        LocalDate startDate = employee.getStartWeeklyAllowance();
        LocalDate endDate = employee.getEndWeeklyAllowance();
        LocalDate now = LocalDate.now();
        endDate = adjustEndDateBasedOnMonthAndNow(endDate, startDate, now);

        List<Attendance> attendances = findAttendancesBetweenDates(employee);
        BigDecimal weeklyHours = calculateWeeklyHours(attendances);
        System.out.println(weeklyHours);

        if (weeklyHours.compareTo(new BigDecimal(15)) < 0) {
            return;
        }

        BigDecimal weeklyAllowance = employee.calculateWeeklyAllowance(weeklyHours);

        if (isCarriedOverWeek(startDate, endDate, now)) {
            saveWeeklyAllowanceAndUpdateDates(employee, endDate, weeklyAllowance);
        } else if (endDate.equals(now)) {
            employee.setIncompleteWeekAllowance(weeklyAllowance);
        }
    }

    // 종료일을 현재 날짜와 기준 월에 따라 조정
    private LocalDate adjustEndDateBasedOnMonthAndNow(LocalDate endDate, LocalDate startDate, LocalDate now) {
        YearMonth yearMonth = YearMonth.from(startDate);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        if (endDate.isAfter(endOfMonth)) {
            endDate = endOfMonth;
        } else if (endDate.isBefore(now)) {
            endDate = startDate.plusDays(6);
        }

        return endDate;
    }

    // 시작일과 종료일 사이의 출석 데이터를 찾음
    private List<Attendance> findAttendancesBetweenDates(Employee employee) {
        return attendanceRepository.findByEmployeeIdAndWorkDayBetweenStartAndEndWeeklyAllowance(employee.getEmployeeId());
    }

    // 한 주간 근무 시간 계산
    private BigDecimal calculateWeeklyHours(List<Attendance> attendances) {
        return attendances.stream()
                .map(Attendance::calculateWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 이어지는 주인지 확인
    private boolean isCarriedOverWeek(LocalDate startDate, LocalDate endDate, LocalDate now) {
        return endDate.minusDays(6) != startDate && endDate.equals(now);
    }

    // 주휴 수당 저장 및 기간 업데이트
    private void saveWeeklyAllowanceAndUpdateDates(Employee employee, LocalDate endDate, BigDecimal weeklyAllowance) throws ObjectNotFoundException {
        Attendance lastAttendance = findLastAttendanceByEmployeeId(employee.getEmployeeId());
        lastAttendance.setWeeklyAllowance(weeklyAllowance);
        attendanceRepository.save(lastAttendance);

        employee.setStartWeeklyAllowance(endDate.plusDays(1));
        employee.setEndWeeklyAllowance(endDate.plusDays(7));
    }

    // 직원 ID로 마지막 근무 정보 검색
    private Attendance findLastAttendanceByEmployeeId(Long employeeId) throws ObjectNotFoundException {
        Employee employee = findEmployeeById(employeeId);
        List<Attendance> attendances = employee.getPayList();

        return attendances.stream()
                .max(Comparator.comparingLong(Attendance::getAttendanceId))
                .orElseThrow(() -> new ObjectNotFoundException("해당하는 근무기록이 존재하지 않습니다."));
    }

    // 직원 ID로 직원 검색
    private Employee findEmployeeById(Long employeeId) throws ObjectNotFoundException {
        return employeeRepository.findById(employeeId).orElseThrow(
                () -> new ObjectNotFoundException("해당하는 사원이 존재하지 않습니다."));
    }
}

