package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử {@link Bank}: đọc dữ liệu và sắp xếp khách hàng.
 */
class BankTest {
  /** Khách hàng có CMND nhỏ hơn lại có tên xếp sau, để phân biệt hai kiểu sắp xếp. */
  private static final String SAMPLE =
      "Trần Văn An 111111111\n"
          + "2001 SAVINGS 9000\n"
          + "\n"
          + "Lê Thị Bình 333333333\n"
          + "1001 CHECKING 2500.75\n"
          + "1002 SAVINGS 7000\n"
          + "1003 UNKNOWN 5\n"
          + "dòng lỗi\n";

  private Bank bank;

  @BeforeEach
  void setUp() {
    bank = new Bank();
  }

  private static InputStream streamOf(String text) {
    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void readCustomerListParsesCustomersAndAccounts() {
    bank.readCustomerList(streamOf(SAMPLE));

    assertEquals(2, bank.getCustomerList().size());
    Customer first = bank.getCustomerList().get(0);
    assertEquals(111111111L, first.getIdNumber());
    assertEquals("Trần Văn An", first.getFullName());
    assertEquals(1, first.getAccountList().size());
    Customer second = bank.getCustomerList().get(1);
    assertEquals(333333333L, second.getIdNumber());
    assertEquals(2, second.getAccountList().size());
  }

  @Test
  void readCustomerListCreatesCorrectAccountTypes() {
    bank.readCustomerList(streamOf(SAMPLE));

    Customer second = bank.getCustomerList().get(1);
    assertTrue(second.getAccountList().get(0) instanceof CheckingAccount);
    assertTrue(second.getAccountList().get(1) instanceof SavingsAccount);
  }

  @Test
  void infoByIdOrderSortsByIdNumber() {
    bank.readCustomerList(streamOf(SAMPLE));

    assertEquals(
        "Số CMND: 111111111. Họ tên: Trần Văn An.\n"
            + "Số CMND: 333333333. Họ tên: Lê Thị Bình.",
        bank.getCustomersInfoByIdOrder());
  }

  @Test
  void infoByNameOrderSortsByFullName() {
    bank.readCustomerList(streamOf(SAMPLE));

    assertEquals(
        "Số CMND: 333333333. Họ tên: Lê Thị Bình.\n"
            + "Số CMND: 111111111. Họ tên: Trần Văn An.",
        bank.getCustomersInfoByNameOrder());
  }

  @Test
  void nullInputStreamLeavesBankEmpty() {
    bank.readCustomerList(null);

    assertTrue(bank.getCustomerList().isEmpty());
  }

  @Test
  void malformedAccountNumberDoesNotCrashTheReader() {
    bank.readCustomerList(streamOf("Nguyen Test 123456789\nabc CHECKING 10\n"));

    assertEquals(1, bank.getCustomerList().size());
    assertTrue(bank.getCustomerList().get(0).getAccountList().isEmpty());
  }

  @Test
  void setCustomerListWithNullResetsToEmptyList() {
    bank.setCustomerList(null);

    assertTrue(bank.getCustomerList().isEmpty());
  }
}
