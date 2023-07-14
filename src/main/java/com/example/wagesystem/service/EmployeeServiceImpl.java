package com.example.wagesystem.service;

import com.example.wagesystem.domain.Employee;
import com.example.wagesystem.domain.Resignation;
import com.example.wagesystem.domain.SearchEmployee;
import com.example.wagesystem.dto.*;
import com.example.wagesystem.exception.LoginIdNotFoundException;
import com.example.wagesystem.repository.EmployeeRepository;
import com.example.wagesystem.repository.ResignationRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    private EmployeeRepository employeeRepository;

    private ResignationRepository resignationRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ResignationRepository resignationRepository) {
        this.employeeRepository = employeeRepository;
        this.resignationRepository = resignationRepository;
    }

    @Override
    @Transactional
    public void updateMonthWage(Long employeeId, EmployeeInfoDto employeeInfoDto) throws ObjectNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ObjectNotFoundException("해당하는 사원이 존재하지 않습니다."));
        employee.setMonthWage(employeeInfoDto.getMonthWage());
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Long tempEmployeeResignation(ResignationDto resignationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Long employeeId = employeeRepository.findByEmployeeId(loginId);
        // 기존 직원 데이터를 가져옵니다.
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("No employee found with Id " + employeeId));

        // 퇴사자 데이터를 생성하고 값을 설정합니다.
        Resignation resignation = new Resignation(resignationDto.getREmployeeId(),
                employee.getName(), employee.getPhoneNumber(), employee.getHireDate(), LocalDate.now());

        // 퇴사자 데이터를 저장합니다.
        resignationRepository.save(resignation);

        return resignation.getREmployeeId();
    }

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
        employeeInfoDto.setHourWage(BigDecimal.valueOf(9620));
        employeeInfoDto.setStartWeeklyAllowance(LocalDate.now());
        employeeInfoDto.setEndWeeklyAllowance(LocalDate.now().plusDays(6));
        employeeInfoDto.setMonthWage(BigDecimal.ZERO);

        return employeeRepository.save(employeeInfoDto.toEntity()).getEmployeeId();
    }

    @Transactional
    @Override
    public void updateProfile(String loginId, ProfileDto profileDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Employee findEmployee = employeeRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다.")
        );

        String emPhoneNumberResult = "";
        String phoneNumberResult = "";
        String birthdayResult = "";

        if (profileDto.getBirthday().length >= 3) {
            birthdayResult = profileDto.getBirthday()[0] + "," + profileDto.getBirthday()[1] + "," + profileDto.getBirthday()[2];
        }

        if (profileDto.getEmPhoneNumber().length >= 3) {
            emPhoneNumberResult = profileDto.getEmPhoneNumber()[0] + "," + profileDto.getEmPhoneNumber()[1] + "," + profileDto.getEmPhoneNumber()[2];
        }

        if (profileDto.getPhoneNumber().length >= 3) {
            phoneNumberResult = profileDto.getPhoneNumber()[0] + "," + profileDto.getPhoneNumber()[1] + "," + profileDto.getPhoneNumber()[2];
        }

        findEmployee.setBirthday(birthdayResult);
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
        Employee findMember = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다"));


        return MyPageDto.builder()
                .name(findMember.getName())
                .position(findMember.getPosition())
                .pay(findMember.getMonthWage())
                .hourWage(findMember.getHourwage())
                .build();
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

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<Employee> employeeEntityWrapper = employeeRepository.findByLoginId(loginId);
        Employee employeeEntity = employeeEntityWrapper.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("c65621").equals(loginId)) {
            authorities.add(new SimpleGrantedAuthority(Position.MANAGER.getValue()));
            employeeEntity.setPosition(Position.MANAGER);
        } else {
            authorities.add(new SimpleGrantedAuthority(Position.STAFF.getValue()));
            employeeEntity.setPosition(Position.STAFF);
        }
        return new User(employeeEntity.getLoginId(), employeeEntity.getLoginPw(), authorities);
    }
}
