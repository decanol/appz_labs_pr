package com.coursework.demo.service.impl;

import com.coursework.demo.entity.Employee;
import com.coursework.demo.repository.EmployeeRepository;
import com.coursework.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee getById(Long id) {
        return employeeRepository.findById(id).get();
    }

    @Override
    public List<Employee> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).getContent();
    }

    @Override
    public Employee save(Employee object) {
        return employeeRepository.save(object);
    }

    @Override
    public Employee delete(Employee object) {
        employeeRepository.delete(object);
        return object;
    }
}
