package com.practice.bank;

/**
 * Ngoại lệ chung trong hệ thống ngân hàng.
 */
public class BankException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * Tạo ngoại lệ với thông báo lỗi.
   *
   * @param message thông báo lỗi
   */
  public BankException(String message) {
    super(message);
  }
}
