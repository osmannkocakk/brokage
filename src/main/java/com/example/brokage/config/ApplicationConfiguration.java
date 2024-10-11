package com.example.brokage.config;

import com.example.brokage.customer.CustomerRepo;
import com.example.brokage.signin.AppUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final CustomerRepo userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(new AppUserDetailsService(userRepository));
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }
}
