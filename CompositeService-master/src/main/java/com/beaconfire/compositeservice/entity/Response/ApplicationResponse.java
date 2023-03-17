package com.beaconfire.compositeservice.entity.Response;

import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplicationResponse {
    private MessageResponse messageResponse;
    private ApplicationDTO application;
}