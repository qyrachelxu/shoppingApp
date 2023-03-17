package com.example.EmployeeService.dto.profile.emergencyContact;

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
@Builder
public class EmergencyContactDTO {
    @NotBlank(message = "fistName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    //new
    private String middleName;

    @NotBlank(message = "cellphone is required")
    private String cellPhone;

    private String alternatePhone;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "relationship is required")
    private String relationship;
}
