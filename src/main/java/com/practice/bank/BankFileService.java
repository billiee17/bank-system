package com.practice.bank;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Đọc dữ liệu khách hàng từ tệp và ghi báo cáo ra tệp.
 *
 * <p>Mọi đường dẫn đều được xử lý bằng {@link Path}, nên không phụ thuộc vào dấu phân cách của
 * hệ điều hành (Windows dùng dấu gạch chéo ngược, Linux và macOS dùng dấu gạch chéo xuôi).
 */
public final class BankFileService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BankFileService.class);

  private BankFileService() {
    // Lớp tiện ích, không tạo đối tượng.
  }

  /**
   * Ghép thư mục và tên tệp thành đường dẫn đúng với hệ điều hành đang chạy.
   *
   * @param directory thư mục chứa tệp
   * @param fileName tên tệp
   * @return đường dẫn đầy đủ, dùng dấu phân cách của hệ điều hành hiện tại
   */
  public static String buildPath(String directory, String fileName) {
    return Paths.get(directory, fileName).toString();
  }

  /**
   * Lấy tên tệp (phần tử cuối) từ một đường dẫn.
   *
   * @param path đường dẫn tệp
   * @return tên tệp, hoặc chuỗi rỗng nếu đường dẫn không có tên tệp
   */
  public static String getFileName(String path) {
    Path fileName = Paths.get(path).getFileName();
    return fileName == null ? "" : fileName.toString();
  }

  /**
   * Đọc danh sách khách hàng từ một tệp văn bản.
   *
   * @param directory thư mục chứa tệp
   * @param fileName tên tệp
   * @return ngân hàng chứa các khách hàng đã đọc
   * @throws IOException nếu không mở hoặc không đọc được tệp
   */
  public static Bank loadBank(String directory, String fileName) throws IOException {
    Path file = Paths.get(directory, fileName);
    LOGGER.info("Đọc dữ liệu khách hàng từ tệp {}", file);
    Bank bank = new Bank();
    try (InputStream inputStream = Files.newInputStream(file)) {
      bank.readCustomerList(inputStream);
    }
    return bank;
  }

  /**
   * Ghi báo cáo thông tin khách hàng (sắp xếp theo CMND) ra tệp, tạo thư mục cha nếu chưa có.
   *
   * @param bank ngân hàng cần lập báo cáo
   * @param directory thư mục chứa tệp báo cáo
   * @param fileName tên tệp báo cáo
   * @return đường dẫn tới tệp báo cáo đã ghi
   * @throws IOException nếu không tạo được thư mục hoặc không ghi được tệp
   */
  public static Path writeReport(Bank bank, String directory, String fileName)
      throws IOException {
    Path file = Paths.get(directory, fileName);
    Path parent = file.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Files.writeString(file, bank.getCustomersInfoByIdOrder(), StandardCharsets.UTF_8);
    LOGGER.info("Đã ghi báo cáo khách hàng vào tệp {}", file);
    return file;
  }
}
