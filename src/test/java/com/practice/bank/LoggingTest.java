package com.practice.bank;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Xác nhận cấu hình logging: dòng log có thời gian + mức log và được ghi vào tệp vật lý.
 */
class LoggingTest {
  /** Dạng: 2026-11-12 22:05:31.123 INFO  [main] c.p.bank.Main - nội dung. */
  private static final Pattern LOG_LINE =
      Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} "
          + "(TRACE|DEBUG|INFO |WARN |ERROR) \\[.+\\] \\S+ - .+$");

  private static final Path LOG_FILE = Paths.get("logs", "bank-system.log");

  @Test
  void logFileIsWrittenWithTimestampAndLevel() throws IOException {
    PrintStream out = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);

    Main.run(new String[0], out);

    assertTrue(Files.exists(LOG_FILE), "Phải có tệp log: " + LOG_FILE.toAbsolutePath());
    List<String> lines = Files.readAllLines(LOG_FILE, StandardCharsets.UTF_8);
    boolean hasInfoMilestone =
        lines.stream().anyMatch(l -> LOG_LINE.matcher(l).matches()
            && l.contains(" INFO  ") && l.contains("BankSystem kết thúc bình thường"));
    assertTrue(hasInfoMilestone, "Thiếu dòng INFO đúng định dạng trong tệp log");
  }

  @Test
  void errorLevelIsWrittenWhenInputFileIsMissing() throws IOException {
    PrintStream out = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);

    Main.run(new String[] {"khong-ton-tai-" + System.nanoTime() + ".txt"}, out);

    List<String> lines = Files.readAllLines(LOG_FILE, StandardCharsets.UTF_8);
    boolean hasError =
        lines.stream().anyMatch(l -> LOG_LINE.matcher(l).matches() && l.contains(" ERROR "));
    assertTrue(hasError, "Thiếu dòng ERROR đúng định dạng trong tệp log");
  }
}
