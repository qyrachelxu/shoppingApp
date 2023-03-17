package com.example.EmailService.controller;

import com.example.EmailService.domain.EmailDetail;
import com.example.EmailService.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("sendEmail")
    public String sendEmail(@RequestBody EmailDetail detail) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return emailService.receivedMessage(detail);
    }
}
