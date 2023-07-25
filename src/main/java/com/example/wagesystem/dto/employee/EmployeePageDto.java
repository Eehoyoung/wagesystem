package com.example.wagesystem.dto.employee;

import com.example.wagesystem.dto.resignation.ResignationEmpDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class EmployeePageDto {

    Page<EmployeeDto> employeeBoards;

    Page<ResignationEmpDto> reEmployeeBoards;

    int startPage;

    int endPage;
}
