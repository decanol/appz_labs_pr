package com.coursework.demo.it;

import com.coursework.demo.dto.EmployeeDTO;
import com.coursework.demo.entity.Employee;
import com.coursework.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getEmployee;
import static com.coursework.demo.TestData.getEmployeeRequest;
import static com.coursework.demo.it.TestUtils.asJsonString;
import static com.coursework.demo.it.TestUtils.deleteRequest;
import static com.coursework.demo.it.TestUtils.getRequest;
import static com.coursework.demo.it.TestUtils.postRequest;
import static com.coursework.demo.it.TestUtils.putRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeControllerIT {

    private static final String EMPLOYEE_CONTROLLER_PATH = "/v1/employees/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @WithMockUser(roles = "OWNER")
    public void testRetrieveEmployeeById() throws Exception {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(getEmployee()));

        mockMvc.perform(getRequest(EMPLOYEE_CONTROLLER_PATH + "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getEmployeeRequest())));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void testRetrieveEmployeeList() throws Exception {
        final Employee employee = getEmployee();
        final List<Employee> employees = Collections.singletonList(employee);
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        final Page<Employee> employeePage = new PageImpl<>(employees, pageable, 10);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        mockMvc.perform(getRequest(EMPLOYEE_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Collections.singletonList(getEmployeeRequest()))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSaveEmployee() throws Exception {
        final Employee employee = getEmployee();
        final EmployeeDTO request = getEmployeeRequest();

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(postRequest(EMPLOYEE_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateEmployee() throws Exception {
        final Employee employee = getEmployee();
        final EmployeeDTO request = getEmployeeRequest();

        when(employeeRepository.save(employee)).thenReturn(employee);

        mockMvc.perform(putRequest(EMPLOYEE_CONTROLLER_PATH + "1", request))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(request)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateEmployeeExpectedBadRequest() throws Exception {
        final EmployeeDTO request = getEmployeeRequest();

        mockMvc.perform(putRequest(EMPLOYEE_CONTROLLER_PATH + "2", request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteEmployee() throws Exception {
        final Employee employee = getEmployee();

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        mockMvc.perform(deleteRequest(EMPLOYEE_CONTROLLER_PATH + "1"))
                .andExpect(status().isNoContent());
    }
}
