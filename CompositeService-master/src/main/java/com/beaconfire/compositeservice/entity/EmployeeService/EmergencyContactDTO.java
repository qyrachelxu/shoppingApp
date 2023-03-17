package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmergencyContactDTO {
    private String firstName;

    private String lastName;

    //new
    private String middleName;

    private String cellPhone;

    private String alternatePhone;

    private String email;

    private String relationship;
}