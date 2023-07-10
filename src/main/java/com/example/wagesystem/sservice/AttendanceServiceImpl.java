package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import com.example.wagesystem.exeption.LoginIdNotFoundException;
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
import java.time.temporal.ChronoUnit;
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
        return hourWage.multiply(hour);
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
    @Transactional
    @Override
    public BigDecimal calculateMonthWage(Long employeeId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Employee findMember = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다"));

        List<Attendance> payList = findMember.getPayList();

        for (Attendance attendance : payList) {
            // 초기화를 위해 모든 대리 연산을 수행해야 함
            attendance.getAttendanceId();
        }

        BigDecimal monthWage = BigDecimal.ZERO;

        for (int i = 0; i < findMember.getPayList().size(); i++) {
            monthWage = monthWage.add(findMember.getPayList().get(i).getDailyWage());
            if (findMember.getPayList().get(i).getDailyWage() == null) {
                monthWage = monthWage.add(findMember.getPayList().get(i).getDailyWage());
            }
        }

        return monthWage.subtract(findMember.getIncompleteWeekAllowance());

    }

    @Scheduled(cron = "0 40 23 * * ?")
    public void cal(){
        Employee employee = new Employee();
        LocalDate startDate = employeeRepository.findByStartWeeklyAllowance(employee.getEmployeeId()); //주휴수당 시작 요일
        LocalDate endDate = employeeRepository.findByEndWeeklyAllowance(employee.getEmployeeId()); // 주휴수당 종료 요일
        LocalDate now = LocalDate.now(); //스케줄러가 실행되는 현 시점
        YearMonth yearMonth = YearMonth.from(startDate); // 주휴수당 시작 주의 연,월을 출력 yyyy-mm
        LocalDate end = yearMonth.atEndOfMonth(); // 해당 연,월의 마지막 날을 출력 yyyy-mm-dd

        if (endDate.isAfter(end)) { //주휴수당 종료 일이 다음달인 경우
            endDate = end; // 주휴수당 종료일을 월말로 변경
        }
        //주휴수당 종료일을 월말로 변경하여 해당월의 말일까지 발생한 주휴 수당을 전부 지급

        if(endDate.isBefore(now)){
            endDate = startDate.plusDays(6);
        }
        //이월된 후 다음달 1일에 다시 원상복구

        // 각 사원의 주휴수당 시작 일과 종료 일을 불러옴
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndWorkDayBetweenStartAndEndWeeklyAllowance(employee.getEmployeeId());

        BigDecimal weeklyHours = attendances.stream()
                .map(att -> {
                    LocalTime startTime = LocalTime.from(att.getStartTime());
                    LocalTime endTime = LocalTime.from(att.getEndTime());
                    long hours = ChronoUnit.HOURS.between(startTime, endTime)
                            + ChronoUnit.MINUTES.between(startTime, endTime) / 60;
                    return BigDecimal.valueOf(hours);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(weeklyHours.compareTo(new BigDecimal(15)) >= 0){ // 1주 근무시간이 15시간 이상인 경우 주휴 수당 발생
           BigDecimal weeklyAllowance = weeklyHours.multiply(employee.getHourwage()).multiply(BigDecimal.valueOf(0.2));
           if(endDate.minusDays(6) != startDate && endDate.equals(now)){ // 이월주인 경우 선지급금을 제외하기 위해 저장
               employee.setIncompleteWeekAllowance(weeklyAllowance);
           }else{
               try {
                   if(endDate.equals(now)) { // 완전한 주인 경우
                       Long attId = findLastAttendanceIdByEmployeeId(employee.getEmployeeId()); //마지막 근무를 불러옵니다.
                       Attendance attendance = attendanceRepository.findById(attId)
                               .orElseThrow(() -> new LoginIdNotFoundException("해당하는 근무기록이 존재하지 않습니다"));
                       attendance.setWeeklyAllowance(weeklyAllowance);
                       attendanceRepository.save(attendance);
                   }
               } catch (ObjectNotFoundException e) {
                   throw new RuntimeException(e);
               }
           }
        }
        employeeRepository.save(employee);

    }

    public Long findLastAttendanceIdByEmployeeId(Long employeeId) throws ObjectNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ObjectNotFoundException("해당하는 사원이 존재하지 않습니다."));

        if (employee != null) {
            List<Attendance> attendances = employee.getPayList();

            return attendances.stream()
                    .mapToLong(Attendance::getAttendanceId)
                    .max()
                    .orElse(-1); // orElse()의 인자는 검색이 실패한 경우 반환할 사용자 지정 값입니다. 여기서 -1은 실패를 나타냅니다.
        } else {
            return (long) -1; // orElse()와 동일한 실패 값을 반환합니다.
        }
    }
}
