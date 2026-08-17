package com.northstar.crm.config;

import com.northstar.crm.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Default profile: Lab 28/29 rules unchanged, customer APIs require a Bearer token. */
  @Bean
  @Profile("!lab35")
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http.cors(Customizer.withDefaults())
        .headers(SecurityConfig::browserHardening)
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/actuator/health", "/error").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/customers/**").hasAnyRole("AGENT", "ADMIN")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN)))
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Lab 35 profile only: the React SPA has no login screen yet (Lab 36 adds tokens and route
   * guards), so customer reads and writes are open while the frontend/API contract is wired.
   *
   * Run with: mvn spring-boot:run -Dspring-boot.run.profiles=lab35
   *
   * The default profile above keeps the Lab 29 role rules and its no-token 401 test green,
   * so this concession is scoped and reversible instead of a permanent hole.
   */
  @Bean
  @Profile("lab35")
  SecurityFilterChain lab35DevFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http.cors(Customizer.withDefaults())
        .headers(SecurityConfig::browserHardening)
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/actuator/health", "/error").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // TODO Lab 36: restore .hasAnyRole("AGENT", "ADMIN") once the SPA sends a token.
            .requestMatchers("/api/customers/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN)))
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Lab 36 — browser security headers on every API response.
   *
   * CSP is defence in depth, not the control: correct escaping in the SPA is what stops XSS.
   * The API serves JSON only, so the policy can be maximally restrictive here.
   * frame-ancestors 'none' blocks clickjacking; nosniff stops content-type guessing;
   * no-referrer keeps customer ids out of outbound Referer headers.
   *
   * Production adds HSTS over HTTPS: Strict-Transport-Security: max-age=31536000; includeSubDomains.
   */
  private static void browserHardening(HeadersConfigurer<HttpSecurity> headers) {
    headers
        .contentSecurityPolicy(csp ->
            csp.policyDirectives("default-src 'none'; frame-ancestors 'none'; object-src 'none'"))
        .referrerPolicy(referrer ->
            referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
        .frameOptions(frame -> frame.deny());
  }
}
