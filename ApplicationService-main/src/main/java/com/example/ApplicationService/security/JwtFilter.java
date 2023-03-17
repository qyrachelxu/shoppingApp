package com.example.ApplicationService.security;

import com.example.ApplicationService.exception.InvalidCredentialsException;
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
import java.io.NotActiveException;
import java.nio.file.NotLinkException;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {  // user register should not be filtered out
        String path = request.getRequestURI();
        return path.startsWith("/downloadlink");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws AuthenticationException, ServletException, IOException, InvalidCredentialsException {

        if (request.getRequestURI().startsWith("/application-service/downloadlink")) {
            filterChain.doFilter(request, response);
            System.out.println("Xxxxx");
            return;
        }

        Optional<AuthUserDetail> authUserDetailOptional = null; // extract jwt from request, generate a userdetails object
        authUserDetailOptional = jwtProvider.resolveToken(request);

        if (authUserDetailOptional != null && authUserDetailOptional.isPresent() ) {
            AuthUserDetail authUserDetail = authUserDetailOptional.get();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authUserDetail.getUsername(),
                    authUserDetail.getUserId(),
                    authUserDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);    // this is needed for the filer chain to continue filtering after current filter

    }
}