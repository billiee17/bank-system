package com.practice.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ngân hàng: quản lý danh sách khách hàng và đọc dữ liệu khách hàng từ luồng đầu vào.
 */
public class Bank {
  /** Số CMND gồm đúng 9 chữ số, đứng cuối dòng khai báo khách hàng. */
  private static final Pattern ID_PATTERN = Pattern.compile("\\d{9}");

  private static final Logger LOGGER = LoggerFactory.getLogger(Bank.class);

  private List<Customer> customers;

  /**
   * Tạo ngân hàng với danh sách khách hàng rỗng.
   */
  public Bank() {
    this.customers = new ArrayList<>();
  }

  /**
   * Trả về danh sách khách hàng.
   *
   * @return danh sách khách hàng
   */
  public List<Customer> getCustomerList() {
    return customers;
  }

  /**
   * Đặt danh sách khách hàng; nếu truyền {@code null} thì dùng danh sách rỗng.
   *
   * @param customerList danh sách khách hàng mới
   */
  public void setCustomerList(List<Customer> customerList) {
    if (customerList == null) {
      this.customers = new ArrayList<>();
    } else {
      this.customers = customerList;
    }
  }

  /**
   * Đọc danh sách khách hàng và tài khoản từ luồng đầu vào.
   *
   * <p>Một dòng kết thúc bằng số CMND 9 chữ số là dòng khai báo khách hàng. Các dòng sau đó có
   * dạng {@code <số tài khoản> <CHECKING|SAVINGS> <số dư>} là tài khoản của khách hàng đó.
   *
   * @param inputStream luồng dữ liệu đầu vào; luồng này không bị đóng bởi phương thức
   */
  public void readCustomerList(InputStream inputStream) {
    LOGGER.debug("Bắt đầu đọc dữ liệu khách hàng");
    if (inputStream == null) {
      LOGGER.warn("Luồng dữ liệu đầu vào là null, bỏ qua việc đọc khách hàng");
      return;
    }
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    Customer current = null;
    try {
      String line = reader.readLine();
      while (line != null) {
        current = processLine(line.trim(), current);
        line = reader.readLine();
      }
    } catch (IOException | NumberFormatException e) {
      LOGGER.error("Lỗi khi đọc dữ liệu khách hàng", e);
    }
    LOGGER.info("Đã đọc xong dữ liệu, tổng số khách hàng: {}", customers.size());
  }

  private Customer processLine(String line, Customer current) {
    int lastSpace = line.lastIndexOf(' ');
    if (lastSpace <= 0) {
      return current;
    }
    String token = line.substring(lastSpace + 1).trim();
    if (ID_PATTERN.matcher(token).matches()) {
      Customer customer =
          new Customer(Long.parseLong(token), line.substring(0, lastSpace).trim());
      customers.add(customer);
      LOGGER.debug("Đã thêm khách hàng thứ {}", customers.size());
      return customer;
    }
    if (current != null) {
      addAccountFromLine(current, line);
    }
    return current;
  }

  private void addAccountFromLine(Customer customer, String line) {
    String[] parts = line.split("\\s+");
    if (parts.length < 3) {
      return;
    }
    long accountNumber = Long.parseLong(parts[0]);
    double balance = Double.parseDouble(parts[2]);
    if (Account.CHECKING_TYPE.equals(parts[1])) {
      customer.addAccount(new CheckingAccount(accountNumber, balance));
    } else if (Account.SAVINGS_TYPE.equals(parts[1])) {
      customer.addAccount(new SavingsAccount(accountNumber, balance));
    } else {
      LOGGER.warn("Bỏ qua tài khoản có loại không hợp lệ: {}", parts[1]);
    }
  }

  /**
   * Sắp xếp danh sách khách hàng theo số CMND tăng dần và trả về thông tin của họ.
   *
   * @return thông tin từng khách hàng, mỗi khách hàng một dòng
   */
  public String getCustomersInfoByIdOrder() {
    customers.sort(Comparator.comparingLong(Customer::getIdNumber));
    return joinCustomerInfo(customers);
  }

  /**
   * Trả về thông tin khách hàng sắp xếp theo họ tên, nếu trùng thì theo số CMND.
   *
   * @return thông tin từng khách hàng, mỗi khách hàng một dòng
   */
  public String getCustomersInfoByNameOrder() {
    List<Customer> sorted = new ArrayList<>(customers);
    sorted.sort(Comparator.comparing(Customer::getFullName)
        .thenComparingLong(Customer::getIdNumber));
    return joinCustomerInfo(sorted);
  }

  private static String joinCustomerInfo(List<Customer> list) {
    return list.stream()
        .map(Customer::getCustomerInfo)
        .collect(Collectors.joining("\n"));
  }
}
