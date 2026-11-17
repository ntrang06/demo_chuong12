# Chương 12 – Persisting Data

**Môn học:** Lập trình thiết bị di động  
**Sinh viên:** Bùi Ngọc Trang  
**MSSV:** 079306029517

## Giới thiệu

Ứng dụng **Ghi chú buổi học** minh họa cách Android lưu dữ liệu qua một tình huống đơn giản: sinh viên nhập ghi chú, bật chế độ tối rồi đóng ứng dụng. Khi mở lại, ứng dụng vẫn nhớ chế độ đã chọn và hiển thị bản nháp đã lưu. Nếu muốn xem ghi chú bên ngoài ứng dụng, sinh viên có thể xuất một bản sao dưới dạng tệp văn bản.

## Chức năng minh họa

| Chức năng | Cách lưu | Kết quả |
|---|---|---|
| Ghi nhớ chế độ tối | Preferences DataStore | Khi mở lại ứng dụng, giao diện vẫn dùng chế độ đã chọn. |
| Lưu bản nháp | Tệp trong bộ nhớ riêng của ứng dụng | Nội dung đã bấm **Lưu bản nháp** xuất hiện lại khi mở ứng dụng. |
| Xuất bản sao | Storage Access Framework (SAF) | Người dùng chọn nơi lưu `ghichu.txt` và có thể mở tệp bằng ứng dụng **Tệp**. |

## Cấu trúc bài nộp

- **[Source Code](../Source%20Code/):** mã nguồn ứng dụng Android, viết bằng Kotlin và Jetpack Compose.
- **Tài liệu:** README, slide thuyết trình và video demo

## Cách chạy ứng dụng

1. Tải hoặc clone repository về máy.
2. Trong Android Studio, chọn **File → Open**, sau đó mở thư mục **Source Code**. Đây là thư mục chứa `settings.gradle.kts`.
3. Đợi Gradle đồng bộ xong, chọn điện thoại Android hoặc máy ảo, rồi bấm **Run ▶**.

## Các bước thử demo

1. Mở **Ghi chú buổi học**, bật **Chế độ tối**.
2. Nhập `Làm bài tập chương 12` và bấm **Lưu bản nháp**.
3. Đóng ứng dụng khỏi danh sách ứng dụng gần đây rồi mở lại. Kiểm tra giao diện vẫn tối và nội dung ghi chú vẫn còn.
4. Bấm **Xuất bản sao**, chọn thư mục **Tải xuống** và lưu với tên `ghichu.txt`.
5. Mở ứng dụng **Tệp → Tải xuống → ghichu.txt**. Nếu đọc được dòng `Làm bài tập chương 12` thì bản sao đã được xuất thành công.

## Video demo

[Xem video demo](./demo.mp4)

