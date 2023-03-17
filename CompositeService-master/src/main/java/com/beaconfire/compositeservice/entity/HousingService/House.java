package com.beaconfire.compositeservice.entity.HousingService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class House {
    private Integer id;
    private Landlord landlord;
    private String address;
    private String maxOccupant;
    private List<Facility> facilities;
}
