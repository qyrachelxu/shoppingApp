package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDisplay {
    private String description;
    private String author;
    private Timestamp lastModificationTime;
}
