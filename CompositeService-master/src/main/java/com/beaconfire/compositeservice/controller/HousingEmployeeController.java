package com.beaconfire.compositeservice.controller;

import com.beaconfire.compositeservice.domain.HousingService.*;

import com.beaconfire.compositeservice.domain.HousingService.FacilityReportResponse;
import com.beaconfire.compositeservice.domain.HousingService.HouseDetailDto;
import com.beaconfire.compositeservice.service.CompositeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.DELETE;
import java.util.List;

@RestController
@RequestMapping("composite/employee-housing")
@ApiOperation("HousingEmployeeController")
public class HousingEmployeeController {

    private CompositeService compositeService;

    @Autowired
    public void setCompositeService(CompositeService compositeService) {
        this.compositeService = compositeService;
    }


    @GetMapping("/employee/{employeeId}/details")
    public ResponseEntity<HouseDetailDto> viewedAssignedHouse(@PathVariable String employeeId){
        return compositeService.getAssignedHouseDetails(employeeId);
    }
    @GetMapping("/employee/view/{houseId}/facilityreports")
    public ResponseEntity<List<FacilityReportDisplay>> viewFacilityReports(@PathVariable Integer houseId){
        return compositeService.getFacilityReports(houseId);
    }

    @GetMapping("/hr/house/summary")
    public ResponseEntity<List<HouseSummaryDto>> viewAllHouses(){
        return compositeService.viewAllHouses();
    }

    @GetMapping("/hr/house/{houseId}/summary/additional")
    public ResponseEntity<AdditionalSummary> viewSummaryByHouseId(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                  @RequestParam(defaultValue = "3") Integer pageSize,
                                                                  @PathVariable Integer houseId) throws JsonProcessingException {
        return compositeService.getAdditionalDetailsByHouseId(pageNumber,pageSize,houseId);
    }

    @DeleteMapping("/hr/house/{houseId}/delete")
    public ResponseEntity<String> deleteHouseByHouseId(@PathVariable Integer houseId){
        return compositeService.deleteHouseByHouseId(houseId);
    }

    @PostMapping("/employee/{employeeId}/facility/{facilityId}/report")
    public ResponseEntity<String> createFacilityReport(@RequestBody ReportRequest report,@PathVariable String employeeId, @PathVariable Integer facilityId){
        return compositeService.createFacilityReport(report,employeeId, facilityId);
    }

    @PutMapping("/user/{employeeId}/facilityReportDetail/{frdId}/comment/update")
    public ResponseEntity<String> editComment(@RequestParam String comment,@PathVariable String employeeId, @PathVariable Integer frdId){
        return compositeService.editComment(comment,employeeId,frdId);
    }
}
