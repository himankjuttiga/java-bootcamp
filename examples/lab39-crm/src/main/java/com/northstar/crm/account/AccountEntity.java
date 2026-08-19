package com.northstar.crm.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Objects;

/**
 * Maps the `account` table.
 *
 * The foreign key is a plain Long, not @ManyToOne CustomerEntity. With open-in-view disabled a
 * lazy association touched during serialisation throws LazyInitializationException, and a list
 * that walks the association becomes N+1. A Long cannot do either. The service resolves the
 * customer when it needs one.
 *
 * Money is `long balanceCents`, exact integer minor units, matching balance_cents BIGINT from
 * Labs 37 and 38. Never double: binary floating point cannot represent 0.10, so balances drift
 * once they are summed. Had the column been NUMERIC(19,2) the field would be BigDecimal.
 */
@Entity
@Table(name = "account")
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  private Long accountId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "account_number", nullable = false, unique = true, updatable = false)
  private String accountNumber;

  @Column(name = "balance_cents", nullable = false)
  private long balanceCents;

  @Version
  private Long version;

  @Column(name = "opened_at", nullable = false, updatable = false)
  private Instant openedAt = Instant.now();

  protected AccountEntity() {
    // required by JPA
  }

  public AccountEntity(Long customerId, String accountNumber, long balanceCents) {
    this.customerId = customerId;
    this.accountNumber = accountNumber;
    this.balanceCents = balanceCents;
  }

  public Long getAccountId() { return accountId; }
  public Long getCustomerId() { return customerId; }
  public String getAccountNumber() { return accountNumber; }
  public long getBalanceCents() { return balanceCents; }
  public Long getVersion() { return version; }
  public Instant getOpenedAt() { return openedAt; }

  public void setBalanceCents(long balanceCents) { this.balanceCents = balanceCents; }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof AccountEntity that)) return false;
    return accountNumber != null && accountNumber.equals(that.accountNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(accountNumber);
  }
}
