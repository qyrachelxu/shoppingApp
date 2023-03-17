package com.beaconfire.housingservice.config;

import com.beaconfire.housingservice.security.JwtFilter;
import com.beaconfire.housingservice.utils.AccessControlHelper;
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

    // authentication provider uses the userDetailsService by calling the loadUserByUsername()
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
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/login")
//                .antMatchers("/register");
//    }
    //This method is used to configure the security of the application
    //Since we are attaching jwt to a request header manually, we don't need to worry about csrf
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        http
                .authorizeRequests()
                .antMatchers("/housing-service/swagger-ui/**", "/housing-service/swagger-ui.html", "/housing-service/swagger-resources/**", "/housing-service/v2/api-docs/**").permitAll()
                .antMatchers("/housing-service/webjars/**", "/housing-service/csrf").permitAll().and()
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/housing-service/housing/user/{employeeId}/facilityReportDetail/{frdId}/comment/update")
                .access("@accessControlHelper.allowUpdateComment(authentication,#employeeId,#frdId)")
                .antMatchers("/housing-service/housing/hr/**").hasAuthority("hr")
                .antMatchers("/housing-service/housing/employee/**").hasAuthority("employee")
                .antMatchers("/").permitAll()
                .anyRequest()
                .authenticated();;
    }
}
