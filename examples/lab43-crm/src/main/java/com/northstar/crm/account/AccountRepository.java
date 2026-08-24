package com.northstar.crm.account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

  /** Accounts for one customer. Ravi (CUS-1002) correctly returns an empty list, not an error. */
  List<AccountEntity> findByCustomerId(Long customerId);

  Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
