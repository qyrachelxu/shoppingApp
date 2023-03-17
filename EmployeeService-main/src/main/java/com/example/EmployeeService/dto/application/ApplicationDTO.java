package com.example.EmployeeService.dto.application;

import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationDTO {
    private ApplicationFormDTO form;
    private List<DocumentDTO> documents;
}
