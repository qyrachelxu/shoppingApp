package com.beaconfire.compositeservice.domain.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {

    //String or Integer or ObjectId?
    private String contactId;

    private String firstName;

    private String lastName;

    //new
    private String middleName;

    private String cellPhone;

    private String alternatePhone;

    private String email;

    private String relationship;

    private String type;

}
