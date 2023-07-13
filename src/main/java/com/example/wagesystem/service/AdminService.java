package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.DailyWageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface AdminService {

    Page<Employee> findAllEmployee(Pageable pageable);

    BigDecimal getTotalBeforeMonthlyWages();

    @Transactional(readOnly = true)
    Page<Attendance> findWorkListYesterday(Pageable pageable);

    List<DailyWageDto> getDailyWages();
}
