package com.xiang.authentication.security;

import com.xiang.authentication.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

//The jwt filter that we want to add to the chain of filters of Spring Security
@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/authentication/login") || path.equals("/authentication/swagger-ui.html") ||
                request.getRequestURI().startsWith("/authentication/webjars") ||
                request.getRequestURI().startsWith("/authentication/v2") ||
                request.getRequestURI().startsWith("/authentication/csrf") ||
                request.getRequestURI().startsWith("/authentication/swagger-resources");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws AuthenticationException, ServletException, IOException, InvalidCredentialsException {
        if (request.getRequestURI().equals("/authentication/registration")) {
            Optional<AuthUserDetail> authUserDetailOptional = jwtProvider.resolveEmailToken(request); // extract jwt from request, generate a userdetails object
            if (authUserDetailOptional != null && authUserDetailOptional.isPresent() ) {
                AuthUserDetail authUserDetail = authUserDetailOptional.get();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authUserDetail.getEmail(),
                        null,
                        authUserDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        Optional<AuthUserDetail> authUserDetailOptional = jwtProvider.resolveToken(request); // extract jwt from request, generate a userdetails object
        if (authUserDetailOptional != null && authUserDetailOptional.isPresent() ) {
            AuthUserDetail authUserDetail = authUserDetailOptional.get();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authUserDetail.getUsername(),
                    null,
                    authUserDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);    // this is needed for the filer chain to continue filtering after current filter

    }
}
