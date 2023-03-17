package com.xiang.authentication.controller;

import com.xiang.authentication.domain.message.EmailDetail;
import com.xiang.authentication.domain.message.SimpleMessage;
import com.xiang.authentication.domain.request.RegistrationRequest;
import com.xiang.authentication.domain.response.LoginResponse;
import com.xiang.authentication.domain.response.MessageResponse;
import com.xiang.authentication.domain.response.ServiceStatus;
import com.xiang.authentication.exception.ConstraintViolationException;
import com.xiang.authentication.exception.EmailExistedException;
import com.xiang.authentication.exception.UsernameExistedException;
import com.xiang.authentication.exception.ZeroOrManyException;
import com.xiang.authentication.service.RegistrationTokenService;
import com.xiang.authentication.service.UserService;
import com.xiang.authentication.util.SerializeUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("authentication")
@ApiOperation("anthtication")
public class RegistrationController {

    private UserService userService;
    private RegistrationTokenService registrationTokenService;
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setContentService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRegistrationTokenDao(RegistrationTokenService registrationTokenService) {
        this.registrationTokenService = registrationTokenService;
    }

    @GetMapping("/token")
    @PreAuthorize("hasAuthority('hr')")
    public LoginResponse generateRegistrationToken(@RequestParam String email) throws AuthenticationException,ZeroOrManyException {
        System.out.println("HIHIIHI");
        String token = registrationTokenService.createTokenByEmail(email);
        String url = "http://localhost:9999/authentication/registration";
        String title = "Registration Token";
        String description = "Welcome to our company!\n " +
                "Please use the following link to complete your registration:\n " +
                "" + url +
                "\nToken: " + token +
                "\nThis link will expire in 3 hours, so please complete your registration as soon as possible.\n  " +
                "Thank you for joining our team!";

        EmailDetail emailDetail = EmailDetail.builder().subject(title).recipient(email).msgBody(description).build();
        String jsonMessage = SerializeUtil.serialize(emailDetail);

        rabbitTemplate.convertAndSend("emailExchange", "binding.key", jsonMessage);

        return LoginResponse.builder()
                .message("Token sent for " + email)
                .token(token)
                .build();
    }

    @PostMapping("/registration")
    @PreAuthorize("hasAuthority('employee')")
    public MessageResponse registration(@RequestBody RegistrationRequest request)
            throws UsernameExistedException, EmailExistedException, ConstraintViolationException, ZeroOrManyException {
        userService.createNewUser(request);
        return MessageResponse.builder()
                .serviceStatus(ServiceStatus.builder().success(true).build())
                .message("New user account created")
                .build();
    }

}
