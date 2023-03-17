package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NameDTO {

    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    //TODO: profile picture

    private String ssn;

    private String dob;

    private String gender;
}
