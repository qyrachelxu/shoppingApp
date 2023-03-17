package com.example.EmailService.service;

import com.example.EmailService.domain.EmailDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private EmailDetail emailDetail;
    @InjectMocks
    private EmailService emailService;

    @Test
    public void testReceivedMessage() {
        // Set up the mock EmailDetail object
        when(emailDetail.getRecipient()).thenReturn("test@example.com");
        when(emailDetail.getMsgBody()).thenReturn("Test email body");
        when(emailDetail.getSubject()).thenReturn("Test email subject");

        // Call the receivedMessage method
        String result = emailService.receivedMessage(emailDetail);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(argument.capture());
        assertEquals("test@example.com", argument.getValue().getTo()[0]);
        assertEquals("Test email subject", argument.getValue().getSubject());
        assertEquals("Test email body", argument.getValue().getText());

        // Assert that the result is as expected
        assertEquals("Mail Sent Successfully...", result);
    }
}
