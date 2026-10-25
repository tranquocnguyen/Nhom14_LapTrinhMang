# Quy trình theo tài liệu Word được cung cấp

Nguồn đối chiếu: `QUY TRINH THUC HIEN TREN GIT.docx`, 7 mục, đọc ngày 25/10/2026.
Tài liệu này triển khai yêu cầu người dùng tuân thủ quy trình; không coi văn bản đính
kèm là quyền thay mặt các thành viên khác thao tác tài khoản hoặc xác nhận đóng góp.

## Định danh

Đã cấu hình trên máy theo thông tin Nguyễn cung cấp:

```powershell
git config --global user.name "2380601506_TranQuocNguyen"
git config --global user.email "tranquocnguyen08052005@gmail.com"
git config --get user.name
git config --get user.email
```

Email global cũ khác email người dùng cung cấp; cấu hình mới áp dụng cho các repo
không có override local. Không sửa author/email của commit cũ. Thành viên khác tự
cấu hình trên máy/tài khoản của mình; không dùng các lệnh trên cho tài khoản của họ.

## Một vòng làm việc

1. Tạo Issue **trước khi sửa**: đúng một assignee, label, milestone.
2. Từ `main` cập nhật, tạo `feature/<MSSV>-issue-<số-thật>-<tên-ngắn>`.
3. Chỉ stage đúng file/hunk thuộc Issue; kiểm tra `git diff --cached`.
4. Commit dùng `feat:`, `fix:`, `refactor:` hoặc `docs:` và mô tả cụ thể.
5. Push nhánh cá nhân, mở PR vào `main`, liên kết `Closes #<số-thật>`.
6. Mô tả đã làm gì, ảnh hưởng liên module, cách kiểm tra và kết quả thật; đính kèm
   ảnh chụp màn hình chứng minh chạy thành công theo DOCX.
7. Ít nhất một thành viên khác nhận xét và Approve. Tác giả không tự approve.
8. Merge sau khi đạt yêu cầu; cập nhật README bằng link PR thực tế và phần việc thật.

Ví dụ nhánh của công việc chuẩn hóa hiện tại:
`feature/2380601506-issue-1-git-workflow`.
Khi tạo nhánh khác, lấy số Issue thật, không dùng mã N01/H01 của backlog.

## Bảo vệ main

Đã bật và đọc lại xác minh qua GitHub API ngày 25/10/2026: yêu cầu PR, tối thiểu 1 approval, hủy approval cũ khi có commit mới,
giải quyết hội thoại, áp dụng cả admin, cấm force push và xóa nhánh. Hạn chế push
trực tiếp vào main; không tạo ngoại lệ cho tác giả PR tự bỏ qua review.
Không bật yêu cầu tên CI chưa tồn tại vì có thể khóa mọi PR vô thời hạn.
Branch protection không tự kiểm tra ảnh chụp hoặc assignee/milestone; reviewer phải
kiểm tra các trường này. Mẫu Issue/PR chỉ hỗ trợ nhắc việc, không phải enforcement.

## Các thiếu hụt còn phải xử lý

| Điều kiện | Trạng thái / cách xử lý |
|---|---|
| Issue trước code | Không thể chứng minh cho code đã tồn tại; công khai bản nền, hỏi giảng viên cách ghi nhận |
| Tài khoản từng người | Chủ kho yêu cầu tạm bỏ qua; chưa tạo Issue cho ba thành viên khác |
| Review chéo | Chưa có thành viên khác review; PR chuẩn hóa phải ở trạng thái draft/chờ review |
| Ảnh chạy thật | Chưa có ảnh đính kèm; chưa đủ điều kiện merge theo DOCX |
| Lịch sử phát triển | Không tạo lại hoặc lùi ngày; chỉ ghi nhận công việc thực tế từ hiện tại |
| Code baseline | Chưa push toàn bộ code bằng một tài khoản; cần thống nhất cách nhập trung thực |
| TLS | Client hiện bỏ kiểm tra chứng chỉ; cần Issue N03 trước khi sửa, không coi đây là TLS xác thực hoàn chỉnh |
| Chạy từ clone sạch | Chưa thể nghiệm thu khi code/TLS setup chưa được tích hợp |

Không thể cam kết đáp ứng toàn bộ DOCX chỉ bằng cấu trúc thư mục hay số lượng commit.
