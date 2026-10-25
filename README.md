# Đồng bộ tệp tin Client–Server — Nhóm 14

Đồ án học phần **Lập trình mạng**, lớp **23DTHB1**. Ứng dụng Java đồng bộ tệp giữa
client và server qua Socket TCP, có giao diện Swing và trang giám sát HTTP.

## Thành viên

| MSSV | Họ và tên | Vai trò |
|---|---|---|
| 2380600941 | Bùi Nhật Quỳnh Hương | Nhóm trưởng, điều phối nghiệp vụ và nghiệm thu |
| 2380601506 | Trần Quốc Nguyễn | Thành viên, phụ trách mạng và bảo mật |
| 2380602014 | Lý Quốc Thái | Thành viên, phụ trách truyền tệp |
| 2380602385 | Nguyễn Trung Trực | Thành viên, phụ trách giao diện và giám sát |

## Chức năng và kiến trúc

- Kết nối TCP thường hoặc TLS; server xử lý nhiều phiên client bằng thread pool.
- Quét thư mục và tính SHA-256; so sánh metadata với snapshot để lập kế hoạch đồng bộ.
- Upload/download theo khối, ACK và Batch ACK; dùng tệp `.part` để tính vị trí tiếp tục truyền.
- Phát hiện xung đột: giữ bản client dưới tên có `_conflict_Client_`, tải bản server về tên gốc.
- Đồng bộ xóa dựa trên snapshot; khóa đọc/ghi khi xử lý tệp và kiểm tra đường dẫn.
- Hiển thị tiến độ, nhật ký, danh sách tệp; lập lịch đồng bộ và dashboard HTTP.

Luồng chính: **GUI → SyncClient → PacketStream/TCP → ClientHandler → SyncDiffEngine → SyncPlan → truyền/xóa/xử lý conflict → lưu snapshot**.

Đây là **một project Maven**, không phải bốn project độc lập. Các phần việc cùng dùng
model và giao thức, được tích hợp về một nhánh `main` sau review.

```text
pom.xml                         Cấu hình Maven, Java 17
nbactions.xml                   Chạy/debug trong NetBeans
run.ps1                         Launcher Windows, tạo chứng chỉ lab và biên dịch
src/main/java/com/nhom14/filesync/
  Main.java                     Điểm vào ứng dụng
  client/                       Kết nối, thực thi kế hoạch, lịch đồng bộ
  server/                       Nhận kết nối, xử lý lệnh, dashboard HTTP
  protocol/                     Hằng số giao thức, đọc/ghi dữ liệu nhị phân
  model/                        Metadata, kế hoạch, khối truyền, tiến độ
  engine/                       So sánh trạng thái, snapshot, chia/ghép khối
  security/                     TLS và kiểm tra đường dẫn
  util/                         Hash, quét thư mục, khóa tệp
  gui/                          Swing và các cặp .java/.form
src/main/resources/ssl/          Tài nguyên TLS tạo tại máy, không commit khóa riêng
src/test/java/com/nhom14/filesync/test/TestRunner.java
```

## Cài đặt và chạy

Yêu cầu **JDK 17 trở lên**; hướng dẫn launcher bên dưới dành cho Windows PowerShell.
Cấu hình `JAVA_HOME` trỏ đến JDK nếu máy không tự tìm được `javac` và `keytool`.

Trong khi PR mã nguồn chưa merge, lấy đúng nhánh chứa project:

```powershell
git clone --branch feature/2380601506-issue-3-project-source https://github.com/tranquocnguyen/Nhom14_LapTrinhMang.git
cd Nhom14_LapTrinhMang
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Launcher biên dịch mã Java, tạo chứng chỉ lab nếu chưa có và mở menu chính.
Lần đầu tạo thư mục tài nguyên TLS; `client_data` và `server_data` dùng làm thư mục
đồng bộ mặc định. Các dữ liệu này không nằm trong Git.

1. Mở giao diện Server, chọn thư mục thử nghiệm và cổng `8888`, rồi khởi động.
2. Mở Client, nhập `localhost`, cổng `8888`; chọn chế độ TLS giống Server.
3. Chọn thư mục client chứa dữ liệu mẫu, kết nối và bấm đồng bộ.
4. Xem dashboard tại [localhost:8088](http://localhost:8088/) sau khi Server chạy.

Dùng thư mục thử nghiệm riêng vì thao tác đồng bộ có thể cập nhật hoặc xóa tệp.
Để chạy bằng Maven/NetBeans, chuẩn bị tài nguyên lab trước:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1 -BuildOnly
mvn clean compile
mvn exec:java
```

