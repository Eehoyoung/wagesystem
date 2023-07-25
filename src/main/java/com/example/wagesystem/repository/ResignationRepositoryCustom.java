package com.example.wagesystem.repository;


import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.resignation.ResignationEmpDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResignationRepositoryCustom {

    Page<ResignationEmpDto> searchAll(Pageable pageable);

    Page<ResignationEmpDto> searchByCondition(SearchResignation search, Pageable pageable);

}
