# BÁO CÁO BÀI TẬP LỚN
**MÔN HỌC: LẬP TRÌNH THIẾT BỊ DI ĐỘNG**
**ĐỀ TÀI: XÂY DỰNG ỨNG DỤNG QUẢN LÝ CỬA HÀNG THỜI TRANG TRÊN NỀN TẢNG ANDROID**

---

## CHƯƠNG 1: TỔNG QUAN VỀ ĐỀ TÀI

### 1.1. Lý do chọn đề tài
Trong những năm gần đây, sự phát triển bùng nổ của mạng Internet và các thiết bị di động thông minh (Smartphone) đã làm thay đổi hoàn toàn thói quen sinh hoạt và mua sắm của con người. Thương mại điện tử (E-commerce) đang trở thành một xu hướng tất yếu trên toàn cầu cũng như tại Việt Nam. Thay vì phải mất thời gian đến tận các cửa hàng truyền thống, người tiêu dùng ngày nay ưu tiên việc lựa chọn, đặt mua hàng hóa ngay trên chiếc điện thoại của mình một cách nhanh chóng và tiện lợi.
Đặc biệt trong lĩnh vực kinh doanh thời trang – một ngành hàng có tính cạnh tranh rất cao và sự thay đổi mẫu mã liên tục – việc quản lý cửa hàng theo phương pháp thủ công bằng sổ sách hay Excel bộc lộ rất nhiều hạn chế. Các chủ cửa hàng gặp khó khăn trong việc kiểm soát số lượng hàng tồn kho, theo dõi doanh thu hàng ngày, quản lý thông tin khách hàng và tình trạng các đơn đặt hàng. Việc sai sót dữ liệu thường xuyên xảy ra gây thất thoát về kinh tế và làm giảm uy tín của cửa hàng.
Nhận thức được nhu cầu thực tế đó, cùng với những kiến thức đã tiếp thu được trong môn học Lập trình thiết bị di động, nhóm chúng em quyết định chọn đề tài: **"Xây dựng ứng dụng Quản lý cửa hàng thời trang trên nền tảng di động Android"**. Ứng dụng được kỳ vọng sẽ giải quyết đồng thời hai bài toán: cung cấp cho người tiêu dùng một công cụ mua sắm trực tuyến thân thiện, tiện ích; và cung cấp cho người quản trị cửa hàng một hệ thống phần mềm quản lý sản phẩm, đơn hàng, doanh thu một cách tự động, chính xác và chuyên nghiệp.

### 1.2. Mục tiêu của đề tài
#### 1.2.1. Mục tiêu tổng quát
Nghiên cứu, thiết kế và triển khai thành công một ứng dụng bán hàng và quản lý cửa hàng thời trang hoàn chỉnh trên hệ điều hành Android. Ứng dụng phải hoạt động mượt mà, giao diện thân thiện với người dùng và đảm bảo được tính logic trong quy trình nghiệp vụ mua bán hàng hóa.

#### 1.2.2. Mục tiêu cụ thể
Về mặt lý thuyết:
- Tìm hiểu và nắm vững kiến trúc của hệ điều hành Android, vòng đời của một ứng dụng (Activity Lifecycle).
- Vận dụng thành thạo ngôn ngữ lập trình Java hướng đối tượng.
- Nắm bắt cách thiết kế giao diện (UI/UX) theo chuẩn Material Design của Google.
- Nghiên cứu và áp dụng hệ quản trị cơ sở dữ liệu SQLite để lưu trữ và truy vấn dữ liệu cục bộ trên thiết bị di động.

Về mặt thực tiễn (Xây dựng phần mềm):
- **Đối với Khách hàng (User):** Xây dựng hệ thống cho phép người dùng đăng ký, đăng nhập tài khoản. Người dùng có thể lướt xem các danh mục sản phẩm, xem chi tiết mô tả và hình ảnh quần áo, thêm vào giỏ hàng, áp dụng các mã khuyến mãi (Voucher) để giảm giá, đặt hàng và theo dõi trạng thái đơn hàng của mình. Khách hàng cũng có thể để lại đánh giá (review) cho sản phẩm sau khi mua.
- **Đối với Quản trị viên (Admin):** Xây dựng phân hệ quản trị độc lập giúp người dùng có quyền Admin dễ dàng theo dõi thống kê doanh thu qua biểu đồ. Admin có toàn quyền Thêm/Sửa/Xóa đối với danh mục và sản phẩm, cập nhật số lượng tồn kho. Đặc biệt là khả năng duyệt đơn hàng, thay đổi trạng thái giao hàng và quản lý danh sách tài khoản khách hàng hệ thống.

