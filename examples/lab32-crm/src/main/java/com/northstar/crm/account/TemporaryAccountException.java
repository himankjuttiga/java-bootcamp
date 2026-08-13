package com.northstar.crm.account;

/** Transient account-service failure (e.g. HTTP 5xx). Retryable. */
public class TemporaryAccountException extends RuntimeException {
  public TemporaryAccountException(String message) {
    super(message);
  }

  public TemporaryAccountException(String message, Throwable cause) {
    super(message, cause);
  }
}
