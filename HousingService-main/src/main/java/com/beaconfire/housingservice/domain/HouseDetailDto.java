package com.beaconfire.housingservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HouseDetailDto {
    private String address;
    private List<EmployeeDTO> roommates;
}
