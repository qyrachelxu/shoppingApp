package com.beaconfire.compositeservice.service.remote;

import com.beaconfire.compositeservice.entity.EmailService.EmailDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("email-service")
public interface RemoteEmailService {
    @PostMapping("/email-service/email/sendEmail")
    String sendEmail(@RequestBody EmailDetail detail);
}
