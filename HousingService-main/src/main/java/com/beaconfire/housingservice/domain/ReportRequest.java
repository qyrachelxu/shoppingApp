package com.beaconfire.housingservice.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {
    private String title;
    private String description;
}
