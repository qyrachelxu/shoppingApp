package com.example.EmailService.consumer;

import com.example.EmailService.domain.EmailDetail;
import com.example.EmailService.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RabbitReceiver {
//    private static final Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void receivedMessage(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = message;
        EmailDetail emailDetail = objectMapper.readValue(json, EmailDetail.class);
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailDetail.getRecipient());
            mailMessage.setText(emailDetail.getMsgBody());
            mailMessage.setSubject(emailDetail.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
//            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
//            return "Error while Sending Mail";
        }
    }
}
