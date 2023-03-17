package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.util.List;
//import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalSummary {
    private Integer numOfBeds;
    private Integer numOfMattresses;
    private Integer numOfTables;
    private Integer numOfChairs;
    private FacilityReportPageDisplay pageOfReports;
    private List<EmployeeInfo> employeeInfo;


}
