package com.northstar.crm.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Objects;

/**
 * Maps the `customer` table created by V1__crm_schema.sql.
 *
 * Never returned from a controller. Entities leak internal ids and the version counter, couple
 * the API to column names, and serialise lazily-loaded state outside the transaction. DTOs
 * (CreateCustomerRequest / CustomerResponse) cross the boundary instead.
 */
@Entity
@Table(name = "customer")
public class CustomerEntity {

  /** Surrogate key. IDENTITY matches BIGSERIAL: the database assigns it. Internal only. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id")
  private Long customerId;

  /** Immutable business key, CUS-1001. This is what the API and the SPA use. */
  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private String publicId;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String status;

  /**
   * The agent this customer belongs to. Object-level authorisation is decided on this column:
   * a role check alone would let any agent read every customer, which is the finding this lab
   * remediates (lab40-001).
   */
  @Column(name = "owner_agent", nullable = false)
  private String ownerAgent;

  /** Optimistic lock. Hibernate maintains this; application code never sets it. */
  @Version
  private Long version;

  /** TIMESTAMPTZ maps to Instant, never LocalDateTime: an offset-free type loses the moment. */
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  protected CustomerEntity() {
    // required by JPA
  }

  public CustomerEntity(
      String publicId, String fullName, String email, String status, String ownerAgent) {
    this.publicId = publicId;
    this.fullName = fullName;
    this.email = email;
    this.status = status;
    this.ownerAgent = ownerAgent;
  }

  @PreUpdate
  void touch() {
    this.updatedAt = Instant.now();
  }

  public Long getCustomerId() { return customerId; }
  public String getPublicId() { return publicId; }
  public String getFullName() { return fullName; }
  public String getEmail() { return email; }
  public String getStatus() { return status; }
  public String getOwnerAgent() { return ownerAgent; }
  public Long getVersion() { return version; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

  public void setFullName(String fullName) { this.fullName = fullName; }
  public void setEmail(String email) { this.email = email; }
  public void setStatus(String status) { this.status = status; }

  /**
   * Equality on the immutable business key, not on the surrogate id.
   *
   * A generated id is null until flush, so an entity added to a HashSet before saving would be
   * lost the moment the id is assigned and its hash changes. publicId is set at construction and
   * never updated, so identity is stable across the whole lifecycle.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof CustomerEntity that)) return false;
    return publicId != null && publicId.equals(that.publicId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(publicId);
  }

  @Override
  public String toString() {
    return "CustomerEntity{publicId=" + publicId + ", status=" + status + "}";
  }
}
