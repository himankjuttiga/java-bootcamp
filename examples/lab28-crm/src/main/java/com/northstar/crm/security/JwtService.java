package com.northstar.crm.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final String secret;

  public JwtService(@Value("${northstar.security.jwt-secret}") String secret) {
    this.secret = secret;
  }

  // Lab stub format: lab.<subject>.<role>.<sig>, where sig binds the token to the secret.
  // Full-path option: replace with a real HS256 (eyJ...) JWT via jjwt.
  public String issueToken(String subject, String role) {
    return "lab." + subject + "." + role + "." + sig();
  }

  public String parseSubject(String token) {
    return parts(token)[1];
  }

  public String parseRole(String token) {
    return parts(token)[2];
  }

  private String sig() {
    return Integer.toHexString(secret.hashCode());
  }

  private String[] parts(String token) {
    if (token == null) {
      throw new IllegalArgumentException("Missing token");
    }
    String[] p = token.split("\\.");
    if (p.length != 4 || !"lab".equals(p[0]) || !sig().equals(p[3])) {
      throw new IllegalArgumentException("Invalid token");
    }
    return p;
  }
}
