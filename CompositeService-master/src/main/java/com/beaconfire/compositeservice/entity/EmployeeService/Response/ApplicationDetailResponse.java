package com.beaconfire.compositeservice.entity.EmployeeService.Response;

import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationFormDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.DocumentDTO;
import com.beaconfire.compositeservice.entity.Response.MessageResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationDetailResponse {
    private MessageResponse messageResponse;
    private ApplicationDTO response;
}
