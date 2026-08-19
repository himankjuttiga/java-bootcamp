package com.northstar.crm.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates persistence failures into safe HTTP answers.
 *
 * Nothing from the exception message reaches the client: Hibernate's text carries the SQL, the
 * constraint name and sometimes parameter values. The detail is logged with the correlation id
 * instead, which is the same lab-request-001 the SPA has sent since Lab 35 and the same value
 * customer_status_history records.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private static final String CORRELATION_HEADER = "X-Correlation-Id";
  private static final String DEFAULT_CORRELATION = "lab-request-001";

  /** Duplicate email or public_id: SQLSTATE 23505, translated by Spring, answered as 409. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail duplicate(DataIntegrityViolationException ex, HttpServletRequest request) {
    String correlationId = correlationId(request);
    log.warn("data integrity violation correlation={}", correlationId, ex);
    return problem(
        HttpStatus.CONFLICT,
        "Conflict",
        "That customer already exists. Check the customer id and email.",
        correlationId);
  }

  /**
   * Stale @Version: another writer changed the row first. The session stays valid; the caller
   * reloads and reapplies. Treating this as anything but 409 would either lose an edit or log
   * someone out for a conflict that has nothing to do with authentication.
   */
  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ProblemDetail optimisticLock(
      OptimisticLockingFailureException ex, HttpServletRequest request) {
    String correlationId = correlationId(request);
    log.warn("optimistic lock conflict correlation={}", correlationId, ex);
    return problem(
        HttpStatus.CONFLICT,
        "Conflict",
        "This record changed while you were editing it. Reload and apply your change again.",
        correlationId);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    String fields =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .sorted()
            .reduce((a, b) -> a + "; " + b)
            .orElse("invalid request");
    return problem(HttpStatus.BAD_REQUEST, "Bad Request", fields, correlationId(request));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail badRequest(IllegalArgumentException ex, HttpServletRequest request) {
    return problem(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), correlationId(request));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ProblemDetail notFound(NoSuchElementException ex, HttpServletRequest request) {
    return problem(
        HttpStatus.NOT_FOUND,
        "Not Found",
        "That customer could not be found.",
        correlationId(request));
  }

  private static ProblemDetail problem(
      HttpStatus status, String title, String detail, String correlationId) {
    ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
    body.setTitle(title);
    body.setProperty("correlationId", correlationId);
    return body;
  }

  private static String correlationId(HttpServletRequest request) {
    String header = request.getHeader(CORRELATION_HEADER);
    return (header == null || header.isBlank()) ? DEFAULT_CORRELATION : header;
  }
}
