package com.example.wagesystem.sservice;

import com.example.wagesystem.dto.AttendanceDto;

public interface AttendanceService {
    AttendanceDto startWork(Long employeeId);

    AttendanceDto endWork(Long employeeId);
}