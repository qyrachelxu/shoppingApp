package com.xiang.authentication.service;

import com.xiang.authentication.dao.RegistrationTokenDao;
import com.xiang.authentication.domain.entity.User;
import com.xiang.authentication.exception.ZeroOrManyException;
import com.xiang.authentication.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class RegistrationTokenService {
    private RegistrationTokenDao registrationTokenDao;
    private JwtProvider jwtProvider;
    private UserService userService;
    @Autowired
    public void setRegistrationTokenDao(RegistrationTokenDao registrationTokenDao) {
        this.registrationTokenDao = registrationTokenDao;
    }
    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }
    @Autowired
    public void setUserService(UserService userService) {this.userService = userService;}

    public String createTokenByEmail(String email) throws ZeroOrManyException {
        //A token wil be created using the username/email/userId and permission
        ZoneId usEasternZone = ZoneId.of("America/New_York");
        ZonedDateTime now = ZonedDateTime.now(usEasternZone);
        ZonedDateTime threeHoursLater = now.plusHours(3);
        Timestamp timestamp = new Timestamp(threeHoursLater.toInstant().toEpochMilli());
        String token = jwtProvider.createTokenByEmail(email, threeHoursLater);
        User user = userService.getCurrentUser();
        registrationTokenDao.createNewRegistrationToken(token,email,timestamp,user);
        return token;
    }
}
