package com.beaconfire.housingservice.controller;

import com.beaconfire.housingservice.domain.*;
import com.beaconfire.housingservice.domain.entity.Facility;
import com.beaconfire.housingservice.domain.entity.FacilityReport;
import com.beaconfire.housingservice.domain.entity.FacilityReportDetail;
import com.beaconfire.housingservice.domain.entity.House;
import com.beaconfire.housingservice.exception.NotFoundException;
import com.beaconfire.housingservice.service.HousingService;
import com.beaconfire.housingservice.utils.JsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("housing")
@ApiOperation("HousingController")
public class HousingController {

    private final HousingService housingService;
    @Autowired
    public HousingController(HousingService housingService) {
        this.housingService = housingService;
    }


    @GetMapping("/{houseId}/address")
    public String getAddressByHouseId(@PathVariable Integer houseId){
        String address = housingService.getAddressByHouseId(houseId);
        if (address == null){
            throw new NotFoundException("Target is not found");
        }
        return address;
    }
    //TODO: Employee
    @PostMapping("/employee/{employeeId}/facility/{facilityId}/report")
    public ResponseEntity<String> createFacilityReport(@RequestBody ReportRequest report, @PathVariable String employeeId, @PathVariable Integer facilityId){
        Facility facility = housingService.getFacilityByFacilityId(facilityId);
        housingService.addNewFacilityReport(report, employeeId, facility);
        return ResponseEntity.ok("Created Facility Report Successfully");
    }
    //TODO:  Any User
    @GetMapping("/{houseId}/facility/{facilityId}/check-match")
    public Boolean checkFacilityInHouse(@PathVariable Integer houseId, @PathVariable Integer facilityId){
        return housingService.checkFacilityHouseMatch(houseId, facilityId);
    }

    //TODO:  Any User
    @GetMapping("/view/house/{houseId}/reports")
    public List<FacilityReportResponse> viewExistingReportsByHouseId(@PathVariable Integer houseId){
        List<FacilityReport> facilityReports= housingService.getFacilityReportsByHouseId(houseId);

        List<FacilityReportResponse> facilityReportResponses=facilityReports.stream().map(facilityReport ->
                FacilityReportResponse
                        .builder()
                        .title(facilityReport.getTitle())
                        .description(facilityReport.getDescription())
                        .employeeId(facilityReport.getEmployeeId())
                        .createdTime(facilityReport.getCreatedTime())
                        .status(facilityReport.getStatus())
                        .commentList(facilityReport.getFacilityReportDetails().stream().map(frd->
                                CommentDto
                                        .builder()
                                        .description(frd.getComment())
                                        .employeeId(frd.getEmployeeId())
                                        .lastModificationTime(frd.getLastModificationTime())
                                        .build()).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
        return facilityReportResponses;
    }
    //TODO: Only HR
    @GetMapping("/hr/view/houses")
    public List<HouseSummary> viewAllHouses(){
        List<House> houses = housingService.getAllHouses();

        return houses.stream().map(house ->
                HouseSummary
                        .builder()
                        .houseId(house.getId())
                        .address(house.getAddress())
                        .landlordDto(
                                LandlordDto.builder()
                                .legalFullName(house.getLandlord().getFirstname()+" "+house.getLandlord().getLastname())
                                .email(house.getLandlord().getEmail())
                                .phoneNumber(house.getLandlord().getCellphone())
                                .build())
                        .build()).collect(Collectors.toList());
    }

    //TODO:Only HR
    @GetMapping("/hr/view/house/{houseId}/facilityInfo")
    public FacilityInfo getFacilityInfo(@RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "3") Integer pageSize, @PathVariable Integer houseId) {
        Integer numberOfBeds = housingService.getNumberOfFacilityByHouseId(houseId,"Bed");
        Integer numberOfMattress = housingService.getNumberOfFacilityByHouseId(houseId,"Mattress");
        Integer numberOfTables = housingService.getNumberOfFacilityByHouseId(houseId,"Table");
        Integer numberOfChairs = housingService.getNumberOfFacilityByHouseId(houseId,"Chair");
        Page<FacilityReport> page = housingService.getReportsByPage(pageNumber, pageSize,houseId);
        JsonConverter jsonConverter = new JsonConverter(new ObjectMapper());
        String pageJson = jsonConverter.convertPageToJson(
                FacilityReportPage
                        .builder()
                        .first(page.isFirst())
                        .last(page.isLast())
                        .totalPages(page.getTotalPages())
                        .content(page.getContent())
                        .numberOfElements(page.getNumberOfElements())
                        .number(page.getNumber())
                        .size(page.getSize())
                        .totalElements((int) page.getTotalElements())
                        .empty(page.isEmpty())
                        .build()
        );

        return FacilityInfo.builder()
                .numOfBeds(numberOfBeds)
                .numOfMattresses(numberOfMattress)
                .numOfTables(numberOfTables)
                .numOfChairs(numberOfChairs)
                .pageOfReports(pageJson).build();
    }
    //TODO: Only HR
    @PostMapping("/hr/house/add")
    public void addNewHouse(@RequestBody HouseInput houseRequest){
        housingService.addNewHouse(houseRequest);
    }
    //TODO:Only HR
    @DeleteMapping("/hr/house/{houseId}/delete")
    public ResponseEntity<String> deleteNewHouse(@PathVariable Integer houseId){
        housingService.deleteHouse(houseId);
        return ResponseEntity.ok("House Deleted");
    }

    //TODO:Any User
    @PostMapping ("/user/{employeeId}/report/{facilityReportId}/comment/add")
    public ResponseEntity<FacilityReportDetail> addComment(@RequestParam String comment, @PathVariable String employeeId, @PathVariable Integer facilityReportId){
        return ResponseEntity.ok(housingService.addCommentToFacilityReport(comment, employeeId,facilityReportId));
    }

    //TODO: Any user
    @PutMapping("/user/{employeeId}/facilityReportDetail/{frdId}/comment/update")
    public ResponseEntity<FacilityReportDetail> editComment(@RequestParam String comment, @PathVariable Integer frdId, @PathVariable String employeeId){
        return ResponseEntity.ok(housingService.updateCommentToFacilityReport(comment, frdId));
    }
    // TODO: Only HR
    @PutMapping("/hr/facility_report/{frId}/status")
    public ResponseEntity<String> updateFacilityReportStatus(@RequestParam String status, @PathVariable Integer frId){
        housingService.updateFacilityReportStatus(status, frId);
        return ResponseEntity.ok("Updated to " + status);
    }

    @GetMapping("/test")
    public Object getAuthUserDetail(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
