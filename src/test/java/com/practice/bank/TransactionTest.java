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
  void summaryContainsTypeAndAmountsWithTwoDecimals() {
    Transaction transaction =
        new Transaction(Transaction.TYPE_DEPOSIT_CHECKING, 100.0, 50.0, 150.0);
    assertEquals(
        "- Kiểu giao dịch: Nạp tiền vãng lai. Số dư ban đầu: $50.00. Số tiền: $100.00."
            + " Số dư cuối: $150.00.",
        transaction.getTransactionSummary());
  }
}
