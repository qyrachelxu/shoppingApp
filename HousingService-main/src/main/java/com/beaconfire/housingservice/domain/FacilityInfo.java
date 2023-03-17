package com.beaconfire.housingservice.domain;

import com.beaconfire.housingservice.domain.entity.FacilityReport;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
public class FacilityInfo {
    private int numOfBeds;
    private int numOfMattresses;
    private int numOfTables;
    private int numOfChairs;
    private String pageOfReports;
}
