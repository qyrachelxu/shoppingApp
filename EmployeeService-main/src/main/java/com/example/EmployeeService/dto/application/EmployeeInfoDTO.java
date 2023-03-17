package com.example.EmployeeService.dto.application;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmployeeInfoDTO {
    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    private int userID;
}
