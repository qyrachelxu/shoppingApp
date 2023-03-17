package com.beaconfire.housingservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class FacilityReportResponse {
    private String title;
    private String description;
    private String employeeId;
    private Timestamp createdTime;
    private String status;
    private List<CommentDto> commentList;

}
