package com.northstar.crm.customer;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** DTOs in, DTOs out. No entity crosses this boundary. */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService service;

  public CustomerController(CustomerService service) {
    this.service = service;
  }

  @GetMapping
  public Page<CustomerResponse> list(
      @RequestParam(defaultValue = "ACTIVE") String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sort) {
    return service.pageByStatus(status, page, size, sort);
  }

  @GetMapping("/{publicId}")
  public CustomerResponse get(@PathVariable String publicId) {
    return service.getByPublicId(publicId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
    return service.create(request);
  }

  @PatchMapping("/{publicId}/status")
  public CustomerResponse changeStatus(
      @PathVariable String publicId, @RequestParam String status) {
    return service.changeStatus(publicId, status);
  }
}
