package com.beaconfire.compositeservice.domain;

import com.beaconfire.compositeservice.entity.HousingService.House;
import com.beaconfire.compositeservice.entity.UserService.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class HousingUserResponse {
    List<User> users;
    List<House> houses;
}
