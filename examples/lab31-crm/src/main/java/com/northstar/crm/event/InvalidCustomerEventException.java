package com.northstar.crm.event;

/** Contract violation (e.g. key does not match customerId). Non-retryable -> routed to DLT. */
public class InvalidCustomerEventException extends RuntimeException {
  public InvalidCustomerEventException(String message) {
    super(message);
  }
}
