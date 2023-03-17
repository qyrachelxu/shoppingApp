package com.beaconfire.housingservice.domain;

import com.beaconfire.housingservice.domain.entity.Landlord;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HouseInput {
    private String address;
    private Landlord landlord;
    private Integer maxOccupant;
    private List<FacilityInput> facilityInputList;
}
