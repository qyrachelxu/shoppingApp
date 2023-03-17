package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReportDisplay {
    private String title;
    private String description;
    private String author;
    private Timestamp createdTime;
    private String status;
    private List<CommentDisplay> commentList;
}
