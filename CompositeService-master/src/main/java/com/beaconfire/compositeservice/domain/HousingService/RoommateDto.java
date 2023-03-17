package com.beaconfire.compositeservice.domain.HousingService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoommateDto {
    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String cellPhone;

    private String alternatePhone;
}

