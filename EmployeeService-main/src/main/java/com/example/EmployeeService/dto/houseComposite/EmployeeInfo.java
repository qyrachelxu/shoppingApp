package com.example.EmployeeService.dto.houseComposite;

import com.example.EmployeeService.domain.Car;
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
