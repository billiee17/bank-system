# Bài 3: CI/CD automation (GitHub Actions + Maven)

## 1. Hệ thống được chọn

Tôi dùng lại hệ thống **BankSystem** (Bài 2) và tổ chức thành một dự án Maven hoàn chỉnh:

- **Logging:** SLF4J 2.0.7 + Logback 1.4.11 (đã có từ Bài 2).
- **Unit Tests:** JUnit Jupiter 5.9.2, 25 test case cho 5 lớp.
- **Chất lượng code:** Checkstyle (Google Java Style) chạy ở pha `validate`.
- **CI:** workflow GitHub Actions tự động xác minh build mỗi khi có thay đổi.

BankSystem phù hợp vì có logic nghiệp vụ rõ ràng (hạn mức rút, số dư tối thiểu, đọc dữ liệu, sắp xếp),
dễ viết test và dễ cố tình gây lỗi để kiểm thử CI.

## 2. Cấu trúc dự án

```
bank-system-ci/
├── .github/workflows/ci.yml        ← quy trình CI
├── .gitignore
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/practice/bank/...      ← mã nguồn (SLF4J)
    ├── main/resources/logback.xml
    └── test/java/com/practice/bank/
        ├── TransactionTest.java     (3 test)
        ├── CheckingAccountTest.java (6 test)
        ├── SavingsAccountTest.java  (5 test)
        ├── CustomerTest.java        (4 test)
        └── BankTest.java            (7 test)
```

## 3. Workflow GitHub Actions (`.github/workflows/ci.yml`)

### 3.1. Điều kiện kích hoạt (trigger)

```yaml
on:
  push:
    branches: ["**"]          # push lên bất kỳ nhánh nào
  pull_request:
    branches: ["main"]        # mở hoặc cập nhật PR vào main
```

### 3.2. Các bước (job `build`, chạy trên `ubuntu-latest`)

| # | Bước | Mục đích |
|---|---|---|
| 1 | `actions/checkout@v4` | Lấy mã nguồn về runner |
| 2 | `actions/setup-java@v4` (Temurin 17, `cache: maven`) | Cài JDK 17 và cache thư viện Maven để build nhanh hơn |
| 3 | Show tool versions | In phiên bản Java và Maven, hữu ích khi gỡ lỗi |
| 4 | `mvn -B -e validate` | Pha **validate**: chạy Checkstyle |
| 5 | `mvn -B -e compile` | Pha **compile** |
| 6 | `mvn -B -e test` | Pha **test**: chạy 25 test JUnit 5 |
| 7 | `mvn -B -e package -DskipTests` | Pha **package**: tạo file `.jar` (test đã chạy ở bước 6 nên không chạy lại) |
| 8 | Verify the JAR | `ls -l`, `jar tf` (liệt kê nội dung) và đọc `MANIFEST.MF` để xác minh JAR |
| 9 | `actions/upload-artifact@v4` | Lưu `target/*.jar` thành artifact `bank-system-jar` |
| 10 | Upload reports on failure (`if: failure()`) | Chỉ khi có lỗi: lưu báo cáo Surefire và Checkstyle để tải về phân tích |

Tách từng pha thành một bước riêng giúp nhìn vào là biết pha nào hỏng. Các cờ Maven:
`-B` (batch mode, log gọn) và `-e` (in stack trace khi lỗi).
Ở bước 9, `if-no-files-found: error` làm bước này **thất bại** nếu không có file JAR nào,
nên artifact rỗng sẽ không bao giờ bị coi là thành công.

## 4. Cách chạy

### Chạy cục bộ

```bash
mvn clean verify
ls target/*.jar
```

### Đưa lên GitHub để kích hoạt CI

```bash
git init
git add .
git commit -m "Bai 3: Maven project + GitHub Actions CI"
git branch -M main
git remote add origin https://github.com/<tên-tài-khoản>/bank-system-ci.git
git push -u origin main
```

