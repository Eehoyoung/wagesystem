package com.example.wagesystem.controller;

import com.example.wagesystem.dto.EmployeeInfoDto;
import com.example.wagesystem.service.EmployeeServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private EmployeeServiceImpl employeeService;

    @Autowired
    public LoginController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @ApiOperation("로그인")
    @GetMapping("/main/login")
    public String loginPage(HttpServletRequest request, @RequestParam(value = "error", required = false) String error, Model model) {

        String referer = request.getHeader("Referer");

        if (referer != null) {
            request.getSession().setAttribute("prevPage", referer);
        } else {
            referer = "http://localhost:8080/main/index";
            request.getSession().setAttribute("prevPage", referer);
        }
        model.addAttribute("error", error);
        return "main/login";

    }

    @ApiOperation("회원가입 요청")
    @GetMapping("main/register")
    public String getRegisterPage() {
        return "main/register_user";
    }

    @ApiOperation("회원가입")
    @PostMapping("main/register")
    public String doRegisterPage(EmployeeInfoDto employeeInfoDto) {
        Long memberId = employeeService.joinEmployee(employeeInfoDto);

        return "redirect:/main/login";
    }

    @GetMapping("/defaultUrl")
    public String loginRedirectPage(HttpServletRequest request) {
        request.getHeader("Referer");
        String referer;

        referer = "http://localhost:8080/main/index";
        request.getSession().setAttribute("prevPage", referer);

        return "redirect:/main/index";
    }

    @ApiOperation("아이디 중복 확인")
    @ResponseBody
    @PostMapping("/main/register/doublecheck")
    public String inDoubleCheck(@RequestParam(value = "registerId") String registerId) {
        if (employeeService.doubleCheckId(registerId)) {
            return "사용 가능한 아이디 입니다.";
        } else {
            return "이미 사용중인 아이디 입니다.";
        }
    }
}
