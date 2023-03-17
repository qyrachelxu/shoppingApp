package com.xiang.authentication.security;

import com.xiang.authentication.exception.InvalidCredentialsException;
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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;
    private final long tokenValidityInMilliseconds = 3600000*3;//EXPIRE IN 3 HOURS

    public String createToken(AuthUserDetail userDetails){
        //Claims is essentially a key-value pair, where the key is a string and the value is an object
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername()); // user identifier
        claims.put("userId", userDetails.getUserId());
        claims.put("email", userDetails.getEmail());
        claims.put("permissions", userDetails.getAuthorities()); // user permission
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key) // algorithm and key to sign the token
                .compact();
    }


    public String createTokenByEmail(String email, ZonedDateTime threeHoursLater) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInMilliseconds);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("employee"));

        Claims claims = Jwts.claims().setSubject(email); // user identifier
        claims.put("permissions",authorities);// user permission
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key) // algorithm and key to sign the token
                .setExpiration(expiryDate)
                .compact();
    }

//    resolve email token
    public Optional<AuthUserDetail> resolveEmailToken(HttpServletRequest request)  throws AuthenticationException  {
        String prefixedToken = request.getHeader("Authorization"); // extract token value by key "Authorization"
        if (prefixedToken==null){
            return Optional.empty();
        }
        String token = prefixedToken.substring(7); // remove the prefix "Bearer "
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // decode
        } catch (Exception e){
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String email = claims.getSubject();
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());

        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .email(email)
                .authorities(authorities)
                .build());

    }

    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request) throws AuthenticationException  {
        String prefixedToken = request.getHeader("Authorization"); // extract token value by key "Authorization"
        if(request.getRequestURI().equals("/authentication/swagger-ui.html") ||
                request.getRequestURI().startsWith("/authentication/webjars") ||
                request.getRequestURI().startsWith("/authentication/v2") ||
                request.getRequestURI().startsWith("/authentication/csrf") ||
                request.getRequestURI().startsWith("/authentication/swagger-resources")){
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
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());

        //return a userDetail object with the permissions the user has
        return Optional.of(AuthUserDetail.builder()
                .username(username)
                .authorities(authorities)
                .build());
    }
}
