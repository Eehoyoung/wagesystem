package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.attendance.AttendanceDto;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.dto.attendance.AttendanceMissDto;
import com.example.wagesystem.dto.employee.DailyWageDto;
import com.example.wagesystem.dto.employee.EmployeeDto;
import com.example.wagesystem.dto.employee.EmployeeInfoDto;
import com.example.wagesystem.dto.employee.EmployeePageDto;
import com.example.wagesystem.dto.resignation.ResignationEmpDto;
import com.example.wagesystem.exception.LoginIdNotFoundException;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import com.example.wagesystem.repository.ResignationRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ResignationRepository resignationRepository;
    private final AttendanceServiceImpl attendanceService;
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public AdminServiceImpl(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository, ResignationRepository resignationRepository, AttendanceServiceImpl attendanceService, EmployeeServiceImpl employeeService) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.resignationRepository = resignationRepository;
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }


    @Override
    public Page<Employee> findAllEmployee(Pageable pageable) {
        return employeeRepository.findAllEmployee(pageable);
    }

    @Override
    @Transactional
    public BigDecimal getTotalBeforeMonthlyWages() {
        LocalDate currentDate = LocalDate.now().minusMonths(1);
        return getBigDecimal(currentDate);
    }

    @Override
    @Transactional
    public Page<Attendance> findWorkListYesterday(Pageable pageable) {
        LocalDate currentDate = LocalDate.now().minusDays(1);
        return attendanceRepository.findAttendancesByWorkDay(currentDate, pageable);
    }

    @Override
    @Transactional
    public List<DailyWageDto> getDailyWages() {
        LocalDate startDate = LocalDate.of(2023, 7, 1); // 시작일
        LocalDate endDate = LocalDate.now(); // 오늘

        List<DailyWageDto> dailyWages = new ArrayList<>();
        while (!startDate.isAfter(endDate)) { // startDate가 endDate 이후가 아닐 때까지 반복
            // startDate와 startDate+1일 사이의 인건비 총합 계산
            BigDecimal totalWage = attendanceRepository.getTotalWageByDate(startDate, startDate.plusDays(1));

            DailyWageDto dailyWage = new DailyWageDto();
            dailyWage.setDate(startDate);
            dailyWage.setDayOfWeek(startDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN));
            dailyWage.setDailyWage(totalWage);
            dailyWages.add(dailyWage);

            startDate = startDate.plusDays(1);
        }
        return dailyWages;
    }

    @Override
    public EmployeePageDto findAllEmployeeByPaging(Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<EmployeeDto> employeeBoards = employeeRepository.searchAll(pageable);
        int homeStartPage = Math.max(1, employeeBoards.getPageable().getPageNumber());
        int homeEndPage = Math.min(employeeBoards.getTotalPages(), employeeBoards.getPageable().getPageNumber() + 5);

        employeePageDto.setEmployeeBoards(employeeBoards);
        employeePageDto.setStartPage(homeStartPage);
        employeePageDto.setEndPage(homeEndPage);

        return employeePageDto;
    }

    @Override
    public EmployeePageDto findAllEmployeeByConditionByPaging(SearchEmployee searchEmployee, Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<EmployeeDto> employeeBoards = employeeRepository.searchByCondition(searchEmployee, pageable);

        int startPage = Math.max(1, employeeBoards.getPageable().getPageNumber() - 2);
        int endPage = Math.min(employeeBoards.getTotalPages(), startPage + 4);

        employeePageDto.setEmployeeBoards(employeeBoards);
        employeePageDto.setStartPage(startPage);
        employeePageDto.setEndPage(endPage);

        return employeePageDto;
    }

    @Override
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new LoginIdNotFoundException("해당 회원을 찾을 수 없습니다.")
        );
    }

    @Override
    public List<AttendanceDto> getEmployeeAttendance(Long employeeId) {
        List<Attendance> attendanceList = attendanceRepository.findAttendanceByEmployee(employeeId);
        List<AttendanceDto> attendanceDtoList = new ArrayList<>();

        for (Attendance attendance : attendanceList) {
            AttendanceDto attendanceDto = new AttendanceDto();
            attendanceDto.setAttendanceId(attendance.getAttendanceId());
            attendanceDto.setEmployeeId(attendance.getEmployee().getEmployeeId());
            attendanceDto.setEmployeeName(attendance.getEmployee().getName());
            attendanceDto.setStartTime(attendance.getStartTime());
            attendanceDto.setEndTime(attendance.getEndTime());
            attendanceDto.setWorkTime(attendance.getWorkTime());
            attendanceDto.setDailyWage(attendance.getDailyWage());
            attendanceDto.setWorkDay(attendance.getWorkDay());
            attendanceDto.setWeeklyAllowance(attendance.getWeeklyAllowance());
            attendanceDto.setBonus(attendance.getBonus());
            attendanceDto.setDayOfWeek(attendance.getWorkDay().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN));

            attendanceDtoList.add(attendanceDto);
        }

        return attendanceDtoList;
    }

    @Override
    public EmployeePageDto findAllReEmployeeByPaging(Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<ResignationEmpDto> ReEmployeeBoards = resignationRepository.searchAll(pageable);
        int homeStartPage = Math.max(1, ReEmployeeBoards.getPageable().getPageNumber());
        int homeEndPage = Math.min(ReEmployeeBoards.getTotalPages(), ReEmployeeBoards.getPageable().getPageNumber() + 5);

        employeePageDto.setReEmployeeBoards(ReEmployeeBoards);
        employeePageDto.setStartPage(homeStartPage);
        employeePageDto.setEndPage(homeEndPage);

        return employeePageDto;
    }

    @Override
    public EmployeePageDto findAllReEmployeeByConditionByPaging(SearchResignation searchResignation, Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<ResignationEmpDto> ReEmployeeBoards = resignationRepository.searchByCondition(searchResignation, pageable);

        int startPage = Math.max(1, ReEmployeeBoards.getPageable().getPageNumber() - 2);
        int endPage = Math.min(ReEmployeeBoards.getTotalPages(), startPage + 4);

        employeePageDto.setReEmployeeBoards(ReEmployeeBoards);
        employeePageDto.setStartPage(startPage);
        employeePageDto.setEndPage(endPage);

        return employeePageDto;
    }


    @Override
    @Transactional
    public Employee updateHourlyWage(Long employeeId, BigDecimal hourlyWage) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사원이 없습니다."));

        employee.setHourwage(hourlyWage);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Attendance insertOrUpdateStartTime(AttendanceMissDto attendanceMissDto, EmployeeInfoDto employeeInfoDto) {
        Attendance attendance = new Attendance();

        Employee findEmployee = employeeRepository.findById(attendanceMissDto.getEmployeeId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사원을 찾을 수 없습니다.")
        );
        attendance.setEmployee(findEmployee);
        attendance.setStartTime(attendanceMissDto.getStartTime());
        attendance.setEndTime(attendanceMissDto.getEndTime());
        attendance.setWeeklyAllowance(new BigDecimal(BigInteger.ZERO));
        attendance.setBonus(new BigDecimal(BigInteger.ZERO));
        attendance.setWorkDay(attendanceMissDto.getWorkDay());
        attendance.setWorkTime(attendanceService.calculationSetWorkTime(attendanceMissDto.getStartTime(), attendanceMissDto.getEndTime()));
        attendance.setDailyWage(attendanceService.calculattionDailyWage(
                attendanceService.calculationWorkTime(attendanceMissDto.getStartTime(), attendanceMissDto.getEndTime()),
                attendanceRepository.findHourWageByEmployeeId(attendanceMissDto.getEmployeeId())));
        attendanceRepository.save(attendance);
        BigDecimal monthWage = attendanceService.calculateMonthWage(attendance.getEmployee().getEmployeeId());
        employeeInfoDto.setMonthWage(monthWage);
        try {
            employeeService.updateMonthWage(attendance.getEmployee().getEmployeeId(), employeeInfoDto);
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e);
        }

        return attendance;
    }

    @Transactional
    public BigDecimal getTotalMonthlyWagesByEmployee() {
        LocalDate currentDate = LocalDate.now();
        return getBigDecimal(currentDate);
    }

    private BigDecimal getBigDecimal(LocalDate currentDate) {
        YearMonth currentYearMonth = YearMonth.from(currentDate);
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        List<Attendance> attendances = attendanceRepository.findAttendancesByWorkDayBetween(firstDayOfMonth, lastDayOfMonth);
        BigDecimal totalWages = BigDecimal.ZERO;

        for (Attendance attendance : attendances) {
            if (attendance.getDailyWage() != null) {
                totalWages = totalWages.add(attendance.getDailyWage());
            }
        }
        return totalWages;
    }

    @Scheduled(cron = "0 30 23 * * ?")
    @Transactional
    public void autoInsertEndWork() {
        // 퇴근 처리가 안된 출석체크 기록을 찾습니다.
        List<Attendance> attendances = attendanceRepository.findAttendanceWithoutEndTime();
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0));

        for (Attendance attendance : attendances) {
            if (attendance.getWorkTime() != null) {
                AttendanceInfoDto attendanceInfoDto = new AttendanceInfoDto();
                attendanceInfoDto.setAttendanceId(attendance.getAttendanceId());
                attendanceInfoDto.setEndTime(endTime); // 퇴근 시간 입력
                attendanceInfoDto.setStartTime(attendance.getStartTime()); //출근 시간 set
                attendanceInfoDto.setWorkTime(attendanceService.calculationSetWorkTime(attendance.getStartTime(), endTime)); // 총 근무 시간 입력
                attendanceInfoDto.setWeeklyAllowance(new BigDecimal(BigInteger.ZERO));
                attendanceInfoDto.setBonus(new BigDecimal(BigInteger.ZERO));
                attendanceInfoDto.setDailyWage(attendanceService.calculattionDailyWage(
                        attendanceService.calculationWorkTime(attendance.getStartTime(), endTime),
                        attendanceRepository.findHourWageByEmployeeId(attendance.getEmployee().getEmployeeId())));

                attendanceService.updateAttendance(attendance.getAttendanceId(), attendanceInfoDto);

                BigDecimal monthWage = attendanceService.calculateMonthWage(attendance.getEmployee().getEmployeeId());
                EmployeeInfoDto employeeInfoDto = new EmployeeInfoDto();
                employeeInfoDto.setMonthWage(monthWage);
                try {
                    employeeService.updateMonthWage(attendance.getEmployee().getEmployeeId(), employeeInfoDto);
                } catch (ObjectNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
