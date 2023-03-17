package com.example.EmployeeService.response;

import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.dto.application.ApplicationDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplicationResponse {
    private MessageResponse messageResponse;
    private ApplicationDTO application;
}
