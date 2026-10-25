# Phân chia đồ án theo trách nhiệm

## Cơ sở và giới hạn

Nhóm trưởng là **Bùi Nhật Quỳnh Hương — 2380600941**, theo thông tin người dùng
đính chính. Trần Quốc Nguyễn là thành viên và chủ tài khoản repository hiện tại;
vai trò chủ repository không đồng nghĩa với nhóm trưởng. Phân công kỹ thuật dưới
đây là đề xuất, cần nhóm trưởng và các thành viên thống nhất.

Đối chiếu toàn bộ danh sách file trong `src`, các import, các điểm xử lý trong
`SyncClient`/`ClientHandler`, và bảng thành viên của `BAO_CAO_DO_AN.md` tại máy.
Đây là **phân công đề xuất cho công việc tiếp theo trên mã đã có**, không khẳng định
ai đã viết mã trước đây. Không đổi tác giả, tạo commit hộ, lùi ngày commit hoặc
chia mã có sẵn thành các commit giả lập tiến độ.

Giữ một project Maven: chia theo module/trách nhiệm, không tách thành bốn project
không biên dịch được. Khi refactor cần Issue riêng và kiểm thử tương thích giao thức.
Đường dẫn Java bên dưới tính từ `src/main/java/com/nhom14/filesync/`.

## Bản đồ file đầy đủ

| Chủ trì đề xuất | File | Phạm vi và điểm bàn giao |
|---|---|---|
| Nguyễn — 2380601506 | `server/SyncServer.java`, `server/ServerListener.java` | Vòng đời server, nhận kết nối, executor và callback |
| Nguyễn | `server/ClientHandler.java` | Điều phối phiên TCP; các hàm nghiệp vụ được phân giới bên dưới |
| Nguyễn | `security/SslContextFactory.java`, `security/PathSecurity.java` | TLS và xác thực đường dẫn; Hương review đường dẫn khi quét/xóa |
| Nguyễn | `util/FileLockManager.java` | Khóa đọc/ghi và giải phóng khi lỗi |
| Nguyễn | `protocol/ProtocolConstants.java` | Hợp đồng chung, mã lệnh, phiên bản, thông số; thay đổi phải thống nhất với Thái/Hương |
| Nguyễn | `pom.xml`, `nbactions.xml` (gốc) | Build Java 17 và chạy NetBeans; chỉ chia sẻ nbactions nếu không có đường dẫn cá nhân |
| Nguyễn | `src/main/resources/ssl/server.crt`, `server.keystore`, `client.truststore` (đường dẫn từ gốc) | Tài nguyên TLS hiện có; keystore/truststore chỉ ở máy và bị ignore; cần quy trình tái tạo lab |
| Hương — 2380600941 | `model/FileMetadata.java`, `model/SyncAction.java`, `model/SyncItem.java`, `model/SyncPlan.java` | Hợp đồng metadata và kế hoạch; thay đổi serialization phải review với Nguyễn/Thái |
| Hương | `util/HashUtil.java`, `util/FileSyncUtil.java` | SHA-256, quét thư mục và thao tác file an toàn |
| Hương | `engine/SyncDiffEngine.java`, `engine/SyncStateTracker.java` | So sánh trạng thái, conflict, dấu vết phục vụ đồng bộ xóa |
| Thái — 2380602014 | `engine/ChunkEngine.java` | Chia/ghép khối, offset và resume |
| Thái | `model/ChunkPacket.java`, `model/ChunkAck.java`, `protocol/PacketStream.java` | Đóng gói luồng, ACK/NACK và đọc/ghi hai đầu tương thích |
| Nguyễn (điều phối file) | `client/SyncClient.java` | File dùng chung TCP, kế hoạch và truyền khối; bàn giao theo hàm bên dưới |
| Trực — 2380602385 | `client/ClientListener.java`, `model/SyncProgress.java`, `client/AutoSyncScheduler.java` | Callback UI, tiến độ và lịch đồng bộ; không chạy chồng phiên sync |
| Trực | `gui/ModernTheme.java` | Thành phần giao diện dùng chung |
| Trực | `gui/ClientForm.java` + `gui/ClientForm.form` | Giao diện client; luôn sửa/commit cặp file cùng nhau |
| Trực | `gui/ServerForm.java` + `gui/ServerForm.form` | Giao diện server; luôn sửa/commit cặp file cùng nhau |
| Trực | `gui/MainLauncherForm.java` + `gui/MainLauncherForm.form`, `Main.java` | Menu khởi chạy và entry point |
| Trực | `server/WebDashboardServer.java` | HTTP dashboard và trình bày; Nguyễn review vòng đời server/cổng |
| Nguyễn (tích hợp) | `src/test/java/com/nhom14/filesync/test/TestRunner.java` (từ gốc) | Mỗi người bổ sung test cho Issue của mình, Nguyễn kiểm tra tích hợp |
| Nguyễn (tổng hợp) | `README.md`, `BAO_CAO_DO_AN.md`, `HUONG_DAN_CHAY.md`, `docs/`, `.github/`, `.gitignore` | Tài liệu chung; thành viên tự cập nhật đóng góp có PR chứng minh |

