# Đồng bộ tệp tin Client–Server — Nhóm 14

Học phần Lập trình mạng, lớp **23DTHB1**.
Nhóm trưởng: **Trần Quốc Nguyễn — 2380601506**.

## Trạng thái thực tế

Repository đang chuẩn hóa quy trình Git. Mã Java đã có ở máy cục bộ trước Issue #1;
PR chuẩn hóa chỉ đưa tài liệu và cấu hình quản lý vào Git. Các module Java chưa được
tích hợp trên `main`. Không xem kế hoạch bên dưới là bằng chứng đã hoàn thành đóng góp.

## Ma trận đóng góp

Chỉ bổ sung công việc **đã thực hiện** khi có bằng chứng và PR thật. Phân công trong
báo cáo cũ là đầu vào lập kế hoạch, không thay thế lịch sử Git hay review.

| MSSV | Họ và tên | Công việc đã thực hiện | Merge Requests / Pull Requests |
|---|---|---|---|
| 2380601506 | Trần Quốc Nguyễn | Đang chuẩn hóa quy trình và phân chia module theo Issue #1; chưa merge | Chưa có PR được merge |
| 2380600941 | Bùi Nhật Quỳnh Hương | Chưa đối chiếu bằng PR | Chưa có |
| 2380602014 | Lý Quốc Thái | Chưa đối chiếu bằng PR | Chưa có |
| 2380602385 | Nguyễn Trung Trực | Chưa đối chiếu bằng PR | Chưa có |

## Tài liệu làm việc

- [Phân chia mã nguồn, phụ thuộc và quyền sửa](docs/PHAN_CHIA_MODULE.md)
- [Danh sách đầu việc và tiêu chí nghiệm thu](docs/KE_HOACH_ISSUE.md)
- [Quy trình Git và những điểm chưa đáp ứng](docs/QUY_TRINH_GIT.md)

## Công nghệ và chạy bản mã nguồn cục bộ

Java 17+, Maven, Socket TCP, Swing, TLS, chia khối và SHA-256.
Khi các module và chứng chỉ môi trường lab đã được tích hợp đầy đủ:

```powershell
mvn compile test-compile
java -cp "target/classes;target/test-classes" com.nhom14.filesync.test.TestRunner
mvn exec:java
```

`TestRunner` có hàm `main`; chỉ chạy `mvn test` không chứng minh bộ kiểm thử này đã chạy.
Test tạo/xóa `test_env` trong thư mục làm việc: chỉ chạy ở thư mục thử nghiệm riêng.
Không commit dữ liệu người dùng, `target`, file JAR hoặc khóa TLS. Hướng dẫn tái tạo
chứng chỉ phải được hoàn thành trong Issue bảo mật trước khi nghiệm thu bản clone sạch.
