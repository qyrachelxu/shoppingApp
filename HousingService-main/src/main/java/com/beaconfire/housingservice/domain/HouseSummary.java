package com.beaconfire.housingservice.domain;

import lombok.*;

@Getter
@Setter
@Builder
public class HouseSummary {
    private Integer houseId;
    private String address;
    private LandlordDto landlordDto;
}
