# Bài 5: Test Coverage & Quality Enforcement (JaCoCo)

## 1. Vấn đề

Có unit test chưa chắc đã đủ: test có thể chỉ chạy qua một phần nhỏ của code. **Độ bao phủ (code coverage)**
cho biết bao nhiêu phần trăm code thực sự được test chạy tới. Bài này dùng **JaCoCo** để:

1. Tạo báo cáo độ bao phủ.
2. **Ép buộc** ngưỡng tối thiểu 80%: dưới ngưỡng thì build thất bại.
3. Chạy bước kiểm tra đó trong GitHub Actions (pha `mvn verify`) và lưu báo cáo bằng `upload-artifact`.

Dự án vẫn là BankSystem (Bài 2 đến 4): Maven, SLF4J/Logback, Checkstyle, JUnit 5, workflow Matrix ba hệ điều hành.

## 2. Cấu trúc dự án

```
bank-system-matrix/
├── .github/workflows/ci.yml      ← workflow: Matrix + mvn verify + upload báo cáo JaCoCo
├── .gitattributes  .gitignore
├── pom.xml                       ← có jacoco-maven-plugin
├── README.md
├── demo/                         ← file dùng để thử nghiệm (Maven không biên dịch)
│   ├── BankFileService.java.broken        (Bài 4: bản lỗi đường dẫn)
│   ├── BankFileServiceTest.java.broken
│   ├── InterestCalculator.java.demo       (Bài 5: tính năng mới CHƯA có test)
│   └── InterestCalculatorTest.java.demo   (Bài 5: test bổ sung để sửa)
└── src/
    ├── main/java/com/practice/bank/...
    └── test/java/com/practice/bank/...    ← 41 test (thêm AccountTest và các test bổ sung để tăng coverage)
```

## 3. Cấu hình JaCoCo trong `pom.xml`

Trong `pom.xml`, `jacoco-maven-plugin` 0.8.11 có ba `execution`:

| Execution | Goal | Pha | Tác dụng |
|---|---|---|---|
| `jacoco-prepare-agent` | `prepare-agent` | `initialize` (mặc định) | Gắn JaCoCo agent vào JVM chạy test; ghi dữ liệu vào `target/jacoco.exec` |
| `jacoco-report` | `report` | `test` | Tạo báo cáo HTML tại `target/site/jacoco/index.html` (kèm `jacoco.csv`, `jacoco.xml`) |
| `jacoco-check` | `check` | `verify` | Áp dụng quy tắc nghiêm ngặt; vi phạm thì build **FAIL** |

Quy tắc nghiêm ngặt:

```xml
<rule>
  <element>BUNDLE</element>              <!-- toàn bộ dự án -->
  <limits>
    <limit>
      <counter>LINE</counter>            <!-- đếm theo dòng code -->
      <value>COVEREDRATIO</value>
      <minimum>0.80</minimum>            <!-- tối thiểu 80% -->
    </limit>
  </limits>
</rule>
```

- `BUNDLE` là phạm vi toàn dự án; có thể đổi thành `PACKAGE` hoặc `CLASS` để áp dụng ngưỡng cho từng gói hoặc lớp.
- `LINE` là độ bao phủ theo **dòng code**. Ngoài ra còn `INSTRUCTION`, `BRANCH`, `METHOD`, `CLASS`.
- Ngưỡng được đặt thành property `jacoco.line.minimum`, nên có thể đổi nhanh khi chạy thử:
  `mvn verify -Djacoco.line.minimum=0.99`.
- Báo cáo được gắn vào pha `test` (không phải `verify`) để **luôn có báo cáo ngay cả khi bước `check` thất bại**;
  lúc đó mới cần xem báo cáo để biết dòng nào chưa được test.

## 4. Độ bao phủ hiện tại

Chạy `mvn clean verify`, mở `target/site/jacoco/index.html`. Kết quả đo được với 41 test
(JaCoCo 0.8.11, độ bao phủ **dòng**):

| Lớp | Độ bao phủ dòng |
|---|---|
| Account | 95.7% |
| Bank | 98.3% |
| BankFileService | 100% |
| CheckingAccount | 100% |
| Customer | 100% |
| SavingsAccount | 88.5% |
| Transaction | 100% |
| Các lớp exception | 100% |
| **Toàn dự án** | **97.4% (226/232 dòng)**, vượt ngưỡng 80% |

