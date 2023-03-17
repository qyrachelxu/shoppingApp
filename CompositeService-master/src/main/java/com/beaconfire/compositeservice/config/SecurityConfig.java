package com.beaconfire.compositeservice.config;

import com.beaconfire.compositeservice.security.JwtFilter;
import com.beaconfire.compositeservice.utils.AccessControlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//WebSecurityConfigurerAdapter needs to be extended to override some of its methods
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;
    private final AccessControlHelper accessControlHelper;

    public SecurityConfig(AccessControlHelper accessControlHelper) {
        this.accessControlHelper = accessControlHelper;
    }

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

//    @Autowired
//    public void setUserDetailsService(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
////     authentication provider uses the userDetailsService by calling the loadUserByUsername()
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(new BCryptPasswordEncoder());
//        return provider;
//    }

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
                .antMatchers("/composite-service/swagger-ui/**", "/composite-service/swagger-ui.html", "/composite-service/swagger-resources/**", "/composite-service/v2/api-docs/**").permitAll()
                .antMatchers("/composite-service/webjars/**", "/composite-service/csrf").permitAll().and()
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/composite-service/composite/employee-housing/user/{employeeId}/facilityReportDetail/{frdId}/comment/update")
                .access("@accessControlHelper.allowEditComment(authentication,#employeeId)")
                .antMatchers("/composite-service/composite/employee-housing/employee/{employeeId}/details")
                .access("@accessControlHelper.allowViewPersonalHouseDetails(authentication,#employeeId)")
                .antMatchers("/composite-service/composite/application-email/hr/**",
                        "/composite-service/composite/employee-housing/hr/**",
                        "/composite-service/composite/employee-application/hr/**").hasAuthority("hr")
                .antMatchers("/composite-service/composite/application-email/hr/updatestatus/**",
                        "/composite-service/composite/employee-housing/employee/**",
                        "/composite-service/composite/employee-application/employee-service/**").hasAuthority("employee")
                .anyRequest()
                .authenticated();;
    }
}
