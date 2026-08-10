package com.northstar.crm;

import com.northstar.crm.account.AccountRepository;
import com.northstar.crm.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransferServiceTest {
  @Autowired TransferService transferService;
  @Autowired AccountRepository accounts;

  @Test
  void forceFailRollsBack() {
    BigDecimal before = accounts.findById("ACC-MAIN-1001").orElseThrow().getBalance();
    assertThrows(Exception.class, () ->
        transferService.transfer("ACC-MAIN-1001", "ACC-FORCE-FAIL", new BigDecimal("10.00")));
    // MAIN balance must equal 'before' after rollback (passes once @Transactional works)
    assertEquals(before, accounts.findById("ACC-MAIN-1001").orElseThrow().getBalance());
  }

  @Test
  void happyPathMovesFunds() {
    BigDecimal fromBefore = accounts.findById("ACC-MAIN-1001").orElseThrow().getBalance();
    BigDecimal toBefore = accounts.findById("ACC-LOYALTY-1001").orElseThrow().getBalance();

    transferService.transfer("ACC-MAIN-1001", "ACC-LOYALTY-1001", new BigDecimal("5.00"));

    BigDecimal fromAfter = accounts.findById("ACC-MAIN-1001").orElseThrow().getBalance();
    BigDecimal toAfter = accounts.findById("ACC-LOYALTY-1001").orElseThrow().getBalance();

    // compareTo avoids BigDecimal scale sensitivity
    assertEquals(0, fromBefore.subtract(new BigDecimal("5.00")).compareTo(fromAfter));
    assertEquals(0, toBefore.add(new BigDecimal("5.00")).compareTo(toAfter));
  }
}
