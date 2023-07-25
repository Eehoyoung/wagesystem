package com.example.wagesystem.repository;

//import com.example.wagesystem.domain.SearchEmployee;

import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.employee.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {

    Page<EmployeeDto> searchAll(Pageable pageable);

    Page<EmployeeDto> searchByCondition(SearchEmployee search, Pageable pageable);

}
