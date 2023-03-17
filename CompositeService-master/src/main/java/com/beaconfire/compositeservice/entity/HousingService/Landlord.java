package com.beaconfire.compositeservice.entity.HousingService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Landlord {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String cellphone;
    private List<House> houseList;
}
