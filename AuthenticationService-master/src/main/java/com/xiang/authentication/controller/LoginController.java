package com.xiang.authentication.controller;

import com.xiang.authentication.domain.request.LoginRequest;
import com.xiang.authentication.domain.response.LoginResponse;
import com.xiang.authentication.security.AuthUserDetail;
import com.xiang.authentication.security.JwtProvider;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("authentication")
@ApiOperation("anthtication")
public class LoginController {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    //User trying to log in with username and password
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        Authentication authentication;
        //Try to authenticate the user using the username and password
        try{
          authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Provided credential is invalid.");
        }
        //Successfully authenticated user will be stored in the authUserDetail object
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal(); //getPrincipal() returns the user object
        //A token wil be created using the username/email/userId and permission
        String token = jwtProvider.createToken(authUserDetail);
        //Returns the token as a response to the frontend/postman
        return LoginResponse.builder()
                .message("Welcome " + authUserDetail.getUsername())
                .token(token)
                .build();
    }
}
