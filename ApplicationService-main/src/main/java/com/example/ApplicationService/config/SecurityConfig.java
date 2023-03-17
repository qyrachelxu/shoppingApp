package com.example.ApplicationService.config;


import com.example.ApplicationService.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//WebSecurityConfigurerAdapter needs to be extended to override some of its methods
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JwtFilter jwtFilter;

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    protected void configure(HttpSecurity http) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        http
                .authorizeRequests()
                .antMatchers("/application-service/swagger-ui/**", "/application-service/swagger-ui.html", "/application-service/swagger-resources/**", "/application-service/v2/api-docs/**").permitAll()
                .antMatchers("/application-service/webjars/**", "/application-service/csrf").permitAll().and()
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/application-service/downloadlink/**").permitAll()
                .antMatchers("/application-service/hr/**").hasAuthority("hr")
                .antMatchers("/application-service/employee/**").hasAuthority("employee")
                .antMatchers("/application-service/application/**").hasAnyAuthority("employee", "hr")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();
                //.and()
                //.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
