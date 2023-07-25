package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.*;
import com.example.wagesystem.dto.attendance.AttendanceDto;
import com.example.wagesystem.service.AdminServiceImpl;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    private final AdminServiceImpl adminService;

    @Autowired
    public AdminController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/admin/main")
    public String getMemberMainPage(Model model, @PageableDefault(size = 4) Pageable pageable) {
        Page<Employee> employeePage = adminService.findAllEmployee(pageable);
        BigDecimal nowTotalSalary = adminService.getTotalMonthlyWagesByEmployee();
        BigDecimal beforeTotalSalary = adminService.getTotalBeforeMonthlyWages();
        Page<Attendance> attendanceList = adminService.findWorkListYesterday(pageable);
        List<DailyWageDto> dailyWages = adminService.getDailyWages();
        model.addAttribute("dailyWages", dailyWages);
        model.addAttribute("employeeList", employeePage);
        model.addAttribute("nowTotalSalary", nowTotalSalary);
        model.addAttribute("beforeTotalSalary", beforeTotalSalary);
        model.addAttribute("attendanceList", attendanceList);

        return "admin/admin_main";
    }

    @GetMapping("/admin/empList")
    public String empList(Model model, @PageableDefault(size = 9) Pageable pageable, SearchEmployee searchEmployee) {
        EmployeePageDto employeePageDto = new EmployeePageDto();

        if (searchEmployee.getSearchKeyWord() == null) {
            employeePageDto = adminService.findAllEmployeeByPaging(pageable);
        } else {
            employeePageDto = adminService.findAllEmployeeByConditionByPaging(searchEmployee, pageable);
        }

        int homeStartPage = employeePageDto.getStartPage();
        int homeEndPage = employeePageDto.getEndPage();
        Page<EmployeeDto> employeeBoards = employeePageDto.getEmployeeBoards();

        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);
        model.addAttribute("empList", employeeBoards);
        model.addAttribute("searchCondition", searchEmployee.getSearchCondition());
        model.addAttribute("searchKeyword", searchEmployee.getSearchKeyWord());

        return "admin/admin_empList";
    }

    @GetMapping("/admin/userList/user/{id}")
    public String pageUser(@PathVariable Long id, Model model) {
        List<AttendanceDto> attendanceList = adminService.getEmployeeAttendance(id);

        model.addAttribute("employee", adminService.findEmployeeById(id));
        model.addAttribute("attList", attendanceList);
        return "admin/admin_employee";
    }

    @GetMapping("/admin/reEmpList")
    public String reEmpList(Model model, @PageableDefault(size = 9) Pageable pageable, SearchResignation searchResignation) {

        EmployeePageDto reEmployeePageDto = new EmployeePageDto();

        if (searchResignation.getSearchKeyWord() == null) {
            reEmployeePageDto = adminService.findAllReEmployeeByPaging(pageable);
        } else {
            reEmployeePageDto = adminService.findAllReEmployeeByConditionByPaging(searchResignation, pageable);
        }

        int homeStartPage = reEmployeePageDto.getStartPage();
        int homeEndPage = reEmployeePageDto.getEndPage();
        Page<ResignationEmpDto> resignationEmployeeBoards = reEmployeePageDto.getReEmployeeBoards();

        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);
        model.addAttribute("reEmpList", resignationEmployeeBoards);
        model.addAttribute("searchCondition", searchResignation.getSearchCondition());
        model.addAttribute("searchKeyword", searchResignation.getSearchKeyWord());

        return "admin/admin_reEmpList";
    }

    @GetMapping("/admin/update-hourly-wage")
    public String showUpdateHourlyWageForm(Model model) {
        model.addAttribute("employeeHourlyWageDto", new EmployeeHourlyWageDto());
        return "admin/admin_updateHourWage";
    }

    @PostMapping("/admin/update-hourly-wage")
    public String updateHourlyWage(@ModelAttribute EmployeeHourlyWageDto employeeHourlyWageDto, Model model) {
        Employee updatedEmployee = adminService.updateHourlyWage(employeeHourlyWageDto.getId(), employeeHourlyWageDto.getHourWage());
        System.out.println("바뀐 시급은?  " + employeeHourlyWageDto.getHourWage());
        model.addAttribute("message", "사원번호: " + updatedEmployee.getEmployeeId() + "의 시급이 " + updatedEmployee.getHourwage() + "로 변경되었습니다.");
        return "admin/admin_message";
    }

}

