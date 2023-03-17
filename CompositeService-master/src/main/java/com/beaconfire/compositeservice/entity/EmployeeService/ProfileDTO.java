package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProfileDTO {
    private NameDTO name;
    private List<AddressDTO> addresses;
    private PersonalContactDTO contactInfo;
    private List<VisaDTO> employment;
    private List<EmergencyContactDTO> emergencyContact;
    private List<DocumentDTO> documents;

}