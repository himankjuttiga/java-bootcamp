package com.northstar.crm.integration;

import com.northstar.crm.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApiIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void getAminaReturns200() {
        ResponseEntity<Customer> res =
                rest.getForEntity(url("/api/customers/CUS-1001"), Customer.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("CUS-1001", res.getBody().getCustomerId());
        assertEquals("Amina Khan", res.getBody().getFullName());
    }

    @Test
    void createEchoesCorrelationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Correlation-Id", "lab-request-001");
        String body = """
            {"customerId":"CUS-3001","fullName":"New Person","email":"new.person@example.com","status":"PROSPECT"}
            """;

        ResponseEntity<Customer> created = rest.exchange(
                url("/api/customers"), HttpMethod.POST,
                new HttpEntity<>(body, headers), Customer.class);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals("lab-request-001", created.getHeaders().getFirst("X-Correlation-Id"));
        assertNotNull(created.getBody());
        assertEquals("CUS-3001", created.getBody().getCustomerId());
    }

    @Test
    void missingCustomerReturns404() {
        ResponseEntity<String> res =
                rest.getForEntity(url("/api/customers/CUS-9999"), String.class);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }
}
