package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmployeeInfoDTO {
    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    private int userID;
}