Hoặc mở thư mục có `pom.xml` trong NetBeans và Run Project. Khi chỉnh GUI bằng
GUI Builder, giữ nguyên cặp `.java` và `.form` tương ứng.

## Phân công công việc cân đối

Phân công dưới đây dựa trên trách nhiệm trong code hiện có, dùng cho việc rà soát,
hoàn thiện và bảo vệ đồ án. **Đây không phải xác nhận ai đã viết code trước đây.**
Khối lượng được cân đối theo độ khó thuật toán, tích hợp và kiểm thử; số dòng của
GUI Builder không được coi tương đương số dòng xử lý giao thức.

Mỗi người nhận một cụm nghiệp vụ chính, một phần tích hợp và bộ kiểm thử tương ứng;
đồng thời phải giải thích được luồng xử lý của phần mình phụ trách. Nhóm trưởng
điều phối nhưng không làm thay hoặc commit thay các thành viên.

Các đường dẫn Java trong bảng tính từ `src/main/java/com/nhom14/filesync/`.

| Thành viên | Cụm chính | File phụ trách riêng | Kết quả phải bàn giao |
|---|---|---|---|
| **Hương — 2380600941** | Metadata, thuật toán đồng bộ và bảo toàn dữ liệu | `model/FileMetadata.java`, `model/SyncAction.java`, `model/SyncItem.java`, `model/SyncPlan.java`; `util/HashUtil.java`, `util/FileSyncUtil.java`; `engine/SyncDiffEngine.java`, `engine/SyncStateTracker.java` | Quy tắc upload/download/conflict/xóa rõ ràng; quét/hash chính xác; snapshot nhất quán; test các trường hợp hai bên cùng sửa, một bên xóa và tệp mới; tổng hợp nghiệm thu nhóm |
| **Nguyễn — 2380601506** | Kết nối TCP, vòng đời server, bảo mật và khóa tệp | `server/SyncServer.java`, `server/ServerListener.java`; `security/PathSecurity.java`, `security/SslContextFactory.java`; `util/FileLockManager.java`; `gui/ServerForm.java` + `.form`; `pom.xml`, `nbactions.xml`, `.gitignore`, `run.ps1` | Server khởi động/dừng đúng, xử lý nhiều client; kiểm tra đường dẫn/TLS; giao diện server và log đúng trạng thái; clone sạch có thể chạy; test lỗi kết nối, tranh chấp khóa và chứng chỉ |
| **Thái — 2380602014** | Giao thức truyền tệp, chia/ghép khối, ACK và resume | `engine/ChunkEngine.java`; `protocol/PacketStream.java`, `protocol/ProtocolConstants.java`; `model/ChunkPacket.java`, `model/ChunkAck.java` | Định dạng đọc/ghi hai đầu khớp; upload/download đủ byte; xác minh hash từng khối/toàn tệp; test khối cuối lẻ, tệp rỗng, dữ liệu lỗi và ngắt mạng rồi tiếp tục truyền |
| **Trực — 2380602385** | Trải nghiệm client, lịch đồng bộ và dashboard | `gui/ClientForm.java` + `.form`, `gui/MainLauncherForm.java` + `.form`, `gui/ModernTheme.java`; `client/ClientListener.java`, `client/AutoSyncScheduler.java`; `model/SyncProgress.java`; `server/WebDashboardServer.java`; `Main.java` | Giao diện phản hồi đúng, cập nhật Swing trên EDT; tiến độ/log rõ ràng; lịch đồng bộ không tạo phiên chồng; dashboard hiển thị đúng dữ liệu; ảnh demo và test thao tác thành công/thất bại |

