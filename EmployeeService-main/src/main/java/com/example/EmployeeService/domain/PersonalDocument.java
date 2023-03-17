package com.example.EmployeeService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonalDocument {

    private String personalDocumentId;

    private String type;

    private String path;

    private String title;

    private String comment;

    private String createDate;

    //new
    private String documentStatus;
}
