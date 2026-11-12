package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Kiểm thử {@link BankFileService}: đường dẫn phải đúng trên cả Windows, Linux và macOS.
 *
 * <p>Test không ghi cứng dấu gạch chéo; chỉ dùng {@link Path} hoặc {@link File#separator}.
 */
class BankFileServiceTest {
  private static final String CUSTOMER_DATA =
      "Trần Văn An 111111111\n"
          + "1001 CHECKING 2500.75\n"
          + "Lê Thị Bình 222222222\n"
          + "2001 SAVINGS 9000\n";

  private static final String EXPECTED_REPORT =
      "Số CMND: 111111111. Họ tên: Trần Văn An.\n"
          + "Số CMND: 222222222. Họ tên: Lê Thị Bình.";

  private static Bank sampleBank() {
    Bank bank = new Bank();
    bank.getCustomerList().add(new Customer(222222222L, "Lê Thị Bình"));
    bank.getCustomerList().add(new Customer(111111111L, "Trần Văn An"));
    return bank;
  }

  @Test
  void buildPathUsesSeparatorOfCurrentOs() {
    assertEquals("data" + File.separator + "customers.txt",
        BankFileService.buildPath("data", "customers.txt"));
  }

  @Test
  void getFileNameReturnsLastPathElement() {
    String path = Paths.get("data", "bank", "customers.txt").toString();

    assertEquals("customers.txt", BankFileService.getFileName(path));
  }

  @Test
  void writeReportCreatesSubDirectoryUsingPath(@TempDir Path tempDir) throws IOException {
    String directory = tempDir.resolve("reports").toString();

    BankFileService.writeReport(sampleBank(), directory, "report.txt");

    Path expected = tempDir.resolve("reports").resolve("report.txt");
    assertTrue(Files.exists(expected));
    assertEquals(EXPECTED_REPORT, Files.readString(expected, StandardCharsets.UTF_8));
  }

  @Test
  void writeReportWorksWithFileSeparator(@TempDir Path tempDir) throws IOException {
    String directory = tempDir.toString() + File.separator + "reports" + File.separator + "2024";

    BankFileService.writeReport(sampleBank(), directory, "report.txt");

    Path expected = tempDir.resolve("reports").resolve("2024").resolve("report.txt");
    assertTrue(Files.exists(expected));
  }

  @Test
  void loadBankReadsFileFromSubDirectory(@TempDir Path tempDir) throws IOException {
    Path dataDirectory = Files.createDirectories(tempDir.resolve("data"));
    Files.writeString(
        dataDirectory.resolve("customers.txt"), CUSTOMER_DATA, StandardCharsets.UTF_8);

    Bank bank = BankFileService.loadBank(dataDirectory.toString(), "customers.txt");

    assertEquals(2, bank.getCustomerList().size());
    assertEquals(1, bank.getCustomerList().get(0).getAccountList().size());
  }

  @Test
  void loadBankThrowsIoExceptionWhenFileIsMissing(@TempDir Path tempDir) {
    String directory = tempDir.resolve("missing").toString();

    assertThrows(IOException.class, () -> BankFileService.loadBank(directory, "none.txt"));
  }
}
