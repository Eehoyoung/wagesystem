package com.example.wagesystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class EmployeePageDto {

    Page<EmployeeDto> employeeBoards;

    int startPage;

    int endPage;
}
