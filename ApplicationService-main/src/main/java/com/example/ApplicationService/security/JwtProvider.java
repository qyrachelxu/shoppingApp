package com.example.ApplicationService.security;

import com.example.ApplicationService.exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
@Component
@PropertySource("classpath:application.yml")
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;

    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request)  {
        String prefixedToken = request.getHeader("Authorization"); // extract token value by key "Authorization"
        if(request.getRequestURI().equals("/application-service/swagger-ui.html") ||
                request.getRequestURI().startsWith("/application-service/webjars") ||
                request.getRequestURI().startsWith("/application-service/v2") ||
                request.getRequestURI().startsWith("/application-service/csrf") ||
                request.getRequestURI().startsWith("/application-service/swagger-resources")){
            return Optional.empty();
        }

        if (prefixedToken == null) {
            throw new InvalidCookieException("Invalid token");
        }
        String token = prefixedToken.substring(7); // remove the prefix "Bearer "

        // extract jwt from request, generate a userdetails object
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // decode
        } catch (Exception e){
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String username = claims.getSubject();
        int userId = (int) claims.get("userId");
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());
        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .userId(userId)
                .username(username)
                .authorities(authorities)
                .build());
    }
}