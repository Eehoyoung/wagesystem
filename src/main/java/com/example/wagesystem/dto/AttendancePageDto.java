package com.example.absenteeismerp.dto;

import com.example.absenteeismerp.model.Attendance;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class AttendancePageDto {
    Page<Attendance> payboards;

    int homeStartPage;

    int homeEndPage;
}
