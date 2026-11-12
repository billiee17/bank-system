package com.practice.bank;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Đại diện cho một giao dịch.
 */
public class Transaction {
  /** Loại giao dịch: nạp tiền vào tài khoản vãng lai. */
  public static final int TYPE_DEPOSIT_CHECKING = 1;

  /** Loại giao dịch: rút tiền từ tài khoản vãng lai. */
  public static final int TYPE_WITHDRAW_CHECKING = 2;

  /** Loại giao dịch: nạp tiền vào tài khoản tiết kiệm. */
  public static final int TYPE_DEPOSIT_SAVINGS = 3;

  /** Loại giao dịch: rút tiền từ tài khoản tiết kiệm. */
  public static final int TYPE_WITHDRAW_SAVINGS = 4;

  private static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);

  private int type;
  private double amount;
  private double initialBalance;
  private double finalBalance;

  /**
   * Tạo một giao dịch.
   *
   * @param type loại giao dịch, một trong các hằng số {@code TYPE_*}
   * @param amount số tiền giao dịch
   * @param initialBalance số dư trước giao dịch
   * @param finalBalance số dư sau giao dịch
   */
  public Transaction(int type, double amount, double initialBalance, double finalBalance) {
    this.type = type;
    this.amount = amount;
    this.initialBalance = initialBalance;
    this.finalBalance = finalBalance;
  }

  /**
   * Trả về loại giao dịch.
   *
   * @return mã loại giao dịch
   */
  public int getType() {
    return type;
  }

  /**
   * Đặt loại giao dịch.
   *
   * @param type mã loại giao dịch
   */
  public void setType(int type) {
    this.type = type;
  }

  /**
   * Trả về số tiền giao dịch.
   *
   * @return số tiền giao dịch
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Đặt số tiền giao dịch.
   *
   * @param amount số tiền giao dịch
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }

  /**
   * Trả về số dư trước giao dịch.
   *
   * @return số dư trước giao dịch
   */
  public double getInitialBalance() {
    return initialBalance;
  }

  /**
   * Đặt số dư trước giao dịch.
   *
   * @param initialBalance số dư trước giao dịch
   */
  public void setInitialBalance(double initialBalance) {
    this.initialBalance = initialBalance;
  }

  /**
   * Trả về số dư sau giao dịch.
   *
   * @return số dư sau giao dịch
   */
  public double getFinalBalance() {
    return finalBalance;
  }

  /**
   * Đặt số dư sau giao dịch.
   *
   * @param finalBalance số dư sau giao dịch
   */
  public void setFinalBalance(double finalBalance) {
    this.finalBalance = finalBalance;
  }

  /**
   * Chuyển mã loại giao dịch thành mô tả dạng chữ.
   *
   * @param type mã loại giao dịch
   * @return mô tả loại giao dịch, hoặc "Không rõ" nếu mã không hợp lệ
   */
  public static String getTypeString(int type) {
    switch (type) {
      case TYPE_DEPOSIT_CHECKING:
        return "Nạp tiền vãng lai";
      case TYPE_WITHDRAW_CHECKING:
        return "Rút tiền vãng lai";
      case TYPE_DEPOSIT_SAVINGS:
        return "Nạp tiền tiết kiệm";
      case TYPE_WITHDRAW_SAVINGS:
        return "Rút tiền tiết kiệm";
      default:
        return "Không rõ";
    }
  }

  /**
   * Tạo chuỗi tóm tắt giao dịch.
   *
   * @return chuỗi mô tả loại giao dịch, số dư đầu, số tiền và số dư cuối
   */
  public String getTransactionSummary() {
    LOGGER.trace("Tạo tóm tắt cho giao dịch loại {}", type);
    return String.format(Locale.US,
        "- Kiểu giao dịch: %s. Số dư ban đầu: $%.2f. Số tiền: $%.2f. Số dư cuối: $%.2f.",
        getTypeString(type), initialBalance, amount, finalBalance);
  }
}
