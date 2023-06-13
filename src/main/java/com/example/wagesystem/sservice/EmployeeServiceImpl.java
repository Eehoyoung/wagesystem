package com.example.wagesystem.sservice;

import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.*;
import com.example.wagesystem.exeption.LoginIdNotFoundException;
import com.example.wagesystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void updateEmployeeId(Long employeeId, Long newEmployeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            employee.setEmployeeId(newEmployeeId);
            employeeRepository.save(employee);
        }
    }

    @Override
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new LoginIdNotFoundException("해당하는 사원이 존재하지 않습니다."));
    }

    @Override
    public Employee findEmployeeLoginID(String loginId) {
        return employeeRepository.findByLoginId(loginId).orElseThrow(() -> new LoginIdNotFoundException("해당하는 사원이 존재하지 않습니다."));
    }

    @Transactional
    @Override
    public Long joinEmployee(EmployeeInfoDto employeeInfoDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        employeeInfoDto.setLoginPw(encoder.encode(employeeInfoDto.getLoginPw()));
        employeeInfoDto.setPosition(Position.STAFF);

        return employeeRepository.save(employeeInfoDto.toEntity()).getEmployeeId();
    }

    @Transactional
    @Override
    public void updateProfile(String loginId, ProfileDto profileDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Employee findEmployee = employeeRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다.")
        );

        String emPhoneNumberResult = profileDto.getEmPhoneNumber()[0] + "," + profileDto.getEmPhoneNumber()[1] + "," + profileDto.getEmPhoneNumber()[2];
        String phoneNumberResult = profileDto.getPhoneNumber()[0] + "," + profileDto.getPhoneNumber()[1] + "," + profileDto.getPhoneNumber()[2];

        findEmployee.setName(profileDto.getName());
        findEmployee.setLoginId(profileDto.getLoginId());
        findEmployee.setLoginPw(encoder.encode(profileDto.getLoginPw()));
        findEmployee.setEmPhoneNumber(emPhoneNumberResult);
        findEmployee.setEmPhoneNumber(phoneNumberResult);

    }

    @Transactional
    @Override
    public Long changePw(Long id, String password) {
        Employee employee = findEmployeeById(id);
        employee.setLoginPw(password);
        return employee.getEmployeeId();
    }

    @Override
    public MyPageDto showSimpleInfo(String loginId) {
        BigDecimal monthWage = BigDecimal.valueOf(0);
        BigDecimal tax;
        MyPageDto myPageDto = new MyPageDto();

        Employee findMember = employeeRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다")
        );

        for (int i = 0; i < findMember.getPayList().size(); i++) {
            monthWage.add(findMember.getPayList().get(i).getEmployee().getDailyWage());
        }

        tax = monthWage.multiply(BigDecimal.valueOf(0.033));

        myPageDto.setName(findMember.getName());
        myPageDto.setPosition(findMember.getPosition());
        myPageDto.setPay(monthWage.subtract(tax));
        myPageDto.setHourWage(findMember.getHourwage());

        return myPageDto;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean doubleCheckId(String registerId) {
        Optional<Employee> findEmployee = employeeRepository.findByLoginId(registerId);
        return !findEmployee.isPresent();
    }

    @Transactional
    @Override
    public void deleteEmployeeByLoginId(String loginId) {
        employeeRepository.deleteByLoginId(loginId);
    }

    @Transactional
    @Override
    public Long deleteById(Long id) {
        employeeRepository.deleteById(id);
        return id;
    }

    @Override
    public ProfileDto showProfileData(String loginId) {
        ProfileDto profileDto = new ProfileDto();

        Employee findEmployee = employeeRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다.")
        );

        String emPhoneNumber = findEmployee.getEmPhoneNumber();
        String[] emPhoneNumberArr = emPhoneNumber.split(",");
        String phoneNumber = findEmployee.getPhoneNumber();
        String[] phoneNumberArr = phoneNumber.split(",");
        String birthday = findEmployee.getBirthday();
        String[] birthdayArr = birthday.split(",");

        profileDto.setName(findEmployee.getName());
        profileDto.setLoginId(findEmployee.getLoginId());
        profileDto.setEmPhoneNumber(emPhoneNumberArr);
        profileDto.setPhoneNumber(phoneNumberArr);
        profileDto.setBirthday(birthdayArr);

        return profileDto;
    }

    @Override
    public Page<Employee> findAllMemberByOrderByCreatedAt(Pageable pageable) {
        return employeeRepository.findAllByOrderByHireDate(pageable);
    }

    @Override
    public EmployeePageDto findAllEmployePaging(Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<EmployeeDto> employeeBoards = employeeRepository.searchAll(pageable);
        int startPage = Math.max(1, employeeBoards.getPageable().getPageNumber());
        int endPage = Math.min(employeeBoards.getTotalPages(), employeeBoards.getPageable().getPageNumber() + 5);

        employeePageDto.setEmployeeBoards(employeeBoards);
        employeePageDto.setStartPage(startPage);
        employeePageDto.setEndPage(endPage);

        return employeePageDto;
    }

    @Override
    public EmployeePageDto findAllEmployeeByConditionByPaging(SearchEmployee searchEmployee, Pageable pageable) {
        EmployeePageDto employeePageDto = new EmployeePageDto();
        Page<EmployeeDto> employeeBoards = employeeRepository.searchByCondition(searchEmployee, pageable);

        int startPage = Math.max(1, employeeBoards.getPageable().getPageNumber() - 2);
        int endPage = Math.min(employeeBoards.getTotalPages(), startPage + 4);

        employeePageDto.setEmployeeBoards(employeeBoards);
        employeePageDto.setStartPage(startPage);
        employeePageDto.setEndPage(endPage);

        return employeePageDto;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<Employee> employeeEntityWrapper = employeeRepository.findByLoginId(loginId);
        Employee employeeEntity = employeeEntityWrapper.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("admin@example.com").equals(loginId)) {
            authorities.add(new SimpleGrantedAuthority(Position.DEPUTY.getValue()));
            employeeEntity.setPosition(Position.DEPUTY);
        } else {
            authorities.add(new SimpleGrantedAuthority(Position.STAFF.getValue()));
            employeeEntity.setPosition(Position.STAFF);
        }
        return new User(employeeEntity.getLoginId(), employeeEntity.getLoginPw(), authorities);
    }
}
