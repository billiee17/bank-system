package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử {@link CheckingAccount}.
 */
class CheckingAccountTest {
  private static final double DELTA = 1e-9;

  private CheckingAccount account;

  @BeforeEach
  void setUp() {
    account = new CheckingAccount(1001L, 1000.0);
  }

  @Test
  void depositIncreasesBalanceAndRecordsTransaction() {
    account.deposit(500.0);

    assertEquals(1500.0, account.getBalance(), DELTA);
    assertEquals(1, account.getTransactionList().size());
    Transaction recorded = account.getTransactionList().get(0);
    assertEquals(Transaction.TYPE_DEPOSIT_CHECKING, recorded.getType());
    assertEquals(1000.0, recorded.getInitialBalance(), DELTA);
    assertEquals(1500.0, recorded.getFinalBalance(), DELTA);
  }

  @Test
  void depositOfNonPositiveAmountIsRejected() {
    account.deposit(0.0);
    account.deposit(-10.0);

    assertEquals(1000.0, account.getBalance(), DELTA);
    assertTrue(account.getTransactionList().isEmpty());
  }

  @Test
  void withdrawDecreasesBalanceAndRecordsTransaction() {
    account.withdraw(300.0);

    assertEquals(700.0, account.getBalance(), DELTA);
    assertEquals(1, account.getTransactionList().size());
    assertEquals(Transaction.TYPE_WITHDRAW_CHECKING,
        account.getTransactionList().get(0).getType());
  }

  @Test
  void withdrawMoreThanBalanceIsRejected() {
    account.withdraw(1000.01);

    assertEquals(1000.0, account.getBalance(), DELTA);
    assertTrue(account.getTransactionList().isEmpty());
  }

  @Test
  void withdrawExactlyTheBalanceIsAllowed() {
    account.withdraw(1000.0);

    assertEquals(0.0, account.getBalance(), DELTA);
    assertEquals(1, account.getTransactionList().size());
  }

  @Test
  void withdrawOfNonPositiveAmountIsRejected() {
    account.withdraw(-5.0);

    assertEquals(1000.0, account.getBalance(), DELTA);
    assertTrue(account.getTransactionList().isEmpty());
  }
}
