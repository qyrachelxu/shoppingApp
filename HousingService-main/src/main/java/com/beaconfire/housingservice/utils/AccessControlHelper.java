package com.beaconfire.housingservice.utils;

import com.beaconfire.housingservice.service.HousingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AccessControlHelper {
    private final HousingService housingService;
    @Autowired
    public AccessControlHelper(HousingService housingService) {
        this.housingService = housingService;
    }

    public boolean allowUpdateComment(Authentication authentication, String employeeId, Integer frdId){
        String actualReporterId = housingService.getFacilityReportDetailByFrdId(frdId);
        System.out.println(actualReporterId);
        System.out.println(employeeId);
        return employeeId.equals(actualReporterId);
    }


    
}
