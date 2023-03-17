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
public class EmployeeVisaDTO {

    private String fullName;

    private String workAuthorizationType;

    private String expirationDate;

    private int daysLeft;
}