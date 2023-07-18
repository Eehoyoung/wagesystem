package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.DailyWageDto;
import com.example.wagesystem.dto.EmployeeDto;
import com.example.wagesystem.dto.EmployeePageDto;
import com.example.wagesystem.dto.ResignationEmpDto;
import com.example.wagesystem.dto.attendance.AttendanceDto;
import com.example.wagesystem.dto.attendance.AttendanceInfoDto;
import com.example.wagesystem.service.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
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
}

