# Quản lý Cửa hàng Thời trang

Ứng dụng di động Android quản lý cửa hàng quần áo, giúp quản lý danh mục, sản phẩm, đơn hàng và đánh giá khách hàng.

## 1. Giới thiệu dự án

Dự án được phát triển sử dụng Android Studio với ngôn ngữ lập trình **Java**. Ứng dụng cung cấp các chức năng:
- Quản lý tài khoản người dùng
- Quản lý danh mục sản phẩm
- Quản lý sản phẩm (thêm, sửa, xóa, tìm kiếm)
- Quản lý đơn hàng (chờ xác nhận, giao hàng, hoàn thành)
- Quản lý đánh giá sản phẩm
- Quản lý voucher giảm giá

## 2. Cài đặt môi trường

Để phát triển và chạy ứng dụng, bạn cần cài đặt:
- **Android Studio** phiên bản mới nhất
- **SDK Android** tối thiểu Android 5.0 (API 21)
- **Gradle** tích hợp trong Android Studio

## 3. Cấu trúc dự án

- `app/java/com/example/quanlycuahangthoitrang/`: Chứa mã nguồn Java
  - `activity/`: Các Activity của ứng dụng
  - `adapter/`: Các Adapter cho RecyclerView
  - `database/`: Logic Database Helper
  - `fragment/`: Các Fragment giao diện
  - `model/`: Các lớp mô hình dữ liệu
  - `utils/`: Các tiện ích
- `app/res/`: Tài nguyên ứng dụng
  - `drawable/`: Hình ảnh và icon
  - `layout/`: File XML layout giao diện
  - `values/`: Strings, colors, styles
- `app/assets/`: File tài nguyên bổ sung

## 4. Hướng dẫn sử dụng

### 4.1. Tạo và chạy ứng dụng
1. Mở Android Studio
2. Import dự án:
   - Chọn **Open an existing Android Studio project**
   - Tìm đến thư mục `QuanLyCuaHangThoiTrang`
3. Chờ Gradle sync hoàn tất
4. Chọn thiết bị ảo (AVD) hoặc thiết bị thật
5. Nhấn nút **Run** (biểu tượng tam giác xanh) để build và deploy ứng dụng

### 4.2. Tài khoản đăng nhập
Ứng dụng có sẵn 2 tài khoản:

**Quản trị viên:**
- Email: [EMAIL_ADDRESS]`
- Password: `123456`

**Khách hàng:**
- Email: [EMAIL_ADDRESS]`
- Password: `123456`

### 4.3. Các chức năng chính

**Giao diện Quản trị (Admin):**
- **Quản lý danh mục:** Thêm, sửa, xóa danh mục sản phẩm
- **Quản lý sản phẩm:**
  - Thêm sản phẩm mới
  - Sửa thông tin sản phẩm
  - Xóa sản phẩm
  - Xem chi tiết sản phẩm
- **Quản lý đơn hàng:**
  - Xem danh sách đơn hàng
  - Cập nhật trạng thái đơn hàng (Chờ xác nhận, Đang giao, Hoàn thành, Hủy)
- **Quản lý voucher:**
  - Tạo voucher mới
  - Cập nhật voucher
  - Xóa voucher

**Giao diện Khách hàng (User):**
- **Trang chủ:** Xem danh sách danh mục và sản phẩm
- **Chi tiết sản phẩm:** Xem mô tả, đánh giá, thêm vào giỏ hàng
- **Giỏ hàng:**
  - Chọn sản phẩm
  - Áp dụng mã giảm giá
  - Đặt hàng
- **Quản lý đơn hàng:** Xem lịch sử đơn hàng
- **Đánh giá sản phẩm:** Viết đánh giá cho sản phẩm đã mua

## 5. Công nghệ sử dụng

- **Ngôn ngữ lập trình:** Java 8
- **IDE:** Android Studio
- **Database:** SQLite tích hợp trong ứng dụng
- **UI Component:** RecyclerView, CardView, TabLayout, NavigationView
- **Thư viện:**
  - Material Components for Android
  - Android Support Libraries

## 6. Cấu trúc Database

Các bảng chính trong Database:

1. **users** - Tài khoản người dùng
2. **categories** - Danh mục sản phẩm
3. **products** - Sản phẩm
4. **product_images** - Hình ảnh sản phẩm
5. **cart_items** - Giỏ hàng
6. **invoices** - Hóa đơn
7. **invoice_details** - Chi tiết hóa đơn
8. **orders** - Đơn hàng
9. **order_details** - Chi tiết đơn hàng
10. **vouchers** - Voucher giảm giá
11. **reviews** - Đánh giá sản phẩm

## 7. Test và Debug

- Để kiểm tra ứng dụng, bạn có thể sử dụng Android Emulator hoặc kết nối thiết bị thật qua USB
- Sử dụng Logcat trong Android Studio để theo dõi logs và debug lỗi
- Kiểm tra database bằng SQLite Database Browser nếu cần

## 8. Cập nhật và phát triển

Các tính năng có thể phát triển thêm:
- Thêm giỏ hàng cho admin
- Tìm kiếm và lọc sản phẩm nâng cao
- Thanh toán trực tuyến
- Notification cho đơn hàng mới
- Quản lý kho hàng chi tiết

---

**Email hỗ trợ:** [EMAIL_ADDRESS]`
**Phiên bản:** 1.0
**Ngày cập nhật:** 2026-06-18
