package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VisaDTO {
    private String visaType;

    private String startDate;

    private String endDate;
}
