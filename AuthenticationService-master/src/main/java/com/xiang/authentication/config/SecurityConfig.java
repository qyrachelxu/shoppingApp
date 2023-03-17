package com.xiang.authentication.config;

import com.xiang.authentication.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//WebSecurityConfigurerAdapter needs to be extended to override some of its methods
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;
    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // authentication provider uses the userDetailsService by calling the loadUserByUsername()
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
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
                .antMatchers("/authentication/swagger-ui/**",
                        "/authentication/swagger-ui.html",
                        "/authentication/swagger-resources/**",
                        "/authentication/v2/api-docs/**").permitAll()
                .antMatchers("/authentication/webjars/**",
                        "/authentication/csrf").permitAll().and()
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/authentication/login/**").permitAll()
                .antMatchers("/authentication/registration/**",
                        "/authentication/update/password/**").hasAuthority("employee")
                .antMatchers("/authentication/token/**",
                        "authentication/userRole/**").hasAuthority("hr")
                .anyRequest()
                .authenticated();
//            http
//                .authorizeRequests()
//                .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**").permitAll()
//                .antMatchers("/webjars/**", "/csrf").permitAll().and()
//                .csrf().disable()
//                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                .antMatchers("/authentication/login/**").permitAll()
//                .antMatchers("/authentication/registration/**",
//                        "/authentication/update/password/**").hasAuthority("employee")
//                .antMatchers("/authentication/token/**",
//                        "authentication/userRole/**").hasAuthority("hr")
//                .anyRequest()
//                .authenticated();
    }
}
