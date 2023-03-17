package com.example.ApplicationService.controller;


import com.example.ApplicationService.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("application")
public class CompositeController {

    ApplicationService applicationService;
    private HashSet<String> possibleStatus = new HashSet<>();

    @Autowired
    public CompositeController(ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.possibleStatus.add("Pending");
        this.possibleStatus.add("Rejected");
        this.possibleStatus.add("Approved");
        this.possibleStatus.add("Incomplete");
    }
    @GetMapping("test")
    public String test(){
        return "test application service";
    }

    @GetMapping("{userid}/getstatus")  // composite service api
    public String getApplicationStatus(@PathVariable(value = "userid") Integer userID) {

        return applicationService.getApplicationStatus(userID);
    }

    @GetMapping("{userid}/getcomment")  // composite service api
    public String getApplicationComment(@PathVariable(value = "userid") Integer userID) {
        return applicationService.getApplicationComment(userID);
    }

    @PutMapping("{userid}/updatetime")  // composite service api
    public void updateApplicationTime(@PathVariable(value = "userid") Integer userID) {
        applicationService.updateApplicationTime(userID);
    }

    @GetMapping("/{status}/list")  // composite service api
    public List<Integer> getPendingUserId(@PathVariable(value = "status") String status) {
        if (!possibleStatus.contains(status)) {
            return new ArrayList<>();
        }
        return applicationService.getUserIdList(status);
    }
}
