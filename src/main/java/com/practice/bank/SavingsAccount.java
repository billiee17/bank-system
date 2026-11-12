package com.practice.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tài khoản tiết kiệm, có quy định riêng về hạn mức rút tiền và số dư tối thiểu.
 */
public class SavingsAccount extends Account {
  /** Số tiền tối đa được rút trong một giao dịch. */
  private static final double MAX_WITHDRAW = 1000.0;

  /** Số dư tối thiểu phải giữ lại sau khi rút. */
  private static final double MIN_BALANCE = 5000.0;

  private static final Logger LOGGER = LoggerFactory.getLogger(SavingsAccount.class);

  /**
   * Tạo tài khoản tiết kiệm.
   *
   * @param accountNumber số tài khoản
   * @param balance số dư ban đầu
   */
  public SavingsAccount(long accountNumber, double balance) {
    super(accountNumber, balance);
  }

  @Override
  public void deposit(double amount) {
    LOGGER.debug("Đang xử lý nạp tiền: tài khoản={}, số tiền={}", getAccountNumber(), amount);
    double initialBalance = getBalance();
    try {
      doDepositing(amount);
      recordTransaction(Transaction.TYPE_DEPOSIT_SAVINGS, amount, initialBalance);
    } catch (BankException e) {
      LOGGER.warn("Nạp tiền bị từ chối: tài khoản={}, lý do={}",
          getAccountNumber(), e.getMessage());
    }
  }

  @Override
  public void withdraw(double amount) {
    double initialBalance = getBalance();
    try {
      validateWithdrawal(amount, initialBalance);
      doWithdrawing(amount);
      recordTransaction(Transaction.TYPE_WITHDRAW_SAVINGS, amount, initialBalance);
    } catch (BankException e) {
      LOGGER.warn("Rút tiền bị từ chối: tài khoản={}, lý do={}",
          getAccountNumber(), e.getMessage());
    }
  }

  private void validateWithdrawal(double amount, double currentBalance) throws BankException {
    if (amount > MAX_WITHDRAW) {
      throw new InvalidFundingAmountException(amount);
    }
    if (currentBalance - amount < MIN_BALANCE) {
      throw new InsufficientFundsException(amount);
    }
  }
}
