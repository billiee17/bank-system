# Bài 9: Triển khai Logging chuyên nghiệp

Dự án: **BankSystem** (Java 17, Maven, SLF4J 2.0.7 + Logback 1.4.11).

## 1\. Đối chiếu với yêu cầu

|Yêu cầu|Cách thực hiện|Cách kiểm tra|
|-|-|-|
|Loại bỏ mọi `System.out.println()`|Thay bằng `LOGGER.xxx(...)` ở tất cả lớp trong `src/main`|`grep -rn "System.out.println\|printStackTrace" src` → không có kết quả|
|SLF4J + Logback, mức log phù hợp|`INFO` cho mốc quan trọng, `ERROR` cho ngoại lệ (xem bảng 2)|Đọc log|
|Parameterized logging `{}`|Mọi lệnh log dùng `{}`, không cộng chuỗi|`grep -rnE "LOGGER\\.\[a-z]+\\(.\*\\+" src/main` → không có kết quả|
|`FileAppender` trong `logback.xml`|Appender `FILE` ghi vào `logs/bank-system.log`|`LoggingTest` + xem tệp sau `mvn test`|
|`mvn test` → log đúng định dạng ở console và tệp|Cùng một `LOG\_PATTERN` cho cả hai appender|Mục 4|

> Về `Main`: lớp này in \*\*kết quả của chương trình\*\* (danh sách khách hàng) ra luồng `out` do người dùng truyền vào
> (mặc định là console). Đó là đầu ra nghiệp vụ, không phải thông tin chẩn đoán; mọi thông tin chẩn đoán đều đi qua logger.

## 2\. Mức log đã dùng

|Mức|Dùng khi|Ví dụ trong code|
|-|-|-|
|`ERROR`|Ngoại lệ làm hỏng một thao tác, cần xử lý|`Bank`: lỗi đọc dữ liệu; `Main`: không đọc được tệp (kèm stack trace)|
|`WARN`|Tình huống bất thường nhưng chương trình vẫn chạy|Giao dịch bị từ chối, dữ liệu đầu vào `null`, loại tài khoản không hợp lệ|
|`INFO`|Mốc quan trọng của luồng nghiệp vụ|Khởi động/kết thúc, giao dịch thành công, đọc/ghi tệp xong|
|`DEBUG`|Chi tiết để dò lỗi|Thêm khách hàng thứ n, đang xử lý nạp tiền|
|`TRACE`|Rất chi tiết|Tạo tóm tắt giao dịch|

Không log dữ liệu nhạy cảm (họ tên, số CMND) – chỉ log số tài khoản, số tiền, số lượng.

## 3\. Parameterized logging

```java
// Tránh: luôn tạo chuỗi dù mức DEBUG đang tắt
LOGGER.debug("Đã thêm khách hàng thứ " + customers.size());

// Dùng: chuỗi chỉ được ghép khi mức log thật sự được bật
LOGGER.debug("Đã thêm khách hàng thứ {}", customers.size());

// Ngoại lệ: truyền exception làm tham số CUỐI để có stack trace
LOGGER.error("Lỗi khi đọc dữ liệu khách hàng", e);
```

Cộng chuỗi tốn thời gian ghép chuỗi và cấp phát bộ nhớ ngay cả khi dòng log bị lọc bỏ; `{}` trì hoãn việc đó
đến khi chắc chắn dòng log sẽ được ghi.

## 4\. `logback.xml` (`src/main/resources`)

* `CONSOLE` (`ConsoleAppender`) và `FILE` (`FileAppender`, `logs/bank-system.log`, `append=true`) dùng chung `LOG\_PATTERN`:

```
%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level \[%thread] %logger{20} - %msg%n
```

* Ví dụ một dòng log:

```
2026-11-12 22:05:31.123 INFO  \[main] c.p.bank.Main - BankSystem kết thúc bình thường
```

(thời gian, mức log, luồng, logger, nội dung)

* Logger `com.practice.bank` ở mức `DEBUG`, `root` ở mức `INFO`.
* Đổi thư mục log bằng `-DLOG\_DIR=/duong/dan`. Thư mục `logs/` đã nằm trong `.gitignore`.
* Mở rộng: nếu log tăng nhiều, đổi sang `RollingFileAppender` để xoay vòng theo ngày/dung lượng.

## 5\. Xác minh

```bash
mvn test
cat logs/bank-system.log         # Linux/macOS/Git Bash
type logs\\bank-system.log        # cmd
Get-Content logs\\bank-system.log # PowerShell
```

`LoggingTest` tự động kiểm tra: (1) tệp `logs/bank-system.log` tồn tại, (2) có dòng `INFO` đúng định dạng
`yyyy-MM-dd HH:mm:ss.SSS LEVEL \[thread] logger - msg`, (3) có dòng `ERROR` khi truyền tệp không tồn tại.

Minh chứng cần chụp:

1. Terminal `mvn test` hiện các dòng log ở console (có thời gian + mức log). `\[ẢNH 1]`
2. Nội dung `logs/bank-system.log` với cùng định dạng. `\[ẢNH 2]`
3. Kết quả `grep` không còn `System.out.println`. `\[ẢNH 3]`
4. `BUILD SUCCESS` với `Tests run: 46`. `\[ẢNH 4]`



.

