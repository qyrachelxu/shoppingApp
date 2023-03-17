package com.beaconfire.housingservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmployeeDTO {
    private String legalFullName;
    private String phoneNumber;
}
