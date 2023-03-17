package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.awt.print.Pageable;
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

}