Lần đo đầu tiên (31 test, trước khi bổ sung) là 84.5%. Báo cáo JaCoCo cho thấy các hàm `set...` của `Account`,
`Customer`, `Transaction` chưa được test. Tôi viết thêm test cho chúng (`AccountTest` và các test bổ sung) để có biên an toàn.

## 5. Workflow GitHub Actions

Các thay đổi chính trong `.github/workflows/ci.yml` so với Bài 4:

| Bước | Nội dung |
|---|---|
| `Phase: verify (test + package + JaCoCo coverage check)` | `mvn -B -e verify`: chạy test, tạo báo cáo, đóng gói JAR, rồi **kiểm tra ngưỡng coverage**. Dưới 80% thì bước này đỏ và cả workflow thất bại |
| `Coverage summary` (`if: always()`) | Đọc `jacoco.csv` và in phần trăm bao phủ dòng ra trang tóm tắt của lần chạy (`$GITHUB_STEP_SUMMARY`) |
| `Upload JaCoCo coverage report` (`if: always()`) | `upload-artifact` lưu `target/site/jacoco/` thành artifact `jacoco-report-<os>` |

`if: always()` rất quan trọng: mặc định các bước sau một bước thất bại sẽ bị bỏ qua. Nếu thiếu nó, khi build
fail vì coverage thấp (đúng lúc cần báo cáo nhất) thì báo cáo sẽ không được lưu.

**Xem báo cáo sau khi build xong:** vào tab **Actions**, chọn lần chạy, kéo xuống mục **Artifacts**,
tải `jacoco-report-ubuntu-latest` (file `.zip`), giải nén rồi mở `index.html` bằng trình duyệt.
Trong báo cáo: dòng **xanh** là đã được chạy, **đỏ** là chưa, **vàng** là nhánh `if` chỉ chạy một phần.

## 6. Thí nghiệm: làm độ bao phủ tụt dưới 80%

Tình huống thực tế: lập trình viên thêm một tính năng mới nhưng quên viết test.

### Bước 1: Thêm tính năng chưa có test

```bash
git checkout -b demo/low-coverage
cp demo/InterestCalculator.java.demo src/main/java/com/practice/bank/InterestCalculator.java
git add -A
git commit -m "Them InterestCalculator (chua co test)"
git push -u origin demo/low-coverage
```

(PowerShell: dùng `Copy-Item` thay cho `cp`.)

`InterestCalculator` có khoảng 60 dòng code mới và không có test nào chạy tới chúng.

### Kết quả quan sát

- Tất cả test cũ vẫn xanh, nhưng bước **`Phase: verify`** thất bại với thông báo dạng:
  `Rule violated for bundle bank-system: lines covered ratio is 0.77, but expected minimum is 0.80`
  và `BUILD FAILURE` (plugin `jacoco-maven-plugin:0.8.11:check`).
- Trang tóm tắt hiện độ bao phủ khoảng **77.1%** (226/293 dòng).
- Nhờ `if: always()`, artifact `jacoco-report-...` vẫn được lưu. Mở báo cáo, vào lớp `InterestCalculator`:
  toàn bộ dòng màu đỏ, cho biết chính xác chỗ cần viết test.

### Bước 2: Sửa bằng cách viết test

```bash
cp demo/InterestCalculatorTest.java.demo src/test/java/com/practice/bank/InterestCalculatorTest.java
git add -A
git commit -m "Them test cho InterestCalculator"
git push
```

Độ bao phủ trở lại khoảng **97.9%** và workflow xanh.

### Thử nhanh cục bộ (không cần đẩy lên GitHub)

```bash
mvn clean verify                               # đạt, báo cáo ở target/site/jacoco/index.html
mvn clean verify -Djacoco.line.minimum=0.99    # hạ ngưỡng lên 99% để thấy build fail
```

## 7. Kết quả

(Dán ảnh chụp màn hình vào các mục sau.)

| Ảnh | Nội dung |
|---|---|
| 1 | Lần chạy xanh với bản đầy đủ, trang tóm tắt hiện độ bao phủ dòng |
| 2 | Mục Artifacts có `jacoco-report-<os>` |
| 3 | Báo cáo `index.html` mở trên trình duyệt (bảng theo lớp) |
| 4 | Lần chạy đỏ ở nhánh `demo/low-coverage`, log bước verify có `Rule violated for bundle ...` |
| 5 | Trang lớp `InterestCalculator` trong báo cáo với các dòng đỏ |
| 6 | Lần chạy xanh trở lại sau khi thêm test |
