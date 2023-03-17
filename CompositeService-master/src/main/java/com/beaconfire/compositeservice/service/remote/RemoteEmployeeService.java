package com.beaconfire.compositeservice.service.remote;

import com.beaconfire.compositeservice.domain.EmployeeService.Employee;
import com.beaconfire.compositeservice.config.FeignClientInterceptor;
import com.beaconfire.compositeservice.domain.HousingService.EmployeeInfo;
import com.beaconfire.compositeservice.domain.HousingService.RoommateDto;
import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.EmployeeInfoDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.ProfileDTO;
import com.beaconfire.compositeservice.entity.Response.ApplicationResponse;
import com.beaconfire.compositeservice.entity.Response.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@FeignClient(value = "employee-service", configuration = FeignClientInterceptor.class)
public interface RemoteEmployeeService {
    @GetMapping("employee-service/employee/{employeeId}/roommates")
    List<RoommateDto> getRoommates(@PathVariable String employeeId);
    @GetMapping("employee-service/employee/{employeeId}")
    EmployeeInfo getEmployeeByEmployeeId(@PathVariable String employeeId);

    @GetMapping("/employee-service/hr/applications")
    List<EmployeeInfoDTO> getApplications(@RequestParam List<Integer> userIDs);

    @GetMapping("/employee-service/hr/applications/pending/{userID}")
    ApplicationDTO getCertainPendingApplication(@PathVariable int userID);

    @GetMapping("/employee-service/employee/house/{houseID}/residents")
    List<EmployeeInfo> getResidents(@PathVariable int houseID);

    //employee see profile in pending status
    @GetMapping("/employee-service/hr/{userID}/profile")
    ProfileDTO getEmployeeProfile(@PathVariable int userID);

    //@GetMapping("/employee-service/employee/{userID}/application")
    //ApplicationResponse getEmployeeApplication(@PathVariable int userID);

    @GetMapping("/employee-service/employee/{userID}/applicationinfo")
    ApplicationDTO getEmployeeApplicationComposite(@PathVariable int userID);

    @GetMapping("/employee-service/employee/{userID}/employeeId")
    String getEmployeeIdByUserID(@PathVariable int userID);

    @PostMapping("employee-service/employee/edit")
    Employee editApplication(@RequestBody Employee employee);

    @PostMapping("employee-service/employee/apply")
//    MessageResponse fillApplicationForm(@Valid @RequestBody Employee employee);
    MessageResponse fillApplicationForm(@Valid @RequestBody Employee employee);

    @GetMapping("/employee-service/employee/{userID}/getemail")
    String getEmailByUserID(@PathVariable int userID);
}