### 1.3. Đối tượng và phạm vi nghiên cứu
- **Đối tượng nghiên cứu:** Nền tảng phát triển ứng dụng di động Android (Android SDK), ngôn ngữ lập trình Java, hệ quản trị cơ sở dữ liệu SQLite.
- **Phạm vi ứng dụng:** Ứng dụng nhắm tới mô hình các cửa hàng bán lẻ thời trang vừa và nhỏ. Do giới hạn về thời gian thực hiện, hệ thống hiện tại sử dụng cơ sở dữ liệu lưu trữ cục bộ (Local Database - SQLite) thay vì máy chủ đám mây, và mô phỏng quá trình thanh toán (thanh toán khi nhận hàng - COD) chứ chưa tích hợp các cổng thanh toán ví điện tử hay ngân hàng nội địa thật.

---

## CHƯƠNG 2: CƠ SỞ LÝ THUYẾT VÀ CÔNG NGHỆ

### 2.1. Tổng quan về nền tảng Android
Android là một hệ điều hành dựa trên nền tảng Linux được thiết kế dành cho các thiết bị di động có màn hình cảm ứng như điện thoại thông minh và máy tính bảng. Ban đầu, Android được phát triển bởi Android, Inc., với sự hỗ trợ tài chính từ Google và sau này được chính Google mua lại. 
Kiến trúc của Android bao gồm 4 tầng chính: 
1. **Linux Kernel (Nhân Linux):** Quản lý phần cứng, bộ nhớ, tiến trình và bảo mật.
2. **Libraries & Android Runtime:** Chứa các thư viện lõi (C/C++) như SQLite, OpenGL, WebKit. Android Runtime (ART/Dalvik) chịu trách nhiệm biên dịch và thực thi mã ứng dụng.
3. **Application Framework:** Cung cấp các hàm API bằng Java để lập trình viên sử dụng (Quản lý Activity, Location, Resource, Notification).
4. **Applications (Tầng Ứng dụng):** Nơi chứa các ứng dụng gốc của hệ thống và các ứng dụng do lập trình viên bên thứ ba (như ứng dụng Quản lý cửa hàng thời trang trong đề tài này) cài đặt vào.

### 2.2. Ngôn ngữ lập trình Java
Đề tài sử dụng Java làm ngôn ngữ lập trình chính để xử lý toàn bộ logic của hệ thống. Java là một ngôn ngữ lập trình hướng đối tượng (OOP) cực kỳ mạnh mẽ, có tính bảo mật cao và độc lập với nền tảng phần cứng. Đặc tính quan trọng nhất của Java là "Viết một lần, chạy mọi nơi" (Write Once, Run Anywhere). Trong phát triển Android truyền thống, Java là ngôn ngữ được hỗ trợ nền tảng vững chắc nhất, với hệ sinh thái thư viện phong phú và cộng đồng hỗ trợ cực kỳ rộng lớn, rất thuận lợi cho việc xử lý các luồng dữ liệu phức tạp của một ứng dụng thương mại điện tử.

### 2.3. Hệ quản trị cơ sở dữ liệu SQLite
SQLite là một thư viện phần mềm cung cấp một hệ quản trị cơ sở dữ liệu quan hệ nhỏ gọn, nhúng trực tiếp vào trong ứng dụng mà không cần phải cài đặt hay cấu hình một máy chủ CSDL (Serverless) độc lập nào. 
**Lý do lựa chọn SQLite cho đề tài:**
- Nhẹ và nhanh: Không tiêu tốn nhiều tài nguyên của thiết bị di động.
- Không cần cấu hình: Dữ liệu được lưu thẳng vào một file duy nhất trên bộ nhớ máy, rất thích hợp cho các bài tập lớn yêu cầu ứng dụng chạy độc lập (offline).
- Hỗ trợ cú pháp SQL chuẩn: Dễ dàng thực hiện các câu lệnh truy vấn phức tạp (JOIN, GROUP BY) để phục vụ việc tính toán giỏ hàng, thống kê doanh thu.

