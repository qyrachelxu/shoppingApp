package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddressDTO {
    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zipcode;
}