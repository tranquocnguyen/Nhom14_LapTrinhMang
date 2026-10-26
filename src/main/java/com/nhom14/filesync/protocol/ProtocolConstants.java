package com.nhom14.filesync.protocol;

/**
 * Định nghĩa giao thức truyền thông tầng ứng dụng trên nền TCP/SSL
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 * @author Bùi Nhật Quỳnh Hương (MSSV: 2380600941) - Nhóm trưởng
 */
public class ProtocolConstants {

    public static final String PROTOCOL_VERSION = "SYNC_NET_v1.0";
    public static final int DEFAULT_PORT = 8888;
    public static final int DEFAULT_CHUNK_SIZE = 65536; // 64KB chuẩn theo nhận xét GV

    // Các chế độ truyền tải
    public static final String MODE_STOP_AND_WAIT = "STOP_AND_WAIT";
    public static final String MODE_BATCH_WINDOW = "BATCH_WINDOW";

    // Các lệnh giao thức chuẩn hóa
    public static final String CMD_HELLO = "HELLO";
    public static final String CMD_HELLO_OK = "HELLO_OK";

    public static final String CMD_SEND_METADATA = "SEND_METADATA";
    public static final String CMD_METADATA_ITEM = "META";
    public static final String CMD_METADATA_END = "META_END";

    public static final String CMD_SYNC_PLAN_ITEM = "PLAN_ITEM";
    public static final String CMD_SYNC_PLAN_END = "PLAN_END";

    public static final String CMD_UPLOAD_START = "UPLOAD_START";
    public static final String CMD_UPLOAD_RESUME = "UPLOAD_RESUME";
    public static final String CMD_UPLOAD_CHUNK = "UPLOAD_CHUNK";
    public static final String CMD_UPLOAD_ACK = "UPLOAD_ACK";
    public static final String CMD_UPLOAD_BATCH_CHUNKS = "UPLOAD_BATCH";
    public static final String CMD_UPLOAD_BATCH_ACK = "UPLOAD_BATCH_ACK";
    public static final String CMD_UPLOAD_END = "UPLOAD_END";
    public static final String CMD_UPLOAD_SUCCESS = "UPLOAD_SUCCESS";

    public static final String CMD_DOWNLOAD_START = "DOWNLOAD_START";
    public static final String CMD_DOWNLOAD_RESUME = "DOWNLOAD_RESUME";
    public static final String CMD_DOWNLOAD_CHUNK = "DOWNLOAD_CHUNK";
    public static final String CMD_DOWNLOAD_ACK = "DOWNLOAD_ACK";
    public static final String CMD_DOWNLOAD_BATCH_CHUNKS = "DOWNLOAD_BATCH";
    public static final String CMD_DOWNLOAD_BATCH_ACK = "DOWNLOAD_BATCH_ACK";
    public static final String CMD_DOWNLOAD_END = "DOWNLOAD_END";
    public static final String CMD_DOWNLOAD_SUCCESS = "DOWNLOAD_SUCCESS";

    public static final String CMD_DELETE_FILE = "DELETE_FILE";
    public static final String CMD_DELETE_SUCCESS = "DELETE_SUCCESS";

    public static final String CMD_BYE = "BYE";
    public static final String CMD_ERROR = "ERROR";
}