### 2.4. Công cụ phát triển Android Studio
Android Studio là Môi trường Phát triển Tích hợp (IDE) chính thức dành cho phát triển ứng dụng Android, dựa trên IntelliJ IDEA. Công cụ này cung cấp:
- Trình biên dịch Gradle linh hoạt, hỗ trợ quản lý thư viện dễ dàng.
- Bộ mô phỏng Android Emulator giúp chạy thử nghiệm ứng dụng trên nhiều cấu hình thiết bị ảo khác nhau mà không cần thiết bị thật.
- Trình thiết kế giao diện (Layout Editor) trực quan kéo - thả, hỗ trợ thiết kế file XML nhanh chóng.

### 2.5. Các thành phần giao diện nâng cao (UI Components)
Để ứng dụng có tính thẩm mỹ cao và trải nghiệm mượt mà, nhóm đã áp dụng:
- **RecyclerView:** Thay thế cho ListView truyền thống, giúp hiển thị danh sách sản phẩm và đơn hàng một cách mượt mà, tái sử dụng bộ nhớ cực kì tốt, tránh tình trạng giật lag khi danh sách có hàng trăm sản phẩm.
- **CardView:** Tạo hiệu ứng nổi (elevation) và bo góc cho các khung chứa sản phẩm, tạo cảm giác trực quan và hiện đại theo triết lý Material Design.
- **Material Components:** Ứng dụng sử dụng BottomNavigationView để điều hướng các tab chính của khách hàng, TextInputLayout để bắt lỗi và hiển thị hiệu ứng khi nhập liệu form đăng ký, đăng nhập.

---

## CHƯƠNG 3: PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

### 3.1. Phân tích yêu cầu hệ thống
#### 3.1.1. Yêu cầu chức năng
Hệ thống được phân chia làm 2 phân hệ rõ rệt phục vụ cho 2 đối tượng là Khách hàng và Quản trị viên, quá trình phân luồng được tự động hóa ngay từ bước Đăng nhập.

**A. Phân hệ Khách hàng (User):**
1. **Quản lý tài khoản:** Khởi động ứng dụng, người dùng có thể Đăng ký tài khoản mới bằng cách cung cấp các thông tin cá nhân cơ bản. Sau khi Đăng nhập thành công, người dùng có thể cập nhật thông tin (Tên, Số điện thoại, Địa chỉ giao hàng mặc định) và Đổi mật khẩu.
2. **Khám phá sản phẩm:** 
   - Trang chủ hiển thị danh sách các danh mục quần áo và các sản phẩm nổi bật.
   - Tìm kiếm và lọc sản phẩm.
   - Khi click vào một sản phẩm, màn hình Chi tiết sản phẩm hiện ra cung cấp hình ảnh (có thể lướt nhiều ảnh), giá bán, mô tả chi tiết, số lượng còn trong kho và danh sách các đánh giá (Review) từ những người mua trước.
3. **Quản lý Giỏ hàng (Cart):**
   - Thêm sản phẩm vào giỏ với số lượng tùy chọn (không được vượt quá số lượng tồn kho).
   - Truy cập giỏ hàng để xem danh sách mặt hàng đã chọn, thay đổi số lượng (+/-) hoặc xóa mặt hàng ra khỏi giỏ. Tổng tiền được tính toán real-time.
4. **Thanh toán và Đặt hàng (Checkout):**
   - Khách hàng xác nhận địa chỉ giao hàng.
   - Hệ thống cho phép nhập mã Khuyến mãi (Voucher) hợp lệ. Nếu đủ điều kiện (ví dụ: Tổng đơn hàng lớn hơn mức quy định), hệ thống tự động trừ tiền chiết khấu.
   - Nhấn "Xác nhận đặt hàng", hệ thống sẽ lưu đơn hàng vào database và làm rỗng giỏ hàng.
5. **Theo dõi đơn hàng:** Người dùng xem lại Lịch sử các đơn hàng đã đặt, xem chi tiết từng đơn và biết được đơn hàng đang ở trạng thái nào (Chờ xác nhận, Đang giao, Đã hoàn thành, Đã hủy).
6. **Đánh giá (Rating):** Với những đơn hàng đã chuyển sang trạng thái "Hoàn thành", người dùng có quyền viết bình luận và chấm điểm (số sao) cho các sản phẩm trong đơn hàng đó.

