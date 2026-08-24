package com.northstar.crm.security;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Agent logins, keyed by username, values read from the environment.
 *
 * There is no default password anywhere in this project. A missing variable is a startup
 * failure, which is the same rule the datasource password has followed since Lab 39.
 */
@ConfigurationProperties(prefix = "crm")
public record AgentProperties(Map<String, String> agents) {}
