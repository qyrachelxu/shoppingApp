package com.beaconfire.compositeservice.domain.HousingService;

import com.beaconfire.compositeservice.domain.EmployeeService.Car;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfo {
    private Integer userId;
    private String legalFullName;
    private Integer houseId;
    private String phoneNumber;
    private String email;
    private List<Car> carList;


}
