package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseDetailDto {
    private String address;
    private List<EmoloyeeDto> roommates;
}
