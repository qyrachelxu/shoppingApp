package com.beaconfire.housingservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FacilityInput {
    private String type;
    private String description;
    private Integer quantity;
}
