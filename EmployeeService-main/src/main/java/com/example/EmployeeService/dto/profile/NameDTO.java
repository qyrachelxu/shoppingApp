package com.example.EmployeeService.dto.profile;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NameDTO {

    @NotBlank(message = "firstName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    //TODO: profile picture

    @NotBlank(message = "ssn is required")
    private String ssn;

    @NotBlank(message = "dob is required")
    private String dob;

    private String gender;
}
