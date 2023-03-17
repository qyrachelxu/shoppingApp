package com.beaconfire.compositeservice.domain.HousingService;

import com.beaconfire.compositeservice.entity.HousingService.Facility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

//import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReport {

    private Integer id;
    private String employeeId;
    private String title;
    private String description;
    private Timestamp createdTime;
    private String status;
    private List<FacilityReportDetail> facilityReportDetails;


}