package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseSummary {
    private Integer houseId;
    private String address;
    private LandlordDto landlordDto;
}
