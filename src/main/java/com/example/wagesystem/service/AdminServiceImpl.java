package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.DailyWageDto;
import com.example.wagesystem.repository.AttendanceRepository;
import com.example.wagesystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AdminServiceImpl(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
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


}