**B. Phân hệ Quản trị viên (Admin):**
1. **Trang tổng quan (Dashboard):** Hiển thị các số liệu thống kê nhanh như: Tổng số khách hàng, tổng sản phẩm, số đơn hàng đang chờ duyệt.
2. **Quản lý Danh mục (Categories):** Thực hiện các tác vụ Thêm danh mục mới, Sửa tên danh mục, Xóa danh mục.
3. **Quản lý Sản phẩm (Products):**
   - Thêm sản phẩm mới (chọn danh mục, nhập tên, giá, mô tả, đính kèm hình ảnh đại diện và các ảnh phụ).
   - Chỉnh sửa thông tin sản phẩm hoặc Xóa sản phẩm.
   - **Quản lý hàng tồn kho:** Hệ thống tự động cảnh báo danh sách các mặt hàng sắp hết (Low Stock), Admin có thể vào cập nhật thêm số lượng nhập kho mới.
4. **Quản lý Đơn hàng (Orders):** 
   - Admin nhận được danh sách toàn bộ đơn hàng của tất cả khách hàng.
   - Có thể xem chi tiết khách hàng A đã mua những món gì, tổng tiền bao nhiêu.
   - Chuyển đổi trạng thái đơn hàng: Từ "Chờ xác nhận" -> "Đang giao" -> "Hoàn thành" hoặc "Hủy" nếu có sự cố.
5. **Quản lý Khuyến mãi (Voucher):** Admin tạo mã giảm giá mới (ví dụ: SALE20), cấu hình phần trăm giảm giá (20%), giá trị đơn hàng tối thiểu để được áp dụng, và ngày hết hạn của mã.
6. **Thống kê báo cáo:** Cung cấp biểu đồ trực quan (Bar Chart / Line Chart) thể hiện doanh thu theo các ngày hoặc các tháng, giúp Admin theo dõi tình hình kinh doanh trực quan nhất.

#### 3.1.2. Yêu cầu phi chức năng
- **Tính tiện dụng (Usability):** Giao diện phải trực quan, dễ thao tác kể cả với người không rành công nghệ. Bố cục không bị vỡ trên các kích thước màn hình điện thoại khác nhau.
- **Tính hiệu năng (Performance):** Tốc độ tải dữ liệu từ SQLite phải tức thời. Chuyển đổi giữa các màn hình mượt mà, không xảy ra hiện tượng đứng máy (Crash) hay ANR (Application Not Responding).
- **Tính bảo mật:** Mật khẩu của người dùng không hiển thị văn bản thuần. Dữ liệu giữa Admin và User được phân lập rõ ràng, User không thể truy cập các tính năng của Admin.

### 3.2. Thiết kế Cơ sở dữ liệu
Hệ thống sử dụng cơ sở dữ liệu quan hệ gồm 9 bảng chính được liên kết chặt chẽ với nhau:

1. **Bảng `users` (Tài khoản người dùng):**
   - `id` (INTEGER PRIMARY KEY AUTOINCREMENT)
   - `name`, `email` (UNIQUE), `password`, `phone`, `address` (TEXT)
   - `role` (TEXT): Xác định quyền là "admin" hoặc "user".

2. **Bảng `categories` (Danh mục sản phẩm):**
   - `id` (INTEGER PRIMARY KEY)
   - `name` (TEXT), `image` (TEXT - lưu đường dẫn ảnh)

3. **Bảng `products` (Sản phẩm):**
   - `id` (INTEGER PRIMARY KEY)
   - `name`, `description` (TEXT)
   - `category_id` (INTEGER): Khóa ngoại liên kết tới bảng categories.
   - `price` (REAL), `stock_quantity` (INTEGER)

4. **Bảng `product_images` (Hình ảnh phụ của sản phẩm):**
   - `id` (INTEGER PRIMARY KEY)
   - `product_id` (INTEGER): Khóa ngoại tới products.
   - `image_url` (TEXT): Cho phép 1 sản phẩm có một bộ sưu tập nhiều ảnh.

5. **Bảng `vouchers` (Mã khuyến mãi):**
   - `id` (INTEGER PRIMARY KEY)
   - `code` (TEXT UNIQUE)
   - `discount_percent` (INTEGER), `min_order_value` (REAL), `expiry_date` (TEXT)

6. **Bảng `cart_items` (Giỏ hàng tạm thời):**
   - `user_id` (INTEGER), `product_id` (INTEGER)
   - `quantity` (INTEGER)
   - PK là cặp (user_id, product_id).

