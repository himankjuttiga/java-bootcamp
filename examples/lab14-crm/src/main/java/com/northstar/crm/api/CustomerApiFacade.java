package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerMapper;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.dto.CustomerResponseDTO;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.service.CustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * API edge: validate -> map -> service -> response DTO.
 * Correlation: lab-request-001 on failures.
 */
public class CustomerApiFacade {

    private final CustomerService service;
    private final Validator validator;

    public CustomerApiFacade(CustomerService service) {
        this.service = service;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public CustomerResponseDTO create(CustomerRequestDTO request, String correlationId) {
        validateOrThrow(request, correlationId);
        Customer saved = service.createCustomer(
                request.getCustomerId(),
                request.getFullName(),
                request.getEmail(),
                null,                                       // phone not on the DTO
                CustomerStatus.valueOf(request.getStatus())
        );
        return CustomerMapper.toResponse(saved);
    }

    public CustomerResponseDTO get(String customerId, String correlationId) {
        try {
            Customer entity = service.getCustomer(customerId);
            return CustomerMapper.toResponse(entity);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "[" + correlationId + "] customer not found: " + customerId);
        }
    }

    private void validateOrThrow(CustomerRequestDTO request, String correlationId) {
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            System.out.println("validation failed correlationId=" + correlationId + " -> " + msg);
            throw new IllegalArgumentException("[" + correlationId + "] " + msg);
        }
    }
}