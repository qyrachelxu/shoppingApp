package com.example.EmployeeService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {

    //String or Integer or ObjectId?
    private String contactId;

    @NotBlank(message = "contact firstName is required")
    private String firstName;

    @NotBlank(message = "contactlastName is required")
    private String lastName;

    //new
    private String middleName;

    @NotBlank(message = "contact cellPhone is required")
    private String cellPhone;

    private String alternatePhone;

    @NotBlank(message = "contact email is required")
    private String email;

    @NotBlank(message = "contact relationship is required")
    private String relationship;

    private String type;

}
