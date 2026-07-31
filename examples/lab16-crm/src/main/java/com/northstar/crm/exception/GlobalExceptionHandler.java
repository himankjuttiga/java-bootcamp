package com.northstar.crm.exception;

import jakarta.validation.ConstraintViolation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Single mapping center for the three failure families. Previews the Spring
 * {@code @ControllerAdvice} pattern arriving in a later module: specific
 * business/validation mappings first, generic fallback last, and never a leak
 * of internal detail on the 500 path.
 */
public class GlobalExceptionHandler {

    /** Typed domain failure -> its own status hint and stable code. */
    public ErrorResponse fromBusiness(BusinessException ex) {
        return new ErrorResponse(
                ex.getStatusHint(),
                ex.getCode(),
                ex.getMessage(),
                ex.getCorrelationId(),
                Map.of());
    }

    /** Bean Validation violations -> 400 with a stable field-error map. */
    public ErrorResponse fromValidation(
            Set<? extends ConstraintViolation<?>> violations, String correlationId) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : violations) {
            fields.putIfAbsent(v.getPropertyPath().toString(), v.getMessage());
        }
        return new ErrorResponse(
                400, "VALIDATION_FAILED", "Validation failed", correlationId, fields);
    }

    /** Anything unexpected -> generic 500. Log the stack internally; never return it. */
    public ErrorResponse fromUnexpected(Exception ex, String correlationId) {
        // A real logger would record ex (stack + message) here at ERROR level.
        return new ErrorResponse(
                500, "INTERNAL_ERROR", "Unexpected server error", correlationId, Map.of());
    }
}
