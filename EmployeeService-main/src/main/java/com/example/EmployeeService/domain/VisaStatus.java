package com.example.EmployeeService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VisaStatus {

    private String visaStatusId;

    private String visaType;

    private boolean activeFlag;

    private String startDate;

    private String endDate;

    private String lastModificationDate;
}