7. **Bảng `orders` (Hóa đơn / Đơn hàng):**
   - `id` (INTEGER PRIMARY KEY)
   - `user_id` (INTEGER): Khóa ngoại người đặt.
   - `total_amount` (REAL)
   - `status` (TEXT): Chờ xác nhận, Đang giao, Hoàn thành, Đã hủy.
   - `order_date`, `shipping_address` (TEXT)
   - `voucher_id` (INTEGER): Khóa ngoại (nếu có sử dụng voucher).

8. **Bảng `order_details` (Chi tiết hóa đơn):**
   - `order_id` (INTEGER), `product_id` (INTEGER)
   - `quantity` (INTEGER), `price` (REAL): Lưu giá tại thời điểm mua để không bị ảnh hưởng nếu sau này Admin đổi giá sản phẩm.

9. **Bảng `reviews` (Đánh giá):**
   - `id` (INTEGER PRIMARY KEY)
   - `user_id`, `product_id` (INTEGER)
   - `rating` (INTEGER 1-5 sao), `comment`, `date` (TEXT).

---

## CHƯƠNG 4: KẾT QUẢ THỰC NGHIỆM VÀ ĐÁNH GIÁ

*(Ghi chú cho sinh viên: Tại chương này, bạn copy nội dung mô tả dưới đây vào Word, sau mỗi mục hãy bấm `Enter` và dán hình ảnh chụp màn hình tương ứng từ điện thoại hoặc máy ảo Android của bạn vào)*

### 4.1. Kết quả giao diện chương trình

**4.1.1. Màn hình Khởi động và Xác thực**
- **Màn hình Splash & Onboarding:** Ứng dụng khởi động với Logo cửa hàng bắt mắt. Tiếp theo là các màn hình trượt (Onboarding) giới thiệu ngắn gọn các tính năng nổi bật của ứng dụng.
*(Chèn ảnh Splash Screen / Onboarding tại đây)*

- **Màn hình Đăng nhập / Đăng ký:** Giao diện form nhập liệu được thiết kế bo góc, sử dụng TextInputLayout để hiển thị thông báo lỗi trực tiếp dưới ô nhập liệu (ví dụ: "Email không đúng định dạng", "Mật khẩu quá ngắn"). Hệ thống sẽ tự động kiểm tra Role trong DB và chuyển hướng vào Admin Dashboard hoặc User Home tương ứng.
*(Chèn ảnh Login / Register tại đây)*

**4.1.2. Phân hệ giao diện Khách hàng (User)**
- **Màn hình Trang chủ (User Home):** Gồm một thanh tìm kiếm trên cùng, tiếp theo là phần danh mục hiển thị dạng thanh cuộn ngang (Horizontal RecyclerView). Bên dưới là danh sách các sản phẩm mới nhất được bố trí dạng lưới (Grid) với hình ảnh, tên và mức giá.
*(Chèn ảnh Trang chủ User tại đây)*

- **Màn hình Chi tiết sản phẩm:** Trực quan với hình ảnh sản phẩm lớn phía trên. Bên dưới có hiển thị số lượng tồn kho để người dùng ra quyết định. Khu vực đánh giá hiển thị các sao vàng (RatingBar) và bình luận từ người dùng khác. Dưới cùng là nút bấm lớn "Thêm vào giỏ hàng".
*(Chèn ảnh Chi tiết sản phẩm tại đây)*

- **Màn hình Giỏ hàng và Thanh toán:** Liệt kê các item đã chọn kèm nút tăng giảm số lượng (+/-). Ô nhập mã Voucher được tích hợp sẵn chức năng "Áp dụng" (Apply) với việc tính toán lại tổng tiền tức thời ngay trên màn hình.
*(Chèn ảnh Giỏ hàng / Đặt hàng tại đây)*

- **Màn hình Lịch sử đơn hàng:** Các đơn hàng được hiển thị theo dạng thẻ (Card), có đánh dấu màu sắc cho từng trạng thái (Màu vàng: Chờ xác nhận, Màu xanh lá: Đã hoàn thành).
*(Chèn ảnh Lịch sử đơn hàng tại đây)*

**4.1.3. Phân hệ giao diện Quản trị viên (Admin)**
- **Màn hình Bảng điều khiển (Admin Dashboard):** Menu chính của Admin phân chia các chức năng quản lý qua các Card lớn, giúp Admin thao tác thuận tiện.
*(Chèn ảnh Admin Dashboard tại đây)*

