package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Kiểm thử luồng chạy của {@link Main} (không gọi {@code main} để tránh {@code System.exit}).
 */
class MainTest {
  private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  private final PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

  private String output() {
    return buffer.toString(StandardCharsets.UTF_8);
  }

  @Test
  void runWithoutArgumentsUsesSampleData() {
    int exitCode = Main.run(new String[0], out);

    assertEquals(0, exitCode);
    assertTrue(output().contains("Số CMND: 111111111. Họ tên: Trần Văn An."));
    assertTrue(output().contains("Lịch sử giao dịch của tài khoản 1001"));
  }

  @Test
  void runWithFileArgumentReadsThatFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("customers.txt");
    Files.writeString(file, "Phạm Văn Cường 333333333\n4001 CHECKING 50\n", StandardCharsets.UTF_8);

    int exitCode = Main.run(new String[] {file.toString()}, out);

    assertEquals(0, exitCode);
    assertTrue(output().contains("Số CMND: 333333333. Họ tên: Phạm Văn Cường."));
  }

  @Test
  void runWithMissingFileReturnsErrorCode(@TempDir Path tempDir) {
    String missing = tempDir.resolve("khong-ton-tai.txt").toString();

    int exitCode = Main.run(new String[] {missing}, out);

    assertEquals(1, exitCode);
    assertTrue(output().contains("Lỗi"));
  }
}
