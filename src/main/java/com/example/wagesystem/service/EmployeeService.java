package com.example.wagesystem.service;


import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.dto.employee.EmployeeInfoDto;
import com.example.wagesystem.dto.employee.MyPageDto;
import com.example.wagesystem.dto.employee.ProfileDto;
import com.example.wagesystem.dto.resignation.ResignationDto;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {


    void updateMonthWage(Long employeeId, EmployeeInfoDto employeeInfoDto) throws ObjectNotFoundException;

    Long tempEmployeeResignation(ResignationDto resignationDto);

    Employee findEmployeeById(Long id);

    Employee findEmployeeLoginID(String loginId);

    Long joinEmployee(EmployeeInfoDto employeeInfoDto);

    void updateProfile(String loginId, ProfileDto profileDto);

    MyPageDto showSimpleInfo(String loginId);

    boolean doubleCheckId(String registerId);

    void deleteEmployeeByLoginId(String loginId);

    ProfileDto showProfileData(String loginId);


}
