package com.northstar.crm.customer;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Focused reads only. No unbounded findAll() in application code: every list is paged.
 * The id type is Long, the surrogate. Business lookups go through publicId.
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByPublicId(String publicId);

  Optional<CustomerEntity> findByEmail(String email);

  /**
   * Cheap pre-check, never the guarantee: another request can win between this and the insert,
   * so uk_customer_email remains the control and the 409 path still has to exist.
   */
  boolean existsByEmail(String email);

  Page<CustomerEntity> findByStatus(String status, Pageable pageable);
}
