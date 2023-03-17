package com.beaconfire.compositeservice.domain.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VisaStatus {

    private String visaStatusId;

    private String visaType;

    private boolean activeFlag;

    private String startDate;

    private String endDate;

    private String lastModificationDate;
}
