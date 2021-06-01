package com.coursework.demo.mapper;

import com.coursework.demo.dto.AddEmployeeDTO;
import com.coursework.demo.dto.EmployeeDTO;
import com.coursework.demo.entity.Employee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO convertToDto(Employee employee);

    Employee convertToEntity(EmployeeDTO employeeDTO);

    Employee convertToEntity(AddEmployeeDTO employeeDTO);

    List<EmployeeDTO> convertToDtoList(List<Employee> employees);

}
