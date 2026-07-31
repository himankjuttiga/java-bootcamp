package com.northstar.crm.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The single JSON shape every failure path returns. Immutable: timestamp,
 * status, error (stable code), message, correlationId, and a never-null errors
 * map. Messages must never carry stack traces, SQL, or PII (Module 16).
 */
public final class ErrorResponse {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String correlationId;
    private final Map<String, String> errors;

    public ErrorResponse(int status, String error, String message,
                         String correlationId, Map<String, String> errors) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.correlationId = correlationId;
        this.errors = (errors == null)
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(errors));
    }

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getCorrelationId() { return correlationId; }
    public Map<String, String> getErrors() { return errors; }

    /** Minimal, dependency-free JSON for demo output. Values are escaped. */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
          .append("\"timestamp\":\"").append(esc(timestamp)).append("\",")
          .append("\"status\":").append(status).append(',')
          .append("\"error\":\"").append(esc(error)).append("\",")
          .append("\"message\":\"").append(esc(message)).append("\",")
          .append("\"correlationId\":\"").append(esc(correlationId)).append("\",")
          .append("\"errors\":{");
        int i = 0;
        for (Map.Entry<String, String> e : errors.entrySet()) {
            if (i++ > 0) sb.append(',');
            sb.append('"').append(esc(e.getKey())).append("\":\"")
              .append(esc(e.getValue())).append('"');
        }
        sb.append("}}");
        return sb.toString();
    }

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default   -> out.append(c);
            }
        }
        return out.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
