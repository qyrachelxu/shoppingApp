package com.example.EmployeeService.dto.profile.employment;

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
public class VisaDTO {
    private String visaType;

    private String startDate;

    private String endDate;
}
