package com.example.EmployeeService.dto.profile.employment;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmployeeSummaryDTO {
    private String fullName;
    private String ssn;
    private String workAythorizationType;
    private String cellPhone;
    private String email;
}
