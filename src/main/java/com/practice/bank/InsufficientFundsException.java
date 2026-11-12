package com.practice.bank;

import java.util.Locale;

/**
 * Ngoại lệ khi số dư tài khoản không đủ.
 */
public class InsufficientFundsException extends BankException {
  private static final long serialVersionUID = 1L;

  /**
   * Tạo ngoại lệ cho giao dịch không đủ số dư.
   *
   * @param amount số tiền của giao dịch bị từ chối
   */
  public InsufficientFundsException(double amount) {
    super(String.format(
        Locale.US, "Số dư tài khoản không đủ $%.2f để thực hiện giao dịch", amount));
  }
}