Nguyễn nhận thêm ServerForm và phần build để giảm khối lượng giao diện cho Trực.
Thái có ít file riêng hơn nhưng chịu trách nhiệm truyền dữ liệu ở cả hai đầu;
Hương chịu trách nhiệm các trường hợp dễ mất dữ liệu cùng việc điều phối nghiệm thu.
Không suy ra tỷ lệ đóng góp đã hoàn thành là 25% chỉ từ bảng phân công này.

### Ranh giới trong các file dùng chung

Hai file tích hợp là `client/SyncClient.java` và `server/ClientHandler.java`;
phân chia theo hàm bên dưới, không gán toàn bộ cả hai file cho một thành viên.

| File/hàm | Người chủ trì phần việc | Phối hợp bắt buộc |
|---|---|---|
| `SyncClient.connect`, `disconnect`, `isConnected`; khung chạy bất đồng bộ và kết thúc phiên trong `sync`/`syncBlocking` | Nguyễn | Trực kiểm tra trạng thái UI; Thái kiểm tra handshake/luồng đọc ghi |
| Gửi/nhận metadata và kế hoạch trong `SyncClient.sync`; `executeConflict`, `executeDeleteLocal`, `executeDeleteRemote` | Hương | Nguyễn kiểm tra đường dẫn, Thái kiểm tra thứ tự lệnh |
| `SyncClient.executeUpload`, `executeDownload`, cấu hình chunk/mode | Thái | Hương xác nhận metadata/hash; Trực xác nhận callback tiến độ |
| `ClientHandler.run`, `handleHello`, `closeConnection`, `getClientInfo` | Nguyễn | Thái kiểm tra tương thích giao thức |
| `ClientHandler.handleMetadataAndPlan`, `handleDelete` | Hương | Nguyễn kiểm tra khóa/đường dẫn |
| `ClientHandler.handleUpload`, `handleDownload` | Thái | Nguyễn kiểm tra giải phóng tài nguyên/khóa khi lỗi |
| `TestRunner.java` | Mỗi người thêm test đúng phạm vi ở bảng dưới | Hương tổng hợp; không giao toàn bộ kiểm thử cho một người |
| `README.md` | Hương điều phối; từng người bổ sung minh chứng phần mình | Chỉ ghi nhận công việc thực tế và PR có thật |

Mỗi Issue có đúng **một người được assign**. Khi hai Issue sửa cùng file, thực hiện
nối tiếp hoặc thống nhất phạm vi hunk; sau khi PR trước merge phải cập nhật `main`
trước khi tiếp tục. Không để hai người cùng thay toàn bộ `SyncClient`/`ClientHandler`.

### Kiểm thử và review chéo

| Người thực hiện | Phạm vi nghiệm thu | Người review đề xuất |
|---|---|---|
| Hương | Quét thư mục lồng nhau/Unicode; hash; xung đột giữ hai bản; xóa theo snapshot; khởi động lại vẫn đọc trạng thái | Trực |
| Nguyễn | Hai client kết nối; dừng server/ngắt giữa phiên; chặn thoát root; khóa đọc/ghi; chứng chỉ hợp lệ và không hợp lệ; chạy từ clone sạch | Hương |
| Thái | Upload và download với nhiều kích thước chunk; tệp rỗng/khối cuối; hash sai; ACK lỗi; resume sau ngắt mạng thật; Batch ACK | Nguyễn |
| Trực | Kết nối/thất bại/ngắt kết nối trên GUI; cập nhật tiến độ; bật/tắt lịch nhiều lần; thao tác khi đang sync; dashboard HTTP và ảnh demo | Thái |

Reviewer phải đọc code, chạy kiểm tra phù hợp, để lại nhận xét và Approve bằng tài
khoản của chính mình. Vai trò trong bảng không thay thế approval thực tế trên GitHub.

## Bảng đóng góp đã ghi nhận

Bảng này tách khỏi kế hoạch phân công ở trên. Mã nền được đưa lên bằng tài khoản
Nguyễn; PR nhập mã không chứng minh tác giả của từng module. Chỉ cập nhật công việc
đã thực hiện khi có bằng chứng, không biến tên `@author` thành lịch sử đóng góp Git.

