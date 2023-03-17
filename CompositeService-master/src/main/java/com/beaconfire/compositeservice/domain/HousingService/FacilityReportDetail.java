package com.beaconfire.compositeservice.domain.HousingService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReportDetail {
    private Integer id;
    private String employeeId;
    private String comment;
    private Timestamp createdTime;
    private Timestamp lastModificationTime;
}
