# Backlog đề xuất — không phải Issue đã được tạo

Issue thật đã tạo: **#1** — chuẩn hóa quy trình, assignee `tranquocnguyen`, label
`documentation`, milestone `M1 - Chuẩn hóa quy trình và phân chia module`.

Các mã dưới đây là mã kế hoạch, **không phải số Issue GitHub**. Chỉ tạo Issue khi
biết đúng username, gán đúng một người, label và milestone, trước khi sửa code.
Theo yêu cầu chủ kho, việc bổ sung username Hương/Thái/Trực tạm hoãn.
Không dùng username Nguyễn thay cho ba người này.

| Mã | Chủ trì | Label / milestone đề xuất | Việc cần làm và nghiệm thu |
|---|---|---|---|
| N01 | Nguyễn | documentation / M1 | Chốt nguồn gốc bản code đã có và cách nhập baseline với giảng viên; ghi nhận trung thực, không giả lập lịch sử |
| N02 | Nguyễn | enhancement / M2-Core | Rà soát TCP đa luồng, vòng đời kết nối và file lock; chứng minh hai client hoạt động, ngắt kết nối giải phóng tài nguyên |
| N03 | Nguyễn | bug / M2-Core | Sửa client TLS đang dùng TrustManager chấp nhận mọi chứng chỉ; nghiệm thu từ chối chứng chỉ không tin cậy và chấp nhận chứng chỉ lab hợp lệ |
| N04 | Nguyễn | enhancement / M2-Core | Bổ sung cách tạo keystore/truststore lab ở máy, giữ khóa riêng ngoài Git; clone sạch có thể build và chạy TLS theo hướng dẫn |
| H01 | Hương | enhancement / M2-Core | Rà soát metadata/quét/hash: thư mục lồng nhau, tệp rỗng, Unicode, đổi nội dung; hash hai đầu khớp |
| H02 | Hương | enhancement / M2-Core | Conflict/state/xóa: giữ cả bản khi xung đột, không xóa nhầm tệp mới, chặn đường dẫn thoát root |
| T01 | Thái | enhancement / M2-Core | Chunk/ACK: tệp rỗng, khối cuối lẻ, kích thước 64–256KB, hash sai và ACK lỗi; không báo thành công khi dữ liệu lỗi |
| T02 | Thái | enhancement / M2-Core | Resume/Batch ACK: ngắt thực tế giữa truyền rồi kết nối lại; kiểm tra offset, hash toàn tệp, không chỉ dựng file .part |
| R01 | Trực | enhancement / M3-Integration | Rà soát các cặp .java/.form, callback EDT, trạng thái kết nối và tiến độ; ảnh GUI thật, UI không treo khi truyền |
| R02 | Trực | enhancement / M3-Integration | Scheduler/dashboard: tránh sync chồng, dừng lịch khi cần, dashboard phản ánh dữ liệu thực; ảnh HTTP/GUI thật |
| N05 | Nguyễn | bug / M3-Integration | Rà soát TestRunner: không in “100% thành công” khi fail, đủ số test dự kiến, HTTP lỗi không bị coi là pass; kiểm thử thất bại phải exit khác 0 |
| N06 | Nguyễn | documentation / M3-Integration | Tổng hợp bằng chứng sau khi từng người tự làm PR; chạy clone sạch, đối chiếu README với PR thật, không tự approve |

## Mẫu nội dung một Issue

- Nguồn gốc: sửa/rà soát mã hiện có, nêu hiện trạng cụ thể.
- Một người phụ trách: username thật; một milestone, ít nhất một label.
- Mục tiêu và file/hàm được sửa (theo bản đồ module).
- Đầu vào/phụ thuộc PR nào; không dùng số PR giả.
- Tiêu chí nghiệm thu đo được, cách chạy test, tình huống lỗi.
- Ảnh cần chụp khi chạy xong; không dùng ảnh dựng làm bằng chứng chạy thật.
- Reviewer đề xuất là thành viên khác, không tự approve.

Không đặt ngày hoàn thành giả cho các Issue nhằm tạo hình thức trải tiến độ.
Commit khi thực sự có thay đổi hoàn chỉnh, với ngày giờ thực tế.
