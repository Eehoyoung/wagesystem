package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    @Query("select e.employeeId from Employee e where e.loginId = :loginid")
    Long findByEmployeeId(@Param("loginid") String loginId);

    @Query("select e.employeeId from Employee e where e.employeeId = :employeeId")
    String findBySeachAllId(@Param("employeeId") Long employeeId);

    Optional<Employee> findByLoginId(String loginId);

//    void updateMonthWageByEmployeeId(Long employeeId, BigDecimal monthWage);

    void deleteByLoginId(String loginId);

    Page<Employee> findAllByOrderByHireDate(Pageable pageable);

    LocalDate findByStartWeeklyAllowance(Long employeeId);
    LocalDate findByEndWeeklyAllowance(Long employeeId);

}
