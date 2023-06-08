package com.example.wagesystem.controller;

import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.MyPageDto;
import com.example.wagesystem.dto.ProfileDto;
import com.example.wagesystem.repository.EmployeeRepository;
import com.example.wagesystem.sservice.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class MainController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @GetMapping("/main/index")
    public String main() {
        return "main/index";
    }

    @GetMapping("/main/mypage")
    public String myPage(Principal principal, Model model) {
        String loginId = principal.getName();
        MyPageDto myPageDto = employeeService.showSimpleInfo(loginId);

        model.addAttribute("employee", myPageDto);

        return "main/mypage";
    }

    @GetMapping("/main/profile")
    public String editDataPage(Principal principal, Model model, @ModelAttribute("member") Employee employee) {
        String loginId = principal.getName();
        ProfileDto myProfileDto = employeeService.showProfileData(loginId);

        model.addAttribute("member", myProfileDto);

        return "main/editdata";
    }

    @PutMapping("/update")
    public String editDataPage(Principal principal, @ModelAttribute("member") ProfileDto profileDto) {

        employeeService.updateProfile(principal.getName(), profileDto);

        return "redirect:/main/mypage";
    }

    @ResponseBody
    @DeleteMapping("/main/withdrawal")
    public String withdrawalMember(HttpServletRequest request, Principal principal, @RequestParam(value = "login_Pw") String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String loginId = principal.getName();
        Employee findEmployee = employeeService.findEmployeeLoginID(loginId);

        boolean result = passwordEncoder.matches(password, findEmployee.getLoginPw());

        if (result) {
            employeeService.deleteEmployeeByLoginId(loginId);
            HttpSession session = request.getSession();
            session.invalidate();
            return "정상적으로 회원탈퇴되었습니다.";
        } else {
            return "비밀번호가 올바르지 않습니다";
        }
    }
}
