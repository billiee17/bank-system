package com.practice.bank;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Điểm khởi chạy của ứng dụng BankSystem; đây là lớp được ghi trong {@code Main-Class} của
 * {@code MANIFEST.MF}.
 *
 * <p>Cách dùng: {@code java -jar bank-system-1.0-SNAPSHOT.jar [tệp-dữ-liệu-khách-hàng]}. Nếu không
 * truyền tham số, ứng dụng dùng dữ liệu mẫu có sẵn.
 */
public final class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String SAMPLE_DATA =
      "Trần Văn An 111111111\n"
          + "1001 CHECKING 2500.75\n"
          + "2001 SAVINGS 9000\n"
          + "Lê Thị Bình 222222222\n"
          + "3001 CHECKING 150.00\n";

  private Main() {
    // Lớp tiện ích, không tạo đối tượng.
  }

  /**
   * Hàm main: JVM gọi hàm này khi chạy {@code java -jar}.
   *
   * @param args tham số dòng lệnh; phần tử đầu (nếu có) là đường dẫn tệp dữ liệu khách hàng
   */
  public static void main(String[] args) {
    int exitCode = run(args, System.out);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }

  /**
   * Chạy ứng dụng và ghi kết quả ra luồng {@code out}.
   *
   * @param args tham số dòng lệnh
   * @param out nơi ghi kết quả cho người dùng
   * @return mã thoát: 0 nếu thành công, 1 nếu không đọc được dữ liệu
   */
  static int run(String[] args, PrintStream out) {
    LOGGER.info("BankSystem khởi động, số tham số: {}", args.length);
    Bank bank;
    try {
      bank = loadBank(args);
    } catch (IOException e) {
      LOGGER.error("Không đọc được dữ liệu khách hàng", e);
      out.println("Lỗi: không đọc được dữ liệu khách hàng: " + e.getMessage());
      return 1;
    }
    out.println("=== Danh sách khách hàng (theo CMND) ===");
    out.println(bank.getCustomersInfoByIdOrder());
    out.println("=== Giao dịch mẫu: nạp 100, rút 50 ===");
    runSampleTransactions(bank, out);
    LOGGER.info("BankSystem kết thúc bình thường");
    return 0;
  }

  private static Bank loadBank(String[] args) throws IOException {
    if (args.length == 0) {
      Bank bank = new Bank();
      byte[] data = SAMPLE_DATA.getBytes(StandardCharsets.UTF_8);
      bank.readCustomerList(new ByteArrayInputStream(data));
      return bank;
    }
    Path file = Paths.get(args[0]).toAbsolutePath();
    return BankFileService.loadBank(file.getParent().toString(), file.getFileName().toString());
  }

  private static void runSampleTransactions(Bank bank, PrintStream out) {
    for (Customer customer : bank.getCustomerList()) {
      for (Account account : customer.getAccountList()) {
        account.deposit(100.0);
        account.withdraw(50.0);
        out.println(account.getTransactionHistory());
      }
    }
  }
}
