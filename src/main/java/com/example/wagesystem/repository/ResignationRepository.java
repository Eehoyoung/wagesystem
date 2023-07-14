package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Resignation;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.EmployeeDto;
import com.example.wagesystem.dto.ResignationEmpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignationRepository extends JpaRepository<Resignation, Long> {

    Page<ResignationEmpDto> searchAll(Pageable pageable);

//    Page<ResignationEmpDto> searchByCondition(SearchEmployee search, Pageable pageable);
}
