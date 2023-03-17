package com.beaconfire.compositeservice.entity.EmployeeService;

import com.beaconfire.compositeservice.domain.EmployeeService.Employee;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EditRequest {

    Employee employee;

    String comment;
}
