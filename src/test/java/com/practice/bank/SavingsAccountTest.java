package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Kiểm thử {@link SavingsAccount}: hạn mức rút 1000 và số dư tối thiểu 5000.
 */
class SavingsAccountTest {
  private static final double DELTA = 1e-9;

  @Test
  void withdrawWithinLimitSucceeds() {
    SavingsAccount account = new SavingsAccount(2001L, 9000.0);

    account.withdraw(500.0);

    assertEquals(8500.0, account.getBalance(), DELTA);
    assertEquals(1, account.getTransactionList().size());
    assertEquals(Transaction.TYPE_WITHDRAW_SAVINGS,
        account.getTransactionList().get(0).getType());
  }

  @Test
  void withdrawAboveMaxLimitIsRejected() {
    SavingsAccount account = new SavingsAccount(2001L, 9000.0);

    account.withdraw(1000.01);

    assertEquals(9000.0, account.getBalance(), DELTA);
    assertTrue(account.getTransactionList().isEmpty());
  }

  @Test
  void withdrawThatBreaksMinimumBalanceIsRejected() {
    SavingsAccount account = new SavingsAccount(2001L, 5500.0);

    account.withdraw(600.0);

    assertEquals(5500.0, account.getBalance(), DELTA);
    assertTrue(account.getTransactionList().isEmpty());
  }

  @Test
  void withdrawLeavingExactlyMinimumBalanceIsAllowed() {
    SavingsAccount account = new SavingsAccount(2001L, 5500.0);

    account.withdraw(500.0);

    assertEquals(5000.0, account.getBalance(), DELTA);
    assertEquals(1, account.getTransactionList().size());
  }

  @Test
  void depositIncreasesBalanceAndRecordsTransaction() {
    SavingsAccount account = new SavingsAccount(2001L, 9000.0);

    account.deposit(250.0);

    assertEquals(9250.0, account.getBalance(), DELTA);
    assertEquals(Transaction.TYPE_DEPOSIT_SAVINGS,
        account.getTransactionList().get(0).getType());
  }
}
