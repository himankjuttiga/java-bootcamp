package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Customer create(
      @Valid @RequestBody CustomerRequest request,
      @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
    return customerService.create(request, correlationId);
  }

  @GetMapping("/{id}")
  public Customer get(@PathVariable String id) {
    return customerService.get(id);
  }

  /** Lab 35: list endpoint the React SPA loads on mount. */
  @GetMapping
  public List<Customer> list() {
    return customerService.list();
  }

  /** Lab 35: full update used by the SPA edit form. */
  @PutMapping("/{id}")
  public Customer update(
      @PathVariable String id,
      @Valid @RequestBody CustomerRequest request,
      @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
    return customerService.update(id, request, correlationId);
  }
}
