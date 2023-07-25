package com.example.wagesystem.repository;

import com.example.wagesystem.domain.QEmployee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.QEmployeeDto;
import com.example.wagesystem.dto.employee.EmployeeDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public EmployeeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<EmployeeDto> searchAll(Pageable pageable) {
        QueryResults<EmployeeDto> results = queryFactory
                .select(new QEmployeeDto(
                        QEmployee.employee.employeeId,
                        QEmployee.employee.name,
                        QEmployee.employee.position,
                        QEmployee.employee.store,
                        QEmployee.employee.phoneNumber,
                        QEmployee.employee.hireDate,
                        QEmployee.employee.hourwage,
                        QEmployee.employee.EmPhoneNumber
                ))
                .from(QEmployee.employee)
                .orderBy(QEmployee.employee.hireDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<EmployeeDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<EmployeeDto> searchByCondition(SearchEmployee search, Pageable pageable) {
        QueryResults<EmployeeDto> results = null;

        if (search.getSearchCondition().equals("employeeId")) {
            results = queryFactory
                    .select(new QEmployeeDto(
                            QEmployee.employee.employeeId,
                            QEmployee.employee.name,
                            QEmployee.employee.position,
                            QEmployee.employee.store,
                            QEmployee.employee.phoneNumber,
                            QEmployee.employee.hireDate,
                            QEmployee.employee.hourwage,
                            QEmployee.employee.EmPhoneNumber
                    ))
                    .from(QEmployee.employee)
                    .where(loginIdEq(search.getSearchKeyWord()))
                    .orderBy(QEmployee.employee.hireDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        } else if (search.getSearchCondition().equals("name")) {
            results = queryFactory
                    .select(new QEmployeeDto(
                            QEmployee.employee.employeeId,
                            QEmployee.employee.name,
                            QEmployee.employee.position,
                            QEmployee.employee.store,
                            QEmployee.employee.phoneNumber,
                            QEmployee.employee.hireDate,
                            QEmployee.employee.hourwage,
                            QEmployee.employee.EmPhoneNumber
                    ))
                    .from(QEmployee.employee)
                    .where(nameEq(search.getSearchKeyWord()))
                    .orderBy(QEmployee.employee.hireDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        }

        List<EmployeeDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression loginIdEq(String loginIdCondition) {
        if (StringUtils.isEmpty(loginIdCondition)) {
            return null;
        }
        return QEmployee.employee.loginId.likeIgnoreCase("%" + loginIdCondition + "%");
    }

    private BooleanExpression nameEq(String nameCondition) {
        if (StringUtils.isEmpty(nameCondition)) {
            return null;
        }
        return QEmployee.employee.name.likeIgnoreCase("%" + nameCondition + "%");
    }

}
