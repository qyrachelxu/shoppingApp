package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReportPageDisplay {
        private List<FacilityReportDisplay> content;
        private int totalPages;
        private int totalElements;
        private boolean last;
        private int numberOfElements;
        private boolean first;
        private int size;
        private int number;
        private boolean empty;
}
