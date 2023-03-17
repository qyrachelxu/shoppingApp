package com.example.EmailService.controller;

import com.example.EmailService.domain.EmailDetail;
import com.example.EmailService.service.EmailService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean       // this annotation is provided by Spring Boot
    EmailService emailService;

    @Test
    public void sendEmail_success() throws Exception {
        //mock
        EmailDetail emailDetail = EmailDetail.builder().recipient("isxu.qy@gamil.com").msgBody("message").subject("subject").build();
        when(emailService.receivedMessage(emailDetail)).thenReturn("success");

        //actual
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/email/sendEmail")
                        .header("Authorization", "Bearer 1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(emailDetail))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void sendEmail_fail() throws Exception {
        //mock
        EmailDetail emailDetail = EmailDetail.builder().recipient("isxu.qy").msgBody("message").subject("subject").build();
        when(emailService.receivedMessage(emailDetail)).thenReturn("fail");

        //assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/email/sendEmail")
                        .header("Authorization", "Bearer 1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(emailDetail))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
