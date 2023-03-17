package com.example.EmployeeService.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reference {
    @NotBlank(message = "reference firstName is required")
    private String firstName;

    @NotBlank(message = "reference lastName is required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "reference phone is required")
    private String phone;

    @NotBlank(message = "reference email is required")
    private String email;

    @NotBlank(message = "reference relationship is required")
    private String relationship;
}
