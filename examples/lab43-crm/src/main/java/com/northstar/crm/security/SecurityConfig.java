package com.northstar.crm.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lab 40 remediation, part one: authentication, which Lab 39 had none of.
 *
 * Every route under /api requires an authenticated AGENT. Anything else is denied rather than
 * permitted, so a route added later is closed until someone opens it deliberately.
 */
@Configuration
@EnableConfigurationProperties(AgentProperties.class)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return http
        // No cookie session and no browser form, so there is no CSRF vector to protect:
        // credentials are sent explicitly on every request rather than attached by the browser.
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Container HEALTHCHECK and orchestrator probes read readiness with no credentials;
            // env/beans stay unexposed and denied.
            .requestMatchers("/actuator/health/**").permitAll()
            .requestMatchers("/api/**").hasRole("AGENT")
            .anyRequest().denyAll())
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  /**
   * In-memory agents for the lab. Real deployments federate to the identity provider; what
   * matters here is that the passwords arrive from the environment and are stored hashed,
   * never in a file and never in plain text.
   */
  @Bean
  public UserDetailsService agentDetailsService(AgentProperties properties, PasswordEncoder encoder) {
    List<UserDetails> agents = new ArrayList<>();
    properties.agents().forEach((username, password) ->
        agents.add(User.withUsername(username)
            .password(encoder.encode(password))
            .roles("AGENT")
            .build()));
    return new InMemoryUserDetailsManager(agents);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
