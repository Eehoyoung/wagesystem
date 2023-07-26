package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Attendance;
import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.attendance.AttendanceDto;
import com.example.wagesystem.dto.attendance.AttendanceEditDto;
import com.example.wagesystem.dto.attendance.AttendanceMissDto;
import com.example.wagesystem.dto.employee.*;
import com.example.wagesystem.dto.resignation.ResignationEmpDto;
import com.example.wagesystem.service.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/admin/update-hourly-wage")
    public String showUpdateHourlyWageForm(Model model) {
        model.addAttribute("employeeHourlyWageDto", new EmployeeHourlyWageDto());
        return "admin/admin_updateHourWage";
    }

    @PostMapping("/admin/update-hourly-wage")
    public String updateHourlyWage(@ModelAttribute EmployeeHourlyWageDto employeeHourlyWageDto, Model model) {
        Employee updatedEmployee = adminService.updateHourlyWage(employeeHourlyWageDto.getId(), employeeHourlyWageDto.getHourWage());
        model.addAttribute("message", "사원번호: " + updatedEmployee.getEmployeeId() + "의 시급이 " + updatedEmployee.getHourwage() + "로 변경되었습니다.");
        return "admin/admin_message";
    }

    @GetMapping("/admin/insert-attendance")
    public String attendanceForm(Model model) {
        model.addAttribute("attendanceMissDto", new AttendanceMissDto());
        return "admin/admin_attendance_form";
    }

    @PostMapping("/admin/insert-attendance")
    public String insertOrUpdateStartTime(@ModelAttribute AttendanceMissDto attendanceMissDto, Model model, EmployeeInfoDto employeeInfoDto) {
        Attendance attendance = adminService.insertOrUpdateStartTime(attendanceMissDto, employeeInfoDto);
        model.addAttribute("message", "사원번호: " + attendance.getEmployee().getEmployeeId() + "의 근무 이력이 정상적으로 입력되었습니다.");
        return "admin/admin_attendance_result";
    }

    @DeleteMapping("/admin/delete-attendance/{attendance_id}")
    @ResponseBody
    public String deleteAttendance(@PathVariable("attendance_id") Long attendanceId){
        adminService.deleteAttendance(attendanceId);
        System.out.println(attendanceId);
        return "근무기록이 삭제 되었습니다.";
    }

    @GetMapping("/admin/delete-attendance")
    public String deleteAttendance() {
        return "admin/admin_delete_attendance";
    }

    @GetMapping("/admin/edit/{attendanceId}")
    public String showEditAttendanceForm(@PathVariable Long attendanceId, Model model) {
        AttendanceEditDto attendanceDto = adminService.showAttendanceData(attendanceId);
        model.addAttribute("attendanceDto", attendanceDto);
        return "admin/admin_edit_attendance";
    }

    @PostMapping("/admin/edit/{attendanceId}")
    public String updateAttendance(@ModelAttribute AttendanceEditDto attendanceDto,
                                   @PathVariable Long attendanceId,
                                   RedirectAttributes redirectAttributes,EmployeeInfoDto employeeInfoDto) {
        try {
            adminService.updateAttendanceData(attendanceDto,employeeInfoDto,attendanceId);
            redirectAttributes.addFlashAttribute("successMessage", "근무기록이 성공적으로 업데이트되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/main/index";
    }

}

