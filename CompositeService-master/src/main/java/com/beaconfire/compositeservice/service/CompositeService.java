package com.beaconfire.compositeservice.service;

import com.beaconfire.compositeservice.domain.EmployeeService.Employee;
import com.beaconfire.compositeservice.domain.HousingService.*;
import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import com.beaconfire.compositeservice.entity.EmailService.EmailDetail;
import com.beaconfire.compositeservice.entity.EmployeeService.EmployeeInfoDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.ProfileDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.Response.ApplicationDetailResponse;
import com.beaconfire.compositeservice.entity.Response.ApplicationResponse;
import com.beaconfire.compositeservice.entity.Response.MessageResponse;
import com.beaconfire.compositeservice.service.remote.RemoteApplicationService;
import com.beaconfire.compositeservice.service.remote.RemoteEmailService;
import com.beaconfire.compositeservice.service.remote.RemoteEmployeeService;
import com.beaconfire.compositeservice.service.remote.RemoteHousingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompositeService {

    private RemoteHousingService housingService;
    private RemoteApplicationService applicationService;
    private RemoteEmployeeService employeeService;
    private RemoteEmailService emailService;

    @Autowired
    public void setEmailService(RemoteEmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setEmployeeService(RemoteEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setApplicationService(RemoteApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Autowired
    public void setHousingService(RemoteHousingService housingService) {
        this.housingService = housingService;
    }

    public ResponseEntity<HouseDetailDto> getAssignedHouseDetails(String employeeId) {
        Integer assignedHouseId = employeeService.getEmployeeByEmployeeId(employeeId).getHouseId();
        System.out.println("assigned House Id: " + assignedHouseId);
        String address = housingService.getAddressByHouseId(assignedHouseId);
        List<EmoloyeeDto> emoloyeeDtos = employeeService.getRoommates(employeeId).stream().map(r -> EmoloyeeDto.builder()
                .legalFullName(r.getFirstName() + " " + r.getLastName())
                .phoneNumber(r.getCellPhone()).build()).collect(Collectors.toList());
        return ResponseEntity.ok(HouseDetailDto.builder().address(address).roommates(emoloyeeDtos).build());
    }

    public ResponseEntity<List<FacilityReportDisplay>> getFacilityReports(Integer houseId) {
        List<FacilityReportDisplay> facilityReportResponses =
                housingService.getExistingReports(houseId).stream().map(fr ->
                        FacilityReportDisplay.builder().title(fr.getTitle()).description(fr.getDescription()).author(employeeService.getEmployeeByEmployeeId(fr.getEmployeeId()).getLegalFullName())
                                .createdTime(fr.getCreatedTime()).status(fr.getStatus()).commentList(fr.getCommentList().stream().map(cm ->
                                                CommentDisplay.builder().description(cm.getDescription()).author(employeeService.getEmployeeByEmployeeId(cm.getEmployeeId()).getLegalFullName()).lastModificationTime(cm.getLastModificationTime()).build())
                                        .collect(Collectors.toList()))
                                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(facilityReportResponses);
    }


    //given status, hr can view all applications under that status
    public List<EmployeeInfoDTO> getApplications(String status) {
        List<Integer> userIDs = applicationService.getUserIDsByStatus(status);
        return employeeService.getApplications(userIDs);
    }

    //hr can review application, give status+feedback. If rejected, send Email to the employee
    public ResponseEntity<String> reviewApplication(int userID, String status, String comment) {
        String curStatus = applicationService.getApplicationStatusByUserID(userID);

        //if pending
        if(curStatus.equalsIgnoreCase("pending")) {
            if(!status.toLowerCase().equals("approved") && !status.toLowerCase().equals("rejected")) {
                return ResponseEntity.ok("Invalid given status. Status can only be approved or rejected.");
            }
            if(status.equalsIgnoreCase("approved")) {
                applicationService.updateStatusHR(userID, "Approved", comment);
                return ResponseEntity.ok("Successfully approve the application.");
            }
            else if(status.equalsIgnoreCase("rejected")) {
                String email = employeeService.getEmailByUserID(userID);
                EmailDetail emailDetail = EmailDetail.builder()
                        .recipient(email)
                        .subject("Application status")
                        .msgBody("Your application is rejected. Please login to the system to check")
                        .build();
                String msgFromEmail = emailService.sendEmail(emailDetail);
                applicationService.updateStatusHR(userID, "Rejected", comment);
                return ResponseEntity.ok("Successfully reject the application and send the notification email.");
            }
        }

        //if approved -> can not edit status and comment
        else if(curStatus.equalsIgnoreCase("approved")) {
            return ResponseEntity.ok("You don't have access to change status or add comment. Application has already been approved.");
        }

        //if rejected -> can add comment but cannot edit status
        else if(curStatus.equalsIgnoreCase("rejected")) {
            applicationService.updateStatusHR(userID, "Rejected", comment);
            return ResponseEntity.ok("Successfully update comment to the application.");
        }

        //if other status
        return ResponseEntity.ok("You don't have access to change status or add comment. Application is incomplete or never submitted.");
    }

    //get certain pending application
    public ApplicationDetailResponse getCertainPendingApplication(int userID) {
        if (!applicationService.getApplicationStatusByUserID(userID).equals("Pending")) {
            return ApplicationDetailResponse.builder()
                    .messageResponse(MessageResponse.builder().message("Not a pending application").build())
                    .build();
        }
        return ApplicationDetailResponse.builder()
                .response(employeeService.getCertainPendingApplication(userID))
                .messageResponse(MessageResponse.builder().message("Success").build())
                .build();
    }

    public ResponseEntity<List<HouseSummaryDto>> viewAllHouses() {
        List<HouseSummary> houseSummaries = housingService.viewAllHouses();
        List<HouseSummaryDto> houseSummaryDtos = houseSummaries.stream().map(hs ->
                HouseSummaryDto
                        .builder()
                        .houseId(hs.getHouseId())
                        .address(hs.getAddress())
                        .landlordDto(hs.getLandlordDto())
                        .numberOfResidents(employeeService.getResidents(hs.getHouseId()).size())
                        .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(houseSummaryDtos);
    }

    public ResponseEntity<AdditionalSummary> getAdditionalDetailsByHouseId(
            Integer pageNumber, Integer pageSize, Integer houseId) throws JsonProcessingException {
        List<EmployeeInfo> employeeInfo = employeeService.getResidents(houseId);
        FacilityInfo facilityInfo = housingService.getFacilityInfo(pageNumber, pageSize, houseId);
        String inputJson = facilityInfo.getPageOfReports();
        ObjectMapper objectMapper = new ObjectMapper();
        FacilityReportPage facilityReportPage = objectMapper.readValue(inputJson, FacilityReportPage.class);
        FacilityReportPageDisplay frPd= FacilityReportPageDisplay.builder().totalElements(
                facilityReportPage.getTotalElements()).empty(facilityReportPage.isEmpty()).first(
                        facilityReportPage.isFirst()).last(facilityReportPage.isLast())
                .content(facilityReportPage.getContent().stream().map(fr -> FacilityReportDisplay.builder()
                                .author(employeeService.getEmployeeByEmployeeId(fr.getEmployeeId()).getLegalFullName())
                                        .status(fr.getStatus()).description(fr.getDescription()).createdTime(fr.getCreatedTime())
                                        .title(fr.getTitle()).commentList(fr.getFacilityReportDetails().stream()
                                        .map(fd -> CommentDisplay.builder().author(employeeService
                                                        .getEmployeeByEmployeeId(fr.getEmployeeId()).getLegalFullName())
                                        .description(fd.getComment())
                                        .lastModificationTime(fd.getLastModificationTime()).build())
                                        .collect(Collectors.toList())).build())
                        .collect(Collectors.toList()))
                .numberOfElements(facilityReportPage.getNumberOfElements()).number(facilityReportPage.getNumber())
                .totalPages(facilityReportPage.getTotalPages()).size(facilityReportPage.getSize()).build();
        AdditionalSummary ads = AdditionalSummary.builder()
                .numOfBeds(facilityInfo.getNumOfBeds()).numOfMattresses(facilityInfo.getNumOfMattresses())
                .numOfTables(facilityInfo.getNumOfTables()).numOfChairs(facilityInfo.getNumOfChairs()).pageOfReports(frPd)
                .employeeInfo(employeeInfo).build();

        return ResponseEntity.ok(ads);
    }

//    public ResponseEntity<String> apply(@Valid @RequestBody Employee employee) {
//        MessageResponse messageResponse = employeeService.fillApplicationForm(employee);
//        if(messageResponse.getMessage().equals("success")) {
//            applicationService.submitApplication();
//            return ResponseEntity.ok("Congratulations! You have successfully submitted your application! Please wait for HR to review your application.");
//        }
//        return ResponseEntity.ok(messageResponse.getMessage());
//    }

    public ResponseEntity<String> apply(@Valid @RequestBody Employee employee) {
        MessageResponse messageResponse = employeeService.fillApplicationForm(employee);
        if(messageResponse.getMessage().equals("success")) {
            applicationService.submitApplication();
            return ResponseEntity.ok("Congratulations! You have successfully submitted your application! Please wait for HR to review your application.");
        }
        return ResponseEntity.ok(messageResponse.getMessage());
    }

    public ResponseEntity<String> editApplication(@RequestBody Employee employee) {
        System.out.println("compositeService");
        employeeService.editApplication(employee);
        //int userID = employee.getUserID();
        //applicationService.updateStatusHR(userID, "pending", comment);
        applicationService.submitApplication();
        return ResponseEntity.ok("Application updated successfully!");
    }

        public ResponseEntity<?> getProfile(int userID){
        String status = applicationService.getApplicationStatus(userID);
        switch (status) {
            case "Approved":
                System.out.println("approved");
                ProfileDTO employeeProfile = employeeService.getEmployeeProfile(userID);
                return ResponseEntity.ok(employeeProfile);
            case "Pending":
                System.out.println("pending");
//                ApplicationResponse employeeApplication = employeeService.getEmployeeApplicationComposite(userID);
                ApplicationDTO employeeApplication = employeeService.getEmployeeApplicationComposite(userID);
                return ResponseEntity.ok("Your application is still waiting for HR to review, here is your application: \n" + employeeApplication);
            case "Rejected":
                String applicationComment = applicationService.getApplicationComment(userID);
                return ResponseEntity.ok("Your application has been rejected. Feedback from HR: " + applicationComment);
            default:
                return ResponseEntity.ok("No application found.");
        }
    }






    //    public ResponseEntity<String> editApplication(Employee employee, String comment) {
//        System.out.println("compositeService");
//        employeeService.editApplication(employee);
//        int userID = employee.getUserID();
//        //applicationService.updateStatusHR(userID, "pending", comment);
//        applicationService.submitApplication();
//        return ResponseEntity.ok("Application updated successfully!");
//    }

    public ResponseEntity<String> deleteHouseByHouseId(Integer houseId) {
        if (!employeeService.getResidents(houseId).isEmpty()){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("This is not an empty house, you can't delete");
        }
        return housingService.deleteHouseByHouseId(houseId);
    }

    public ResponseEntity<String> createFacilityReport(ReportRequest reportRequest,String employeeId, Integer facilityId) {
        Integer houseId = employeeService.getEmployeeByEmployeeId(employeeId).getHouseId();
        if (!housingService.checkFacilityInHouse(houseId,facilityId)){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("This is not your house's facility");
        };
       return housingService.createFacilityReport(reportRequest,employeeId,facilityId);
    }

    public ResponseEntity<String> editComment(@RequestParam String comment, String employeeId, Integer frdId) {
        housingService.editComment(comment,employeeId, frdId);
        return ResponseEntity.ok("Edit comment successfully");
    }
}
