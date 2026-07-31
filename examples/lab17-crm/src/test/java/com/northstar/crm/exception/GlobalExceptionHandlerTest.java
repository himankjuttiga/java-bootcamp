package com.northstar.crm.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFoundTo404() {
        ErrorResponse err = handler.fromBusiness(
                BusinessException.notFound("CUS-9999", "lab-request-001"));
        assertEquals(404, err.getStatus());
        assertEquals("CUSTOMER_NOT_FOUND", err.getError());
        assertEquals("lab-request-001", err.getCorrelationId());
    }

    @Test
    void mapsConflictTo409() {
        ErrorResponse err = handler.fromBusiness(BusinessException.conflict(
                "illegal status transition ACTIVE -> PROSPECT", "lab-request-001"));
        assertEquals(409, err.getStatus());
        assertEquals("BUSINESS_CONFLICT", err.getError());
        assertEquals("lab-request-001", err.getCorrelationId());
    }

    @Test
    void unexpectedIsGeneric500() {
        ErrorResponse err = handler.fromUnexpected(
                new RuntimeException("java.sql.SQLException: table CUSTOMERS at line 42"),
                "lab-request-001");
        assertEquals(500, err.getStatus());
        assertEquals("INTERNAL_ERROR", err.getError());
        assertFalse(err.toJson().contains("SQLException"));
        assertFalse(err.toJson().contains("line 42"));
    }
}
