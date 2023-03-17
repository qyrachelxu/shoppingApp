package com.example.EmployeeService.response;

import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetOPTResponse {
    private List<DocumentDTO> approvedDocuments;
    private MessageResponse messageResponse;
}
