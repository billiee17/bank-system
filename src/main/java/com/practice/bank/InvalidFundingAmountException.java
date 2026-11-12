package com.practice.bank;

import java.util.Locale;

/**
 * Ngoại lệ khi số tiền giao dịch không hợp lệ.
 */
public class InvalidFundingAmountException extends BankException {
  private static final long serialVersionUID = 1L;

  /**
   * Tạo ngoại lệ cho số tiền giao dịch không hợp lệ.
   *
   * @param amount số tiền không hợp lệ
   */
  public InvalidFundingAmountException(double amount) {
    super(String.format(Locale.US, "Số tiền không hợp lệ: $%.2f", amount));
  }
}
