package com.beaconfire.compositeservice.domain.HousingService;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class CommentDto {
    private String description;
    private String employeeId;
    private Timestamp lastModificationTime;
}