Sau khi push, vào tab **Actions** của repository để xem workflow chạy. Khi chạy xong, kéo xuống mục
**Artifacts** ở cuối trang của lần chạy để tải `bank-system-jar`.

## 5. Kiểm thử tự động hóa bằng cách cố tình gây lỗi

### Kịch bản A: làm hỏng một unit test

```bash
git checkout -b broken-build
```

Trong `SavingsAccountTest.withdrawWithinLimitSucceeds`, đổi giá trị mong đợi `8500.0` thành `9999.0`:

```java
assertEquals(9999.0, account.getBalance(), DELTA);   // đúng phải là 8500.0
```

```bash
git commit -am "Cố tình làm hỏng test"
git push -u origin broken-build
```

Mở pull request vào `main`. Workflow chạy và **thất bại ở bước "Phase: test"**. Quy trình gỡ lỗi từ log:

1. Vào **Actions**, chọn lần chạy có dấu ✗ đỏ. Bước hỏng được đánh dấu đỏ, các bước sau (package, upload JAR) bị bỏ qua.
2. Mở bước **"Phase: test (JUnit 5)"** và tìm dòng `[ERROR]`. Log của Surefire chỉ rõ tên test hỏng,
   dòng code và giá trị lệch, dạng `expected: <9999.0> but was: <8500.0>`.
3. Nhờ `trimStackTrace=false`, stack trace được in đầy đủ.
4. Log của chính ứng dụng (Logback) cũng nằm trong cùng bước, ví dụ các dòng `INFO Giao dịch thành công: ... số dư sau=8500.0`.
   Dòng này cho biết giá trị thực của số dư, giúp xác định đúng là test sai chứ không phải code sai.
5. Mục **Artifacts** có thêm `failure-reports` (từ bước `if: failure()`): tải về để đọc báo cáo Surefire dạng XML/TXT.
6. Sửa lại `8500.0`, commit và push. Workflow chạy lại và chuyển sang xanh.

### Kịch bản B: vi phạm Checkstyle (cổng đầu tiên của pipeline)

Thêm một dòng thụt lề 4 khoảng trắng (thay vì 2) vào một file Java rồi push. Workflow thất bại ngay ở bước
**"Phase: validate (Checkstyle)"**, chưa kịp compile hay test. Log liệt kê file, dòng và tên luật vi phạm,
ví dụ `[Indentation]`.

### Các công cụ gỡ lỗi khi log chưa đủ

| Công cụ | Cách dùng |
|---|---|
| Re-run với debug logging | Ở trang lần chạy, chọn **Re-run jobs**, rồi **Enable debug logging**. Log có thêm các dòng `##[debug]` về runner và biến môi trường |
| Tìm kiếm trong log | Dùng ô tìm kiếm của trang log với từ khóa `ERROR` hoặc `FAIL` |
| Maven debug | Đổi tạm lệnh thành `mvn -B -X test` để xem classpath, plugin và cấu hình chi tiết |
| Chạy lại một test cục bộ | `mvn -B -e test -Dtest=SavingsAccountTest` |
| Tải báo cáo | Artifact `failure-reports` chứa `target/surefire-reports/` và `checkstyle-result.xml` |

## 6. Kết quả

(Dán ảnh chụp màn hình vào các mục sau.)

| Ảnh | Nội dung |
|---|---|
| 1 | Lần chạy thành công (dấu ✓ xanh), danh sách các bước |
| 2 | Mục **Artifacts** có `bank-system-jar`, và bước "Verify the JAR" hiện nội dung JAR |
| 3 | Lần chạy thất bại ở Kịch bản A (dấu ✗ đỏ) |
| 4 | Log của bước "Phase: test" hiển thị `[ERROR]` và `expected: <...> but was: <...>` |
| 5 | Lần chạy xanh trở lại sau khi sửa lỗi |
