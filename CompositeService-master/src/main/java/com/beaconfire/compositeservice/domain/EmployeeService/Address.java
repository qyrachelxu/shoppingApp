package com.beaconfire.compositeservice.domain.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {

    //String or Integer or ObjectId?
    private String addressId;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zipcode;
}