- **Màn hình Quản lý Sản phẩm:** Danh sách toàn bộ mặt hàng cửa hàng đang có. Hỗ trợ nút Thêm mới (dấu +) ở góc dưới màn hình (Floating Action Button). Khi thêm mới, cho phép chụp ảnh hoặc tải ảnh từ thư viện máy. 
*(Chèn ảnh Danh sách sản phẩm / Thêm sản phẩm tại đây)*

- **Màn hình Quản lý Đơn hàng:** Hiển thị chi tiết khách A đặt mua gì. Admin có một danh sách dropdown (Spinner) để Cập nhật trạng thái chuyển phát của đơn hàng đó.
*(Chèn ảnh Quản lý đơn hàng Admin tại đây)*

- **Màn hình Thống kê (Statistic):** Vẽ biểu đồ cột thể hiện các chỉ số kinh doanh trong các ngày hoặc các tháng, một công cụ đắc lực hỗ trợ ra quyết định kinh doanh.
*(Chèn ảnh Biểu đồ thống kê tại đây)*

### 4.2. Đánh giá kết quả đạt được
Ứng dụng đã hoàn thành 100% các mục tiêu cốt lõi đề ra ở Chương 1. Những ưu điểm nổi bật bao gồm:
- Hoạt động cực kỳ ổn định, tốc độ load dữ liệu nhanh chóng do thuật toán truy vấn SQLite được tối ưu và thiết kế Database chuẩn hóa.
- Giao diện UI/UX rất được trau chuốt, màu sắc hài hòa, tuân thủ đúng chuẩn Material Design tạo cảm giác đây là một ứng dụng thương mại điện tử thực tế.
- Các tính năng nâng cao (Logic tính toán giỏ hàng, Kiểm tra tồn kho trước khi đặt, Áp dụng Voucher giảm giá, Cập nhật trạng thái đơn và Thống kê bằng biểu đồ) hoạt động hoàn hảo, đảm bảo tính chặt chẽ trong nghiệp vụ bán lẻ.

### 4.3. Những mặt hạn chế
Bên cạnh những kết quả đạt được, do giới hạn về mặt thời gian và cơ sở hạ tầng, hệ thống vẫn còn một số điểm cần cải thiện:
- Hệ thống cơ sở dữ liệu hiện tại đang là SQLite (Local Database), điều này có nghĩa là ứng dụng chỉ lưu trữ trên chính máy chạy app đó. Không thể chia sẻ dữ liệu thực tế giữa máy khách hàng A và máy Admin B qua mạng internet thật.
- Ứng dụng chưa liên kết được với các API của bên thứ ba để thực hiện thanh toán điện tử (như VNPay, Momo) hay API giao hàng (Giao Hàng Nhanh).

### 4.4. Hướng phát triển trong tương lai
Để ứng dụng có thể triển khai thực tế trên Google Play Store, nhóm đề xuất các hướng phát triển tiếp theo:
1. **Chuyển đổi Cơ sở dữ liệu:** Nâng cấp từ SQLite sang CSDL đám mây (Cloud Database) như Firebase Realtime Database / Firestore, hoặc xây dựng hệ thống RESTful API kết nối với máy chủ MySQL/NodeJS để dữ liệu được đồng bộ hóa tức thời trên toàn cầu.
2. **Tích hợp cổng thanh toán trực tuyến:** Thêm SDK của Momo, ZaloPay hoặc Stripe để đa dạng hóa phương thức thanh toán.
3. **Tích hợp Dịch vụ đám mây:** Cấu hình Firebase Cloud Messaging (FCM) để đẩy thông báo (Push Notifications) về máy khách hàng ngay khi đơn hàng được Admin xác nhận giao.
4. **Trí tuệ nhân tạo (AI):** Bổ sung thuật toán gợi ý sản phẩm liên quan dựa trên lịch sử mua sắm của khách hàng để tăng tỷ lệ chốt đơn.

---
**KẾT LUẬN**
Quá trình thực hiện bài tập lớn "Quản lý cửa hàng thời trang" đã giúp nhóm củng cố vững chắc nền tảng lập trình Android và Java. Việc đối mặt với các bài toán logic thực tế (giỏ hàng, tồn kho, phân quyền) đã rèn luyện tư duy phân tích hệ thống và kỹ năng giải quyết vấn đề. Dù còn một số hạn chế về mặt hạ tầng mạng, nhưng sản phẩm thu được hoàn toàn đáp ứng được các tiêu chí của môn học và có tiềm năng rất lớn để mở rộng thành một dự án thương mại trong tương lai.
