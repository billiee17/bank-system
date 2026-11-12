package com.practice.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp trừu tượng đại diện cho một tài khoản ngân hàng.
 *
 * <p>Mỗi tài khoản có số tài khoản, số dư và danh sách các giao dịch đã thực hiện.
 */
public abstract class Account {
  /** Mã loại tài khoản vãng lai. */
  public static final String CHECKING_TYPE = "CHECKING";

  /** Mã loại tài khoản tiết kiệm. */
  public static final String SAVINGS_TYPE = "SAVINGS";

  private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

  private long accountNumber;
  private double balance;
  private List<Transaction> transactions;

  /**
   * Tạo tài khoản mới với số dư ban đầu.
   *
   * @param accountNumber số tài khoản
   * @param balance số dư ban đầu
   */
  public Account(long accountNumber, double balance) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.transactions = new ArrayList<>();
  }

  /**
   * Trả về số tài khoản.
   *
   * @return số tài khoản
   */
  public long getAccountNumber() {
    return accountNumber;
  }

  /**
   * Đặt số tài khoản.
   *
   * @param accountNumber số tài khoản mới
   */
  public void setAccountNumber(long accountNumber) {
    this.accountNumber = accountNumber;
  }

  /**
   * Trả về số dư hiện tại.
   *
   * @return số dư hiện tại
   */
  public double getBalance() {
    return balance;
  }

  /**
   * Đặt số dư tài khoản.
   *
   * @param balance số dư mới
   */
  protected void setBalance(double balance) {
    this.balance = balance;
  }

  /**
   * Trả về danh sách giao dịch của tài khoản.
   *
   * @return danh sách giao dịch
   */
  public List<Transaction> getTransactionList() {
    return transactions;
  }

  /**
   * Đặt danh sách giao dịch; nếu truyền {@code null} thì dùng danh sách rỗng.
   *
   * @param transactionList danh sách giao dịch mới
   */
  public void setTransactionList(List<Transaction> transactionList) {
    if (transactionList == null) {
      this.transactions = new ArrayList<>();
    } else {
      this.transactions = transactionList;
    }
  }

  /**
   * Nạp tiền vào tài khoản và ghi lại giao dịch.
   *
   * @param amount số tiền cần nạp
   */
  public abstract void deposit(double amount);

  /**
   * Rút tiền từ tài khoản và ghi lại giao dịch.
   *
   * @param amount số tiền cần rút
   */
  public abstract void withdraw(double amount);

  /**
   * Cộng tiền vào số dư sau khi kiểm tra số tiền hợp lệ.
   *
   * @param amount số tiền cần nạp, phải lớn hơn 0
   * @throws InvalidFundingAmountException nếu số tiền không lớn hơn 0
   */
  protected void doDepositing(double amount) throws InvalidFundingAmountException {
    if (amount <= 0) {
      throw new InvalidFundingAmountException(amount);
    }
    balance += amount;
  }

  /**
   * Trừ tiền khỏi số dư sau khi kiểm tra số tiền và số dư.
   *
   * @param amount số tiền cần rút, phải lớn hơn 0 và không vượt quá số dư
   * @throws InvalidFundingAmountException nếu số tiền không lớn hơn 0
   * @throws InsufficientFundsException nếu số tiền lớn hơn số dư
   */
  protected void doWithdrawing(double amount)
      throws InvalidFundingAmountException, InsufficientFundsException {
    if (amount <= 0) {
      throw new InvalidFundingAmountException(amount);
    }
    if (amount > balance) {
      throw new InsufficientFundsException(amount);
    }
    balance -= amount;
  }

  /**
   * Thêm một giao dịch vào lịch sử; bỏ qua nếu giao dịch là {@code null}.
   *
   * @param transaction giao dịch cần thêm
   */
  public void addTransaction(Transaction transaction) {
    if (transaction != null) {
      transactions.add(transaction);
    }
  }

  /**
   * Ghi nhận một giao dịch thành công và ghi log mức INFO.
   *
   * @param type loại giao dịch, một trong các hằng số {@code Transaction.TYPE_*}
   * @param amount số tiền giao dịch
   * @param initialBalance số dư trước khi giao dịch
   */
  protected void recordTransaction(int type, double amount, double initialBalance) {
    addTransaction(new Transaction(type, amount, initialBalance, balance));
    LOGGER.info("Giao dịch thành công: tài khoản={}, loại={}, số tiền={}, số dư sau={}",
        accountNumber, Transaction.getTypeString(type), amount, balance);
  }

  /**
   * Tạo chuỗi lịch sử giao dịch của tài khoản.
   *
   * @return tiêu đề và danh sách tóm tắt từng giao dịch, mỗi giao dịch một dòng
   */
  public String getTransactionHistory() {
    LOGGER.debug("Lấy lịch sử giao dịch của tài khoản {}", accountNumber);
    String body = transactions.stream()
        .map(Transaction::getTransactionSummary)
        .collect(Collectors.joining("\n"));
    return "Lịch sử giao dịch của tài khoản " + accountNumber + ":\n" + body;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Account)) {
      return false;
    }
    Account other = (Account) obj;
    return this.accountNumber == other.accountNumber;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(accountNumber);
  }
}
