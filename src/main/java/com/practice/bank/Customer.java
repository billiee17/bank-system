package com.practice.bank;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp Customer đại diện cho một khách hàng.
 */
public class Customer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Customer.class);

  private long idNumber;
  private String fullName;
  private List<Account> accountList;

  /**
   * Constructor không tham số (phục vụ MyTest).
   */
  public Customer() {
    this(0L, "");
  }

  /**
   * Tạo khách hàng với số CMND và họ tên.
   *
   * @param idNumber số CMND
   * @param fullName họ tên đầy đủ
   */
  public Customer(long idNumber, String fullName) {
    this.idNumber = idNumber;
    this.fullName = fullName;
    this.accountList = new ArrayList<>();
  }

  /**
   * Trả về số CMND.
   *
   * @return số CMND
   */
  public long getIdNumber() {
    return idNumber;
  }

  /**
   * Đặt số CMND.
   *
   * @param idNumber số CMND mới
   */
  public void setIdNumber(long idNumber) {
    this.idNumber = idNumber;
  }

  /**
   * Trả về họ tên đầy đủ.
   *
   * @return họ tên đầy đủ
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Đặt họ tên đầy đủ.
   *
   * @param fullName họ tên mới
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Trả về danh sách tài khoản của khách hàng.
   *
   * @return danh sách tài khoản
   */
  public List<Account> getAccountList() {
    return accountList;
  }

  /**
   * Đặt danh sách tài khoản; nếu truyền {@code null} thì dùng danh sách rỗng.
   *
   * @param accountList danh sách tài khoản mới
   */
  public void setAccountList(List<Account> accountList) {
    if (accountList == null) {
      this.accountList = new ArrayList<>();
    } else {
      this.accountList = accountList;
    }
  }

  /**
   * Thêm tài khoản cho khách hàng; bỏ qua nếu tài khoản là {@code null} hoặc đã tồn tại.
   *
   * @param account tài khoản cần thêm
   */
  public void addAccount(Account account) {
    if (account == null) {
      return;
    }
    if (accountList.contains(account)) {
      LOGGER.debug("Bỏ qua tài khoản đã tồn tại: {}", account.getAccountNumber());
      return;
    }
    accountList.add(account);
  }

  /**
   * Xóa tài khoản khỏi khách hàng.
   *
   * @param account tài khoản cần xóa
   */
  public void removeAccount(Account account) {
    if (account == null) {
      return;
    }
    accountList.remove(account);
  }

  /**
   * Trả về thông tin khách hàng dạng text.
   *
   * @return chuỗi gồm số CMND và họ tên
   */
  public String getCustomerInfo() {
    return "Số CMND: " + idNumber + ". Họ tên: " + fullName + ".";
  }
}
