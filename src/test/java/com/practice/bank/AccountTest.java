package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử các hành vi chung của {@link Account} (qua {@link CheckingAccount}).
 */
class AccountTest {
  @Test
  void transactionHistoryHasHeaderAndOneLinePerTransaction() {
    CheckingAccount account = new CheckingAccount(1001L, 1000.0);
    account.deposit(100.0);
    account.withdraw(50.0);

    String history = account.getTransactionHistory();

    assertTrue(history.startsWith("Lịch sử giao dịch của tài khoản 1001:\n"));
    assertEquals(3, history.split("\n").length);
  }

  @Test
  void equalsAndHashCodeDependOnlyOnAccountNumber() {
    Account checking = new CheckingAccount(1001L, 10.0);
    Account savings = new SavingsAccount(1001L, 99.0);
    Account other = new CheckingAccount(1002L, 10.0);

    assertTrue(checking.equals(savings));
    assertEquals(checking.hashCode(), savings.hashCode());
    assertFalse(checking.equals(other));
    assertFalse(checking.equals(null));
    assertFalse(checking.equals("1001"));
  }

  @Test
  void setAccountNumberChangesTheNumber() {
    Account account = new CheckingAccount(1001L, 10.0);

    account.setAccountNumber(2002L);

    assertEquals(2002L, account.getAccountNumber());
  }

  @Test
  void setTransactionListWithNullResetsToEmptyList() {
    Account account = new CheckingAccount(1001L, 10.0);
    account.addTransaction(new Transaction(Transaction.TYPE_DEPOSIT_CHECKING, 1.0, 10.0, 11.0));

    account.setTransactionList(null);

    assertTrue(account.getTransactionList().isEmpty());
  }

  @Test
  void setTransactionListReplacesTheList() {
    Account account = new CheckingAccount(1001L, 10.0);
    List<Transaction> replacement = new ArrayList<>();
    replacement.add(new Transaction(Transaction.TYPE_WITHDRAW_CHECKING, 1.0, 10.0, 9.0));

    account.setTransactionList(replacement);

    assertEquals(1, account.getTransactionList().size());
  }

  @Test
  void addTransactionIgnoresNull() {
    Account account = new CheckingAccount(1001L, 10.0);

    account.addTransaction(null);

    assertTrue(account.getTransactionList().isEmpty());
  }
}
