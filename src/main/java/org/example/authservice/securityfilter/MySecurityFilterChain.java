package org.example.authservice.securityfilter;


import org.example.authservice.myservice.MyService;
import org.example.authservice.myservice.MyServiceImpl;
import org.example.authservice.userauthentication.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.AuthProvider;

@Configuration
public class MySecurityFilterChain {
    @Autowired
    MyServiceImpl service;
    @Autowired
    JwtFilter filter;

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(){
        DaoAuthenticationProvider authprovider=new DaoAuthenticationProvider();
        authprovider.setPasswordEncoder(encoder());
        authprovider.setUserDetailsService(service);

        return authprovider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securutyFilter(HttpSecurity http) throws Exception{

        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(req->
                        req.requestMatchers(
                                "/welcome",
                                "/registerpage",
                                "/register/**",
                                "/register",
                                "/login",
                                "/login/**",
                                "/loginpage"
                                ).permitAll()
                                .anyRequest().authenticated()

                ) .formLogin(formlogin->formlogin.disable())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();


    }
}
