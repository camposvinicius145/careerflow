package com.careerflow.common.config;

import com.careerflow.auth.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${careerflow.security.remember-me-key}") String rememberMeKey)
            throws Exception {
        http.authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/login",
                                                "/register",
                                                "/css/**",
                                                "/js/**",
                                                "/favicon.svg",
                                                "/actuator/health",
                                                "/docs",
                                                "/docs/**",
                                                "/swagger-ui/**",
                                                "/api-docs/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .formLogin(
                        form ->
                                form.loginPage("/login")
                                        .defaultSuccessUrl("/dashboard", true)
                                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .rememberMe(remember -> remember.key(rememberMeKey));
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(UserAccountRepository users) {
        return username ->
                users.findByEmailIgnoreCase(username)
                        .map(
                                account ->
                                        User.withUsername(account.getEmail())
                                                .password(account.getPasswordHash())
                                                .roles("USER")
                                                .build())
                        .orElseThrow(
                                () ->
                                        new org.springframework.security.core.userdetails
                                                .UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
