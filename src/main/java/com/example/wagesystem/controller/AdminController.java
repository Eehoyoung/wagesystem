package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.DailyWageDto;
import com.example.wagesystem.service.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

}

