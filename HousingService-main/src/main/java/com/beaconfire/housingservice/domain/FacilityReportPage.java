package com.beaconfire.housingservice.domain;

import com.beaconfire.housingservice.domain.entity.FacilityReport;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReportPage {
    private List<FacilityReport> content;
    private int totalPages;
    private int totalElements;
    private boolean last;
    private int numberOfElements;
    private boolean first;
    private int size;
    private int number;
    private boolean empty;

    // getters and setters
}