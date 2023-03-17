package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityInfo {
    private int numOfBeds;
    private int numOfMattresses;
    private int numOfTables;
    private int numOfChairs;
    private String pageOfReports;
}
