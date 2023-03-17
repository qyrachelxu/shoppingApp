package com.example.EmployeeService.controller;

import com.example.EmployeeService.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HrController.class)
public class HrControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean       // this annotation is provided by Spring Boot
    EmployeeService employeeService;
}
