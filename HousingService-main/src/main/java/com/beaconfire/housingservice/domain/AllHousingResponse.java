package com.beaconfire.housingservice.domain;

import com.beaconfire.housingservice.domain.entity.House;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AllHousingResponse {
    private List<House> houseList;
}
