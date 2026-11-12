package com.practice.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tính lãi suất tài khoản. Đây là tính năng MỚI và CHƯA có unit test (dùng để thử nghiệm JaCoCo).
 */
public final class InterestCalculator {
  private static final Logger LOGGER = LoggerFactory.getLogger(InterestCalculator.class);

  private static final double LOW_TIER_RATE = 0.01;
  private static final double MID_TIER_RATE = 0.02;
  private static final double HIGH_TIER_RATE = 0.035;
  private static final double MID_TIER_LIMIT = 10_000.0;
  private static final double HIGH_TIER_LIMIT = 100_000.0;

  private InterestCalculator() {
    // Lớp tiện ích, không tạo đối tượng.
  }

  /**
   * Trả về lãi suất năm theo bậc số dư.
   *
   * @param balance số dư tài khoản
   * @return lãi suất năm
   */
  public static double annualRateFor(double balance) {
    if (balance < 0) {
      throw new IllegalArgumentException("Số dư không được âm");
    }
    if (balance >= HIGH_TIER_LIMIT) {
      return HIGH_TIER_RATE;
    }
    if (balance >= MID_TIER_LIMIT) {
      return MID_TIER_RATE;
    }
    return LOW_TIER_RATE;
  }

  /**
   * Tính lãi đơn.
   *
   * @param principal tiền gốc
   * @param rate lãi suất năm
   * @param years số năm
   * @return tiền lãi
   */
  public static double simpleInterest(double principal, double rate, int years) {
    validate(principal, rate, years);
    double interest = principal * rate * years;
    LOGGER.debug("Lãi đơn: gốc={}, lãi suất={}, số năm={}", principal, rate, years);
    return interest;
  }

  /**
   * Tính lãi kép.
   *
   * @param principal tiền gốc
   * @param rate lãi suất năm
   * @param years số năm
   * @return tiền lãi
   */
  public static double compoundInterest(double principal, double rate, int years) {
    validate(principal, rate, years);
    double amount = principal;
    for (int year = 1; year <= years; year++) {
      amount += amount * rate;
      LOGGER.trace("Năm {}: {}", year, amount);
    }
    return amount - principal;
  }

  /**
   * Dự phóng số dư sau một số năm, lãi suất thay đổi theo bậc số dư.
   *
   * @param balance số dư ban đầu
   * @param years số năm
   * @return số dư dự phóng
   */
  public static double projectBalance(double balance, int years) {
    double projected = balance;
    for (int year = 1; year <= years; year++) {
      projected += projected * annualRateFor(projected);
    }
    return projected;
  }

  /**
   * Lập bảng dự phóng từng năm.
   *
   * @param balance số dư ban đầu
   * @param years số năm
   * @return mỗi phần tử là một dòng mô tả một năm
   */
  public static List<String> schedule(double balance, int years) {
    List<String> lines = new ArrayList<>();
    double projected = balance;
    for (int year = 1; year <= years; year++) {
      double rate = annualRateFor(projected);
      double interest = projected * rate;
      projected += interest;
      lines.add(String.format(Locale.US, "Năm %d: lãi suất %.1f%%, lãi $%.2f, số dư $%.2f",
          year, rate * 100, interest, projected));
    }
    return lines;
  }

  /**
   * Mô tả ngắn gọn kết quả dự phóng.
   *
   * @param balance số dư ban đầu
   * @param years số năm
   * @return chuỗi mô tả
   */
  public static String describe(double balance, int years) {
    double rate = annualRateFor(balance);
    double future = projectBalance(balance, years);
    if (future <= balance) {
      return "Không có lãi sau " + years + " năm";
    }
    return String.format(Locale.US, "Sau %d năm (%s): $%.2f, lãi $%.2f",
        years, bracketName(rate), future, future - balance);
  }

  /**
   * Tính phí rút tiền trước hạn.
   *
   * @param amount số tiền rút
   * @param monthsHeld số tháng đã gửi
   * @return phí phải trả
   */
  public static double earlyWithdrawalPenalty(double amount, int monthsHeld) {
    if (amount < 0 || monthsHeld < 0) {
      throw new IllegalArgumentException("Tham số không được âm");
    }
    if (monthsHeld < 6) {
      return amount * 0.03;
    }
    if (monthsHeld < 12) {
      return amount * 0.015;
    }
    return 0.0;
  }

  /**
   * Tính lợi suất hiệu dụng hằng năm.
   *
   * @param principal tiền gốc
   * @param finalAmount số tiền nhận được cuối kỳ
   * @param years số năm
   * @return lợi suất hiệu dụng hằng năm
   */
  public static double effectiveYield(double principal, double finalAmount, int years) {
    validate(principal, 0.0, years);
    if (principal == 0 || years == 0) {
      return 0.0;
    }
    double growth = finalAmount / principal;
    return Math.pow(growth, 1.0 / years) - 1.0;
  }

  private static String bracketName(double rate) {
    if (rate >= HIGH_TIER_RATE) {
      return "bậc cao";
    }
    if (rate >= MID_TIER_RATE) {
      return "bậc giữa";
    }
    return "bậc thấp";
  }

  private static void validate(double principal, double rate, int years) {
    if (principal < 0) {
      throw new IllegalArgumentException("Tiền gốc không được âm");
    }
    if (rate < 0) {
      throw new IllegalArgumentException("Lãi suất không được âm");
    }
    if (years < 0) {
      throw new IllegalArgumentException("Số năm không được âm");
    }
  }
}
