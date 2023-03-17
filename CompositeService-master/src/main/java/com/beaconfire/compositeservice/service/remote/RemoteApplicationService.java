package com.beaconfire.compositeservice.service.remote;


import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import com.beaconfire.compositeservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "application-service", configuration = FeignClientInterceptor.class)
public interface RemoteApplicationService {
    @GetMapping("/application-service/application/test")
    String getTest();

    @GetMapping("/application-service/application/{status}/list")
    List<Integer> getUserIDsByStatus(@PathVariable(value = "status") String status);

    @GetMapping("/application-service/application/{userID}/getstatus")
    String getApplicationStatusByUserID(@PathVariable int userID);

    @PutMapping("/application-service/employee/application/submit")
    ResponseEntity<String> submitApplication();

    @GetMapping("/application-service/application/{userID}/getstatus")
    String getApplicationStatus(@PathVariable(value = "userID") Integer userID);

    @GetMapping("/application-service/application/{userid}/getcomment")
    String getApplicationComment(@PathVariable(value = "userid") Integer userID);

//    @GetMapping("/{userID}/apply")
//    ApplicationDTO getEmployeeApplicationComposite(@PathVariable int userID);

    @PutMapping("/application-service/hr/application/updatestatus")
    ResponseEntity<String> updateStatusHR(@RequestParam("userid") Integer id,
                                          @RequestParam(value = "result") String status,
                                          @RequestParam("comment") String comment);
}
