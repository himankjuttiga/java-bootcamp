package com.northstar.crm.customer;

import java.time.Instant;

/**
 * Response DTO. Deliberately omits the surrogate customer_id: the API's identifier is publicId,
 * so the database key stays internal and renumbering it never becomes a breaking change.
 * `version` is exposed because a client needs it to make a conditional update.
 */
public record CustomerResponse(
    String publicId,
    String fullName,
    String email,
    String status,
    long version,
    Instant createdAt) {

  static CustomerResponse from(CustomerEntity entity) {
    return new CustomerResponse(
        entity.getPublicId(),
        entity.getFullName(),
        entity.getEmail(),
        entity.getStatus(),
        entity.getVersion() == null ? 0L : entity.getVersion(),
        entity.getCreatedAt());
  }
}
