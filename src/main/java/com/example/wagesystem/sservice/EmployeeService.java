package com.example.wagesystem.sservice;


import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.EmployeeInfoDto;
import com.example.wagesystem.dto.EmployeePageDto;
import com.example.wagesystem.dto.MyPageDto;
import com.example.wagesystem.dto.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    void updateEmployeeId(Long employeeId, Long newEmployeeId);

    Employee findEmployeeById(Long id);

    Employee findEmployeeLoginID(String loginId);

    Long joinEmployee(EmployeeInfoDto employeeInfoDto);

    void updateProfile(String loginId, ProfileDto profileDto);

    Long changePw(Long id, String password);

    MyPageDto showSimpleInfo(String loginId);

    boolean doubleCheckId(String registerId);

    void deleteEmployeeByLoginId(String loginId);

    Long deleteById(Long id);

    ProfileDto showProfileData(String loginId);

    Page<Employee> findAllMemberByOrderByCreatedAt(Pageable pageable);

    EmployeePageDto findAllEmployePaging(Pageable pageable);

    EmployeePageDto findAllEmployeeByConditionByPaging(SearchEmployee searchEmployee, Pageable pageable);


}
