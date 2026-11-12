package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử {@link Customer}.
 */
class CustomerTest {
  @Test
  void addAccountIgnoresDuplicateAccountNumber() {
    Customer customer = new Customer(111111111L, "Trần Văn An");

    customer.addAccount(new CheckingAccount(1001L, 10.0));
    customer.addAccount(new SavingsAccount(1001L, 20.0));

    assertEquals(1, customer.getAccountList().size());
  }

  @Test
  void addAndRemoveIgnoreNull() {
    Customer customer = new Customer();

    customer.addAccount(null);
    customer.removeAccount(null);

    assertTrue(customer.getAccountList().isEmpty());
  }

  @Test
  void removeAccountDeletesIt() {
    Customer customer = new Customer(111111111L, "Trần Văn An");
    Account account = new CheckingAccount(1001L, 10.0);
    customer.addAccount(account);

    customer.removeAccount(account);

    assertTrue(customer.getAccountList().isEmpty());
  }

  @Test
  void settersUpdateIdAndName() {
    Customer customer = new Customer();

    customer.setIdNumber(222222222L);
    customer.setFullName("Lê Thị Bình");

    assertEquals(222222222L, customer.getIdNumber());
    assertEquals("Lê Thị Bình", customer.getFullName());
  }

  @Test
  void setAccountListWithNullResetsToEmptyList() {
    Customer customer = new Customer(111111111L, "Trần Văn An");
    customer.addAccount(new CheckingAccount(1001L, 10.0));

    customer.setAccountList(null);

    assertTrue(customer.getAccountList().isEmpty());
  }

  @Test
  void setAccountListReplacesTheList() {
    Customer customer = new Customer();
    List<Account> replacement = new ArrayList<>();
    replacement.add(new SavingsAccount(2001L, 9000.0));

    customer.setAccountList(replacement);

    assertEquals(1, customer.getAccountList().size());
  }

  @Test
  void customerInfoContainsIdAndName() {
    Customer customer = new Customer(111111111L, "Trần Văn An");

    assertEquals("Số CMND: 111111111. Họ tên: Trần Văn An.", customer.getCustomerInfo());
  }
}
