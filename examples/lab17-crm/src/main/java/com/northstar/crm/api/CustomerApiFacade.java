package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerMapper;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.BusinessException;
import com.northstar.crm.exception.GlobalExceptionHandler;
import com.northstar.crm.service.CustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

/**
 * API boundary for the CRM. Every method returns {@link ApiResult}: Ok with a
 * response DTO, or Fail with a consistent ErrorResponse carrying the
 * correlation id. Catch order is deliberate: typed BusinessException before the
 * generic Exception so conflicts never collapse into a 500.
 */
public class CustomerApiFacade {

    private final CustomerService service;
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final Validator validator;

    public CustomerApiFacade(CustomerService service) {
        this.service = service;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public ApiResult create(CustomerRequestDTO request, String correlationId) {
        requireCorrelation(correlationId);
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return new ApiResult.Fail(handler.fromValidation(violations, correlationId));
        }
        try {
            var saved = service.addCustomer(CustomerMapper.toEntity(request));
            return new ApiResult.Ok(CustomerMapper.toResponse(saved));
        } catch (BusinessException ex) {
            return new ApiResult.Fail(handler.fromBusiness(ex));
        } catch (IllegalStateException ex) {
            // Duplicate id/email policy surfaces from the validator as a conflict.
            return new ApiResult.Fail(handler.fromBusiness(
                    BusinessException.conflict(ex.getMessage(), correlationId)));
        } catch (Exception ex) {
            return new ApiResult.Fail(handler.fromUnexpected(ex, correlationId));
        }
    }

    public ApiResult getById(String customerId, String correlationId) {
        requireCorrelation(correlationId);
        try {
            return service.findById(customerId)
                    .<ApiResult>map(c -> new ApiResult.Ok(CustomerMapper.toResponse(c)))
                    .orElseThrow(() -> BusinessException.notFound(customerId, correlationId));
        } catch (BusinessException ex) {
            return new ApiResult.Fail(handler.fromBusiness(ex));
        } catch (Exception ex) {
            return new ApiResult.Fail(handler.fromUnexpected(ex, correlationId));
        }
    }

    public ApiResult changeStatus(String customerId, CustomerStatus newStatus, String correlationId) {
        requireCorrelation(correlationId);
        try {
            var updated = service.changeStatus(customerId, newStatus, correlationId);
            return new ApiResult.Ok(CustomerMapper.toResponse(updated));
        } catch (BusinessException ex) {
            return new ApiResult.Fail(handler.fromBusiness(ex));
        } catch (Exception ex) {
            return new ApiResult.Fail(handler.fromUnexpected(ex, correlationId));
        }
    }

    private static void requireCorrelation(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("correlationId is required at the API boundary");
        }
    }
}
