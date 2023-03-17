package com.example.EmployeeService.security;

import com.example.EmployeeService.Exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
//@PropertySource("classpath:application.properties")
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;

    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request) throws AuthenticationException {
        String prefixedToken = request.getHeader("Authorization"); // extract token value by key "Authorization"
        if(request.getRequestURI().equals("/employee-service/swagger-ui.html") ||
                request.getRequestURI().startsWith("/employee-service/webjars") ||
                request.getRequestURI().startsWith("/employee-service/v2") ||
                request.getRequestURI().startsWith("/employee-service/csrf") ||
                request.getRequestURI().startsWith("/employee-service/swagger-resources")){
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
        String userAuthority = claims.get("authority", String.class);

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("authority" + userAuthority));

        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .userId(userId)
                .username(username)
                .authorities(authorities)
                .build());
    }
}