| MSSV | Họ và tên | Công việc đã thực hiện có thể đối chiếu | Merge Requests / Pull Requests |
|---|---|---|---|
| 2380600941 | Bùi Nhật Quỳnh Hương | Chưa có PR cá nhân được đối chiếu; phụ trách theo bảng phân công | Chưa có |
| 2380601506 | Trần Quốc Nguyễn | Đưa bản mã nguồn hiện có lên nhánh project, bổ sung launcher và kiểm tra bản xuất sạch; cập nhật README/phân công. Không quy toàn bộ tác giả code nền cho người nhập | [#4 — đang chờ review/merge](https://github.com/tranquocnguyen/Nhom14_LapTrinhMang/pull/4), [Issue #3](https://github.com/tranquocnguyen/Nhom14_LapTrinhMang/issues/3) |
| 2380602014 | Lý Quốc Thái | Chưa có PR cá nhân được đối chiếu; phụ trách theo bảng phân công | Chưa có |
| 2380602385 | Nguyễn Trung Trực | Chưa có PR cá nhân được đối chiếu; phụ trách theo bảng phân công | Chưa có |

## Kiểm tra project

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1 -Test
```

Launcher chạy `TestRunner` ở thư mục tạm riêng vì bộ test tạo/xóa `test_env`.
`TestRunner` là chương trình có `main`, không phải test JUnit tự được chạy bởi
`mvn test`. Lần kiểm tra bản xuất sạch ngày **25/10/2026**, commit `453f9fe`, biên dịch
Java 17 và chạy bộ test hiện có đạt **8/8**, exit code **0**. Đây không phải chứng nhận
mọi trường hợp đã được kiểm thử; thay đổi README không thay đổi kết quả chạy Java.

Các việc cần hoàn thiện theo phân công:

- **Nguyễn:** client TLS hiện dùng trust manager chấp nhận mọi chứng chỉ; handshake
  thành công chưa chứng minh xác thực server. Chứng chỉ/mật khẩu hiện tại dành cho lab.
- **Thái:** test resume hiện mới kiểm tra offset dựng sẵn, cần thêm ngắt mạng thật;
  kiểm tra giới hạn độ dài khối trước khi cấp phát trong `PacketStream.readChunk`.
- **Hương:** mở rộng test xóa ở cả hai chiều và tổ hợp sửa/xóa; kiểm tra toàn vẹn snapshot.
- **Trực:** bổ sung bằng chứng GUI/scheduler và kiểm tra khi kết nối lỗi; ảnh chụp thật
  phải được đính kèm PR.
- **Cả nhóm:** sửa cách báo cáo của TestRunner (chuỗi “100% thành công” hiện cố định),
  kiểm tra đầy đủ số test dự kiến và quy định exit code khi thất bại.

## Quy trình làm việc trên Git

1. Tạo Issue trước thay đổi: một assignee thật, label và milestone cụ thể.
2. Tạo nhánh từ `main` đã cập nhật: `feature/<MSSV>-issue-<số-issue>-<tên-ngắn>`.
3. Commit phần việc thực tế bằng định danh cá nhân, dùng `feat:`, `fix:`, `refactor:` hoặc `docs:`.
4. Mở PR nêu thay đổi, ảnh hưởng module khác, lệnh/kết quả kiểm tra và ảnh chạy thật.
5. Ít nhất một thành viên khác nhận xét và Approve; giải quyết phản hồi trước khi merge.
6. Cập nhật bảng đóng góp bằng PR thật; tất cả phần việc cuối cùng về cùng một project.

Không commit `target`, `bin`, cấu hình IDE cá nhân, dữ liệu đồng bộ, JAR/ZIP hay khóa
riêng. Không commit thay tài khoản người khác hoặc dựng lại ngày tháng/lịch sử.
Mã nền đã có trước Issue nhập mã; nhóm cần thống nhất với giảng viên cách ghi nhận
phần này. Phân chia trách nhiệm hiện tại không thay thế tiến độ thực tế trước đây.
