package com.example.EmployeeService.config;

import com.example.EmployeeService.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//WebSecurityConfigurerAdapter needs to be extended to override some of its methods
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //    private UserDetailsService userDetailsService;
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

    //This method is used to configure the security of the application
    //Since we are attaching jwt to a request header manually, we don't need to worry about csrf
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/employee-service/swagger-ui/**", "/employee-service/swagger-ui.html", "/employee-service/swagger-resources/**", "/employee-service/v2/api-docs/**").permitAll()
                .antMatchers("/employee-service/webjars/**", "/employee-service/csrf").permitAll().and()
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/employee-service/hr/{userId}/profile").hasAnyAuthority("employee", "hr")
                .antMatchers("/employee-service/hr/**").hasAuthority("hr")
                .antMatchers("/employee-service/employee/{userID}/getOPT").hasAnyAuthority("employee", "hr")
                .antMatchers("/employee-service/employee/{employeeId}/roommates").hasAnyAuthority("employee", "hr")
                .antMatchers("/employee-service/employee/house/{houseID}/residents").hasAnyAuthority("employee", "hr")
                .antMatchers("/employee-service/employee/{employeeId}").hasAnyAuthority("employee", "hr")
                .antMatchers("/employee-service/employee/**").hasAuthority("employee")
                .anyRequest()
                .authenticated();
    }
}
