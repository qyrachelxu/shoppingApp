package com.beaconfire.compositeservice.service.remote;


import com.beaconfire.compositeservice.config.FeignClientInterceptor;
import com.beaconfire.compositeservice.domain.HousingService.FacilityInfo;
import com.beaconfire.compositeservice.domain.HousingService.FacilityReportResponse;
import com.beaconfire.compositeservice.domain.HousingService.HouseSummary;
import com.beaconfire.compositeservice.domain.HousingService.ReportRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "housing-service", configuration = FeignClientInterceptor.class)
public interface RemoteHousingService {

    @GetMapping("housing-service/housing/{houseId}/address")
    String getAddressByHouseId(@PathVariable Integer houseId);
    @GetMapping("housing-service/housing/view/house/{houseId}/reports")
    List<FacilityReportResponse> getExistingReports(@PathVariable Integer houseId);

    @GetMapping("housing-service/housing/hr/view/house/{houseId}/facilityInfo")
    FacilityInfo getFacilityInfo(@RequestParam(defaultValue = "0") Integer pageNumber,
                            @RequestParam(defaultValue = "3") Integer pageSize, @PathVariable Integer houseId);

    @GetMapping("housing-service/housing/hr/view/houses")
    List<HouseSummary> viewAllHouses();

    @DeleteMapping("housing-service/housing/hr/house/{houseId}/delete")
    ResponseEntity<String> deleteHouseByHouseId(@PathVariable Integer houseId);
    @PostMapping("housing-service/housing/employee/{employeeId}/facility/{facilityId}/report")
    ResponseEntity<String> createFacilityReport(@RequestBody ReportRequest report, @PathVariable String employeeId, @PathVariable Integer facilityId);
    @GetMapping("housing-service/housing/{houseId}/facility/{facilityId}/check-match")
    boolean checkFacilityInHouse(@PathVariable Integer houseId, @PathVariable Integer facilityId);
    @PutMapping("housing-service/housing/user/{employeeId}/facilityReportDetail/{frdId}/comment/update")
    void editComment(@RequestParam String comment, @PathVariable String employeeId, @PathVariable Integer frdId);

}
