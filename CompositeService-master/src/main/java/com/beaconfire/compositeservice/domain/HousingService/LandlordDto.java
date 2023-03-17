package com.beaconfire.compositeservice.domain.HousingService;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LandlordDto {
    private String legalFullName;
    private String phoneNumber;
    private String email;
}
