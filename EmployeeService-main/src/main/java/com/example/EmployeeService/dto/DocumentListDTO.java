package com.example.EmployeeService.dto;

import com.example.EmployeeService.domain.PersonalDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DocumentListDTO {
    private List<PersonalDocument> personalDocuments;
}
