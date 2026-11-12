package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Kiểm thử lớp {@link Transaction}.
 */
class TransactionTest {
  @Test
  void typeStringCoversAllKnownTypes() {
    assertEquals("Nạp tiền vãng lai",
        Transaction.getTypeString(Transaction.TYPE_DEPOSIT_CHECKING));
    assertEquals("Rút tiền vãng lai",
        Transaction.getTypeString(Transaction.TYPE_WITHDRAW_CHECKING));
    assertEquals("Nạp tiền tiết kiệm",
        Transaction.getTypeString(Transaction.TYPE_DEPOSIT_SAVINGS));
    assertEquals("Rút tiền tiết kiệm",
        Transaction.getTypeString(Transaction.TYPE_WITHDRAW_SAVINGS));
  }

  @Test
  void typeStringReturnsFallbackForUnknownType() {
    assertEquals("Không rõ", Transaction.getTypeString(99));
  }

  @Test
  void settersUpdateAllFields() {
    Transaction transaction =
        new Transaction(Transaction.TYPE_DEPOSIT_CHECKING, 1.0, 2.0, 3.0);

    transaction.setType(Transaction.TYPE_WITHDRAW_SAVINGS);
    transaction.setAmount(10.0);
    transaction.setInitialBalance(20.0);
    transaction.setFinalBalance(30.0);

    assertEquals(Transaction.TYPE_WITHDRAW_SAVINGS, transaction.getType());
    assertEquals(10.0, transaction.getAmount(), 1e-9);
    assertEquals(20.0, transaction.getInitialBalance(), 1e-9);
    assertEquals(30.0, transaction.getFinalBalance(), 1e-9);
  }

  @Test
  void summaryContainsTypeAndAmountsWithTwoDecimals() {
    Transaction transaction =
        new Transaction(Transaction.TYPE_DEPOSIT_CHECKING, 100.0, 50.0, 150.0);
    assertEquals(
        "- Kiểu giao dịch: Nạp tiền vãng lai. Số dư ban đầu: $50.00. Số tiền: $100.00."
            + " Số dư cuối: $150.00.",
        transaction.getTransactionSummary());
  }
}
