package com.beaconfire.compositeservice.utils;

import com.beaconfire.compositeservice.domain.HousingService.EmployeeInfo;
import com.beaconfire.compositeservice.service.remote.RemoteEmployeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AccessControlHelper {
    private final RemoteEmployeeService employeeService;

    public AccessControlHelper(RemoteEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public boolean allowViewPersonalHouseDetails(Authentication authentication,String employeeId){
        EmployeeInfo employee =employeeService.getEmployeeByEmployeeId(employeeId);
        return employee.getUserId().equals(authentication.getCredentials());

    }

    public boolean allowEditComment(Authentication authentication,String employeeId){
        System.out.println("Credetial:"+authentication.getCredentials());
        String actualEmployeeId = employeeService.getEmployeeIdByUserID((Integer) authentication.getCredentials());
        return actualEmployeeId.equals(employeeId);
    }

}
