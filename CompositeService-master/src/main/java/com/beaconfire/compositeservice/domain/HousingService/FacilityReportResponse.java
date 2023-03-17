package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReportResponse {
    private String title;
    private String description;
    private String employeeId;
    private Timestamp createdTime;
    private String status;
    private List<CommentDto> commentList;

}
