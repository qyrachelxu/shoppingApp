package com.example.EmployeeService.dto.profile;

import com.example.EmployeeService.dto.profile.emergencyContact.EmergencyContactDTO;
import com.example.EmployeeService.dto.profile.address.AddressDTO;
import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import com.example.EmployeeService.dto.profile.employment.VisaDTO;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Language;

import javax.validation.Valid;
import java.util.List;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProfileDTO {
    @Valid
    private NameDTO name;

    @Valid
    private List<AddressDTO> addresses;

    @Valid
    private PersonalContactDTO contactInfo;

    @Valid
    private List<VisaDTO> employment;

    @Valid
    private List<EmergencyContactDTO> emergencyContact;

    private List<DocumentDTO> documents;

}