## Các file dùng chung: phân giới ở mức hàm

| File | Nguyễn | Hương | Thái | Trực |
|---|---|---|---|---|
| `SyncClient.java` | `connect`, `disconnect`, `isConnected`, điều phối `sync`/`syncBlocking` | Phần metadata/kế hoạch trong `sync`, `executeConflict`, `executeDeleteRemote`, `executeDeleteLocal` | `executeUpload`, `executeDownload`, thương lượng chunk/mode | Góp ý callback/log/tiến độ qua Issue, không sửa thuật toán truyền trong Issue giao diện |
| `ClientHandler.java` | `run`, `handleHello`, `closeConnection`, quản lý phiên | `handleMetadataAndPlan`, `handleDelete` | `handleUpload`, `handleDownload` | Không phụ trách logic socket |
| `WebDashboardServer.java` | Review start/stop và tích hợp `SyncServer` | Review nội dung metadata | Review chỉ số truyền nếu thay đổi | Chủ trì HTTP handler, HTML và cập nhật dashboard |
| `TestRunner.java` | TCP/TLS/path/lock và tổng hợp kết quả | hash/conflict/xóa | chunk/resume/ACK | thao tác GUI có ảnh riêng; scheduler/dashboard |

Mỗi Issue vẫn chỉ có **một assignee**. Người review không phải đồng assignee.
Hai Issue đụng cùng file phải xếp nối tiếp: PR trước merge, tác giả PR sau cập nhật
`origin/main` rồi mới sửa. Các hàm trên là ranh giới phối hợp, chưa phải lớp độc lập.

## Thứ tự tích hợp

1. Chuẩn hóa Git, hướng dẫn và hợp đồng dữ liệu/giao thức.
2. Utilities, metadata, diff/state và chunk/packet dựa trên hợp đồng đã thống nhất.
3. TLS, TCP client/server và tích hợp truyền/nhận theo các hàm đã phân giới.
4. GUI, scheduler, dashboard sau khi API callback ổn định.
5. Kiểm thử liên module, chạy từ clone sạch, ảnh minh chứng và đối chiếu README.

Không nhập lần lượt những mảnh code đang phụ thuộc vòng rồi tuyên bố từng PR chạy
độc lập. Trước khi nhập bản nền đã có, nhóm cần thống nhất với giảng viên cách ghi
nhận nguồn gốc bản nền; README phải phân biệt bản nền và thay đổi mới. Kế hoạch này
không tự khắc phục được yêu cầu tạo Issue trước code đối với mã viết trước đây.

## Bản thầy tải về phải là một project hoàn chỉnh

Giữ duy nhất `pom.xml` ở gốc và các package trong `src/main/java`; các phần việc
được tích hợp về cùng `main`. Không tạo bốn thư mục bài nộp riêng cho bốn người.
Commit là đơn vị ghi nhận thay đổi; bản tải về là toàn bộ cây file tại một commit,
không phải một mảnh riêng tương ứng từng commit.

Trước khi chốt bản nộp cần kiểm tra từ clone sạch, không dùng `target` có sẵn:

1. Có đủ model/protocol/util/engine/client/server/gui và tài nguyên cần thiết.
2. Có hướng dẫn tái tạo chứng chỉ lab, không phụ thuộc khóa riêng trên máy tác giả.
3. `mvn clean test-compile` biên dịch được; chạy trực tiếp TestRunner và kiểm tra exit code.
4. `mvn exec:java` mở ứng dụng; kiểm tra kết nối, đồng bộ, conflict và resume thật.
5. README ghi rõ JDK, Maven, lệnh chạy và ảnh minh chứng thực tế.

Chỉ tạo tag/bản nộp sau khi các PR được review và các bước trên đạt. Chia commit
theo chức năng giúp review, nhưng không chứng minh có nhiều người thực hiện và
không bảo đảm miễn chế tài Big Bang trong DOCX. Với mã đã hoàn thiện trước khi có
Issue, cần thống nhất cách nhập bản nền minh bạch với giảng viên.
