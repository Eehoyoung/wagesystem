package com.example.wagesystem.service;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.DailyWageDto;
import com.example.wagesystem.dto.EmployeePageDto;
import com.example.wagesystem.dto.attendance.AttendanceDto;
import javassist.tools.rmi.ObjectNotFoundException;
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

    EmployeePageDto findAllEmployeeByPaging(Pageable pageable);

    EmployeePageDto findAllEmployeeByConditionByPaging(SearchEmployee searchEmployee, Pageable pageable);

    Employee findEmployeeById(Long id);

    List<AttendanceDto> getEmployeeAttendance(Long id);

    EmployeePageDto findAllReEmployeeByPaging(Pageable pageable);

    EmployeePageDto findAllReEmployeeByConditionByPaging(SearchResignation searchResignation, Pageable pageable);

    Employee updateHourlyWage(Long employeeId, BigDecimal newHourlyWage) throws ObjectNotFoundException;

    Employee saveEmployee(Employee employee);
}
