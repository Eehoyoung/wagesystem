package com.example.wagesystem.repository;

import com.example.wagesystem.domain.QResignation;
import com.example.wagesystem.domain.SearchResignation;
import com.example.wagesystem.dto.QResignationEmpDto;
import com.example.wagesystem.dto.resignation.ResignationEmpDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class ResignationRepositoryImpl implements ResignationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ResignationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ResignationEmpDto> searchAll(Pageable pageable) {
        QueryResults<ResignationEmpDto> results = queryFactory
                .select(new QResignationEmpDto(
                        QResignation.resignation.rEmployeeId,
                        QResignation.resignation.name,
                        QResignation.resignation.hireDate,
                        QResignation.resignation.resignationDate,
                        QResignation.resignation.phoneNumber
                ))
                .from(QResignation.resignation)
                .orderBy(QResignation.resignation.resignationDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ResignationEmpDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ResignationEmpDto> searchByCondition(SearchResignation search, Pageable pageable) {
        QueryResults<ResignationEmpDto> results = null;

        if (search.getSearchCondition().equals("rEmployeeId")) {
            results = queryFactory
                    .select(new QResignationEmpDto(
                            QResignation.resignation.rEmployeeId,
                            QResignation.resignation.name,
                            QResignation.resignation.hireDate,
                            QResignation.resignation.resignationDate,
                            QResignation.resignation.phoneNumber
                    ))
                    .from(QResignation.resignation)
                    .where(loginIdEq(search.getSearchKeyWord()))
                    .orderBy(QResignation.resignation.resignationDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        } else if (search.getSearchCondition().equals("name")) {
            results = queryFactory
                    .select(new QResignationEmpDto(
                            QResignation.resignation.rEmployeeId,
                            QResignation.resignation.name,
                            QResignation.resignation.hireDate,
                            QResignation.resignation.resignationDate,
                            QResignation.resignation.phoneNumber
                    ))
                    .from(QResignation.resignation)
                    .where(loginIdEq(search.getSearchKeyWord()))
                    .orderBy(QResignation.resignation.resignationDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        }

        List<ResignationEmpDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression loginIdEq(String loginIdCondition) {
        if (StringUtils.isEmpty(loginIdCondition)) {
            return null;
        }
        return QResignation.resignation.rEmployeeId.like("%" + loginIdCondition + "%");
    }

    private BooleanExpression nameEq(String nameCondition) {
        if (StringUtils.isEmpty(nameCondition)) {
            return null;
        }
        return QResignation.resignation.name.likeIgnoreCase("%" + nameCondition + "%");
    }

}
