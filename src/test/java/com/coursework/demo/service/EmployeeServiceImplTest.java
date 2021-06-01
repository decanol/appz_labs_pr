package com.coursework.demo.service;

import com.coursework.demo.entity.Employee;
import com.coursework.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmployeeServiceImplTest {
    
    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testGetById() {
        final Employee employee = getEmployee();

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        final Employee result = employeeService.getById(1L);

        assertEquals(employee, result);
        verify(employeeRepository).findById(anyLong());
    }

    @Test
    public void testGetAll() {
        final Employee employee = getEmployee();
        final List<Employee> employeeList = Collections.singletonList(employee);
        final Pageable pageable = PageRequest.of(0, 5);
        final Page<Employee> employees = new PageImpl<>(employeeList, pageable, 5);

        when(employeeRepository.findAll(pageable)).thenReturn(employees);

        final List<Employee> result = employeeService.getAll(pageable);

        assertEquals(employeeList, result);
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    public void testSave() {
        final Employee employee = getEmployee();

        when(employeeRepository.save(employee)).thenReturn(employee);

        final Employee result = employeeService.save(employee);

        assertEquals(employee, result);
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testDelete() {
        final Employee employee = getEmployee();

        doNothing().when(employeeRepository).delete(employee);

        final Employee result = employeeService.delete(employee);

        assertEquals(employee, result);
        verify(employeeRepository).delete(employee);
    }
}
