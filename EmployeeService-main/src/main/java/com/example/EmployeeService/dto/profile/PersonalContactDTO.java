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
public class PersonalContactDTO {

    @NotBlank(message = "cell phone is required")
    private String cellPhone;

    private String alternatePhone;
}
