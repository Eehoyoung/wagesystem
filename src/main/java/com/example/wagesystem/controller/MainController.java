package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.MyPageDto;
import com.example.wagesystem.dto.ProfileDto;
import com.example.wagesystem.dto.ResignationDto;
import com.example.wagesystem.dto.WeeklyAllowanceResult;
import com.example.wagesystem.dto.attendance.AttendancePageDto;
import com.example.wagesystem.sservice.AttendanceServiceImpl;
import com.example.wagesystem.sservice.EmployeeServiceImpl;
import io.swagger.annotations.ApiOperation;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
public class MainController {

    private final EmployeeServiceImpl employeeService;

    private final AttendanceServiceImpl attendanceServiceImpl;

    private final AttendanceServiceImpl attendanceService;

    @Autowired
    public MainController(EmployeeServiceImpl employeeService, AttendanceServiceImpl attendanceServiceImpl, AttendanceServiceImpl attendanceService) {
        this.employeeService = employeeService;
        this.attendanceServiceImpl = attendanceServiceImpl;
        this.attendanceService = attendanceService;
    }

    @ApiOperation("메인 페이지")
    @GetMapping("/main/index")
    public String main() {
        return "main/index";
    }

    @ApiOperation("마이페이지")
    @GetMapping("/main/mypage")
    public String myPage(Principal principal, Model model) {
        String loginId = principal.getName();
        MyPageDto myPageDto = employeeService.showSimpleInfo(loginId);

        model.addAttribute("employee", myPageDto);

        return "main/mypage";
    }

    @ApiOperation("회원정보수정")
    @GetMapping("/main/profile")
    public String editDataPage(Principal principal, Model model, @ModelAttribute("employee") Employee employee) {
        String loginId = principal.getName();
        ProfileDto myProfileDto = employeeService.showProfileData(loginId);

        model.addAttribute("employee", myProfileDto);

        return "main/editdata";
    }

    @ApiOperation("회원정보수정 요청")
    @PutMapping("/update")
    public String editDataPage(Principal principal, @ModelAttribute("member") ProfileDto profileDto) {

        employeeService.updateProfile(principal.getName(), profileDto);

        return "redirect:/main/mypage";
    }

    @ApiOperation("회원탈퇴")
    @ResponseBody
    @DeleteMapping("/main/withdrawal/{loginPw}")
    public String withdrawalMember(HttpServletRequest request, Principal principal, @PathVariable(value = "loginPw") String password) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String loginId = principal.getName();

        Employee findEmployee = employeeService.findEmployeeLoginID(loginId);

//        퇴사자 정보 저장
        ResignationDto resignationDto = new ResignationDto();

        resignationDto.setName(findEmployee.getName());
        resignationDto.setResignationDate(LocalDate.now());
        resignationDto.setHireDate(findEmployee.getHireDate());
        resignationDto.setPhoneNumber(findEmployee.getPhoneNumber());

        boolean result = passwordEncoder.matches(password, findEmployee.getLoginPw());

        if (result) {
            employeeService.tempEmployeeResignation(resignationDto); //퇴사자 정보 저장
            employeeService.deleteEmployeeByLoginId(loginId);
            HttpSession session = request.getSession();
            session.invalidate();
            return "정상적으로 회원탈퇴되었습니다.";
        } else {
            return "비밀번호가 올바르지 않습니다";
        }
    }

    @GetMapping("/main/wage")
    public String getWagePage(Principal principal, Model model, @PageableDefault(size = 5) Pageable pageable) throws ObjectNotFoundException {
        String loginId = principal.getName();
        Employee findEmployee = employeeService.findEmployeeLoginID(loginId);

        BigDecimal totalWage = findEmployee.getMonthWage();
        BigDecimal tax = findEmployee.getMonthWage().multiply(BigDecimal.valueOf(0.033));

        AttendancePageDto attendancePageDto = attendanceServiceImpl.getWagePagingDto(loginId,pageable);
        YearMonth currentYearMonth = YearMonth.now();

        model.addAttribute("totalWage", totalWage);
        model.addAttribute("tax",tax);
        model.addAttribute("realWage", totalWage.subtract(tax));
        model.addAttribute("startPage", attendancePageDto.getHomeStartPage());
        model.addAttribute("endPage", attendancePageDto.getHomeEndPage());
        model.addAttribute("wageList", attendancePageDto.getPayboards());

        return "main/wage";
    }
}
