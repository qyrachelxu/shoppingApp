package com.example.EmployeeService.dto;

import com.example.EmployeeService.domain.VisaStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VisaListDTO {
    private List<VisaStatus> visaStatuses;
}
