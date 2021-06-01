package com.coursework.demo.repository;

import com.coursework.demo.entity.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
