package com.example.wagesystem.dto.attendance;

import com.example.wagesystem.domain.Attendance;
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
