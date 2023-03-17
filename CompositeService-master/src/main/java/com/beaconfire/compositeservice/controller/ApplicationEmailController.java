package com.beaconfire.compositeservice.controller;

import com.beaconfire.compositeservice.service.CompositeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("composite/application-email")
@ApiOperation("ApplicationEmailController")
public class ApplicationEmailController {
    private CompositeService compositeService;

    @Autowired
    public void setCompositeService(CompositeService compositeService) {
        this.compositeService = compositeService;
    }

    @PutMapping("/hr/updatestatus/{userID}")
    public ResponseEntity<String> reviewApplication(@PathVariable int userID, @RequestParam String status, @RequestParam String comment) {
        return compositeService.reviewApplication(userID, status, comment);
    }


}
