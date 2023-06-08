package com.example.wagesystem.controller;

import com.example.wagesystem.dto.AttendanceDto;
import com.example.wagesystem.sservice.AttendanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendanceController {
    @Autowired
    private AttendanceServiceImpl attendanceService;

    @PostMapping("/start-work")
    public AttendanceDto startWork(@RequestParam(name = "employeeId") Long employeeId) {
        return attendanceService.startWork(employeeId);
    }

    @PostMapping("/end-work")
    public AttendanceDto endWork(@RequestParam(name = "employeeId") Long employeeId) {
        return attendanceService.endWork(employeeId);
    }
}