package com.example.wagesystem.controller;

import com.example.wagesystem.service.AttendanceServiceImpl;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final AttendanceServiceImpl attendanceService;

    public TestController(AttendanceServiceImpl attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/runCalculation")
    public ResponseEntity<Void> runWeeklyAllowanceCalculation() {
        try {
            attendanceService.calculateWeeklyAllowances();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
