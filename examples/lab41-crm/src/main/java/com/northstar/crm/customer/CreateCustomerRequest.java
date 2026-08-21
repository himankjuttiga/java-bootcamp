package com.northstar.crm.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO. Binding JSON straight onto an entity would let a caller set version, ids, or any
 * column added later; a record with explicit fields cannot be over-posted.
 */
public record CreateCustomerRequest(
    @NotBlank @Pattern(regexp = "CUS-[0-9A-Za-z-]{1,27}") String publicId,
    @NotBlank @Size(max = 200) String fullName,
    @NotBlank @Email @Size(max = 320) String email,
    @NotBlank @Pattern(regexp = "PROSPECT|ACTIVE|CLOSED") String status) {}
