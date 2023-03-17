package com.beaconfire.housingservice.utils;

import com.beaconfire.housingservice.domain.FacilityReportPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;

public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String convertPageToJson(FacilityReportPage facilityReportPage) {
        try {
            return objectMapper.writeValueAsString(facilityReportPage);
        } catch (Exception e) {
            // handle exception
        }
        return null;
    }
}
