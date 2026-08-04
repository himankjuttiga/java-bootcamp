package com.northstar.crm.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class CustomerLoggingIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void getAminaLogsCorrelationWithoutPii(CapturedOutput output) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", "lab-request-001");

        ResponseEntity<String> response = rest.exchange(
                "http://localhost:" + port + "/api/customers/CUS-1001",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String logs = output.getOut();
        assertTrue(logs.contains("lab-request-001"), "correlation id should appear in logs");
        assertTrue(logs.contains("CUS-1001"), "customer id should appear in logs");
        assertFalse(logs.contains("Amina"), "full name (PII) must never appear in logs");
    }
}
