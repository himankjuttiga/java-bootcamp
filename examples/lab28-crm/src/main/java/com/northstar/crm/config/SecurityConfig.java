package com.northstar.crm.config;

import com.northstar.crm.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http.csrf(csrf -> csrf.disable());

    // Stateless: no HTTP session; every request authenticates from its Bearer token.
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/login", "/actuator/health", "/error").permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/customers/**").hasAnyRole("AGENT", "ADMIN")
        .anyRequest().authenticated());

    // API-style: unauthenticated -> 401 (not a form-login redirect or 403).
    http.exceptionHandling(ex -> ex.authenticationEntryPoint(
        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));

    // Validate the JWT before the username/password filter runs.
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
