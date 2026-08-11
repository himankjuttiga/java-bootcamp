package com.northstar.crm.api;

import com.northstar.crm.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
        .sorted(Comparator.comparing(ErrorResponse.FieldViolation::getField))
        .toList();
    ErrorResponse body = envelope(HttpStatus.BAD_REQUEST, "Validation failed", request);
    body.setViolations(violations);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(IllegalArgumentException ex, WebRequest request) {
    ErrorResponse body = envelope(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex, WebRequest request) {
    ErrorResponse body = envelope(HttpStatus.CONFLICT, ex.getMessage(), request);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleSafe500(Exception ex, WebRequest request) {
    // Preserve intentional ResponseStatusException codes (e.g. login 401) instead of masking them as 500.
    if (ex instanceof ResponseStatusException rse) {
      HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());
      ErrorResponse body = envelope(status, rse.getReason(), request);
      return ResponseEntity.status(status).body(body);
    }
    // Never leak stack traces / SQL to the client; details belong in server logs.
    ErrorResponse body = envelope(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private ErrorResponse envelope(HttpStatus status, String message, WebRequest request) {
    ErrorResponse body = new ErrorResponse();
    body.setStatus(status.value());
    body.setError(status.getReasonPhrase());
    body.setMessage(message);
    body.setCorrelationId(correlationId(request));
    return body;
  }

  private String correlationId(WebRequest request) {
    String cid = request.getHeader("X-Correlation-Id");
    return (cid == null || cid.isBlank()) ? "lab-request-001" : cid;
  }
}
