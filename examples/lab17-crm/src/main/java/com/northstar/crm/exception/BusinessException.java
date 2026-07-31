package com.northstar.crm.exception;

/**
 * Typed domain/API failure carrying a stable error code, an HTTP-like status
 * hint, and the request correlation id. Handlers map on the code, not on
 * parsed English messages (Module 16). Factories cover the two cases this lab
 * demonstrates: not-found (404) and business conflict (409).
 */
public class BusinessException extends RuntimeException {

    private final String code;
    private final int statusHint;
    private final String correlationId;

    public BusinessException(String code, String message, int statusHint, String correlationId) {
        super(message);
        this.code = code;
        this.statusHint = statusHint;
        this.correlationId = correlationId;
    }

    public String getCode() { return code; }
    public int getStatusHint() { return statusHint; }
    public String getCorrelationId() { return correlationId; }

    /** Missing resource, e.g. CUS-9999. Maps to 404. */
    public static BusinessException notFound(String customerId, String correlationId) {
        return new BusinessException(
                "CUSTOMER_NOT_FOUND",
                "Customer not found: " + customerId,
                404,
                correlationId);
    }

    /** Business rule or state violation, e.g. an illegal status transition. Maps to 409. */
    public static BusinessException conflict(String message, String correlationId) {
        return new BusinessException("BUSINESS_CONFLICT", message, 409, correlationId);
    }
}
