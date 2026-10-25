# Kết quả kiểm tra ngày 25/10/2026

## Phạm vi

Kiểm tra mã Java có sẵn tại máy; mã đó chưa nằm trong PR tài liệu chuẩn hóa.
Kết quả này không phải kiểm tra một bản clone từ GitHub và không thay thế ảnh chụp
màn hình bắt buộc trong tài liệu Word.

## Kết quả thực tế

- Biên dịch toàn bộ **30 file Java** ở `src/main/java` và `src/test/java` bằng
  JDK 22 `javac --release 17 -encoding UTF-8`: thành công.
- Chạy `com.nhom14.filesync.test.TestRunner` với classpath gồm các lớp vừa biên dịch
  và `src/main/resources`: **8/8**, process exit **0**.
- HTTP dashboard trong lần chạy trả **200**; kiểm thử dùng cổng TCP **9095** và HTTP **8088**.
- Chạy trong thư mục tạm riêng để thao tác tạo/xóa `test_env` không đụng dữ liệu đồ án.
- `git diff --check`: không báo lỗi.
- `git check-ignore` xác nhận chặn target, client_data/server_data, .idea/.vscode,
  JAR/ZIP và keystore/truststore.
- GitHub API xác nhận `main` cần 1 approval, áp dụng cho admin, hủy approval cũ khi
  có commit mới, yêu cầu giải quyết hội thoại; force push và xóa nhánh bị cấm.

## Giới hạn của kết quả

- Maven chưa có trên PATH; chưa chạy `mvn` để xác minh vòng đời Maven.
- Test resume hiện chỉ kiểm tra offset dựng sẵn; chưa chứng minh ngắt mạng và nối lại.
- Test xóa chỉ chứng minh trường hợp client xóa rồi server xóa theo; chưa đủ mọi chiều.
- Test khóa chỉ kiểm tra đọc đồng thời; chưa chứng minh đầy đủ tranh chấp đọc/ghi.
- TLS handshake thành công không chứng minh xác thực chứng chỉ, vì client đang dùng
  trust manager chấp nhận mọi chứng chỉ.
- Chưa kiểm tra thao tác GUI bằng ảnh; chưa có người khác nhận xét và Approve.
- TestRunner có câu báo “100% thành công” cố định và cách đếm cần rà soát trong N05;
  lần chạy này đối chiếu thực tế đủ 8 test, 8 pass và exit 0.

Các thiếu hụt đã được đưa vào backlog. Không sửa mã nghiệp vụ trong Issue #1.
