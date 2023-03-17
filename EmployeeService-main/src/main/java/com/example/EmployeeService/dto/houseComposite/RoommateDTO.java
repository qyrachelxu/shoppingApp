package com.example.EmployeeService.dto.houseComposite;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoommateDTO {
    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String cellPhone;

    private String alternatePhone;
}
