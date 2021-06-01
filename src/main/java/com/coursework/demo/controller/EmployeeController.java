package com.coursework.demo.controller;

import com.coursework.demo.dto.AddEmployeeDTO;
import com.coursework.demo.dto.EmployeeDTO;
import com.coursework.demo.entity.Employee;
import com.coursework.demo.mapper.EmployeeMapper;
import com.coursework.demo.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "Employee API")
@RequestMapping("/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get employee info by id")
    public ResponseEntity<EmployeeDTO> get(@PathVariable("id") long id){
        Employee employee = employeeService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(employeeMapper.convertToDto(employee));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN, OWNER, WORKER, BOOKKEEPER')")
    @ApiOperation(value = "Get the list of all employees")
    public ResponseEntity<List<EmployeeDTO>> getPage(@PageableDefault(sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok().body(employeeMapper.convertToDtoList(employeeService.getAll(pageable)));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Create new employee")
    public ResponseEntity<EmployeeDTO> save(@RequestBody AddEmployeeDTO addEmployeeDTO) {
        Employee employee = employeeService.save(employeeMapper.convertToEntity(addEmployeeDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.convertToDto(employee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update existing employee by id")
    public ResponseEntity<EmployeeDTO> update(@PathVariable("id") long id, @RequestBody EmployeeDTO employeeDTO) {
        if (id == employeeDTO.getId()) {
            Employee employee = employeeService.save(employeeMapper.convertToEntity(employeeDTO));
            return ResponseEntity.status(HttpStatus.OK).body(employeeMapper.convertToDto(employee));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete employee by id")
    public ResponseEntity delete(@PathVariable("id") long id){
        Employee employee = employeeService.getById(id);
        employeeService.delete(employee);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
