package com.northstar.crm.customer;

import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction boundary and business rules. Controllers hold neither.
 *
 * With open-in-view disabled the persistence context closes when these methods return, so every
 * response is mapped to a DTO here, inside the transaction, rather than lazily in the view layer.
 */
@Service
public class CustomerService {

  /** Sort fields a client may name. Anything else is rejected rather than passed to JPA. */
  private static final Set<String> SORTABLE = Set.of("createdAt", "fullName", "status");

  private static final Set<String> STATUSES = Set.of("PROSPECT", "ACTIVE", "CLOSED");

  private static final int MAX_PAGE_SIZE = 100;

  private final CustomerRepository repository;

  public CustomerService(CustomerRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public CustomerResponse create(CreateCustomerRequest request) {
    String email = normalize(request.email());
    CustomerEntity entity =
        new CustomerEntity(request.publicId(), request.fullName().trim(), email, request.status());
    // No pre-check race to lose: a duplicate reaches uk_customer_email, Spring translates the
    // SQLSTATE 23505 into DataIntegrityViolationException, and the handler answers 409.
    return CustomerResponse.from(repository.saveAndFlush(entity));
  }

  @Transactional(readOnly = true)
  public CustomerResponse getByPublicId(String publicId) {
    return repository
        .findByPublicId(publicId)
        .map(CustomerResponse::from)
        .orElseThrow(() -> new NoSuchElementException("customer " + publicId));
  }

  @Transactional(readOnly = true)
  public Page<CustomerResponse> pageByStatus(String status, int page, int size, String sortBy) {
    return repository.findByStatus(status, pageable(page, size, sortBy)).map(CustomerResponse::from);
  }

  /**
   * Status change under optimistic locking. A stale version means another writer already changed
   * this row, so Hibernate's UPDATE ... WHERE version = ? matches nothing and
   * ObjectOptimisticLockingFailureException becomes a 409. Nobody's edit is silently overwritten.
   */
  @Transactional
  public CustomerResponse changeStatus(String publicId, String newStatus) {
    if (!STATUSES.contains(newStatus)) {
      throw new IllegalArgumentException("status must be one of " + STATUSES.stream().sorted().toList());
    }
    CustomerEntity entity =
        repository
            .findByPublicId(publicId)
            .orElseThrow(() -> new NoSuchElementException("customer " + publicId));
    entity.setStatus(newStatus);
    return CustomerResponse.from(repository.saveAndFlush(entity));
  }

  /**
   * Bounded and deterministic. The size cap stops size=1000000 materialising the table into heap;
   * the customerId tie-breaker stops rows appearing on two pages when created_at ties, which is
   * also the order Lab 38's ix_customer_status_created already stores.
   */
  private PageRequest pageable(int page, int size, String sortBy) {
    if (!SORTABLE.contains(sortBy)) {
      throw new IllegalArgumentException(
          "sort must be one of " + SORTABLE.stream().sorted().toList());
    }
    int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    int safePage = Math.max(page, 0);
    Sort sort = Sort.by(Sort.Direction.DESC, sortBy).and(Sort.by(Sort.Direction.DESC, "customerId"));
    return PageRequest.of(safePage, safeSize, sort);
  }

  /** Lowercased and trimmed, so Amina@Example.com and amina@example.com collide as they should. */
  private static String normalize(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
}
