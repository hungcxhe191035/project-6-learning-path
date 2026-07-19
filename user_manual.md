# TÀI LIỆU HƯỚNG DẪN SỬ DỤNG
## HỆ THỐNG MY LEARNING PATH

---

## 1. OVERVIEW (TỔNG QUAN HỆ THỐNG)

Hệ thống **My Learning Path** là một nền tảng học tập trực tuyến kết nối trực tiếp giữa **Học viên** và **Giảng viên** dưới sự giám sát và điều phối của **Quản trị viên**. Hệ thống tích hợp ví tiền cá nhân giúp các giao dịch mua bán khóa học, nạp và rút tiền được diễn ra nhanh chóng, bảo mật và thuận tiện.

Hệ thống phân chia rõ ràng ba nhóm vai trò sử dụng:
*   **Học viên**: Đăng ký tài khoản, nạp tiền vào ví cá nhân thông qua cổng thanh toán ngân hàng, tìm kiếm, mua khóa học và tham gia học tập các bài giảng. Học viên có đủ năng lực có thể nộp đơn đăng ký để trở thành Giảng viên.
*   **Giảng viên**: Soạn thảo bài giảng, đăng ký khóa học, thiết lập mức giá, tạo mã giảm giá riêng cho khóa học của mình, theo dõi doanh thu được chia sẻ và thực hiện rút tiền trực tiếp về tài khoản ngân hàng cá nhân.
*   **Quản trị viên (Admin)**: Kiểm soát và quản lý toàn bộ hệ thống. Quản trị viên phê duyệt đơn đăng ký giảng viên, kiểm duyệt chất lượng và tạm khóa khóa học, quản lý danh sách người dùng (cấp quyền, khóa tài khoản), giám sát lịch sử giao dịch tiền, cấu hình tỷ lệ chia sẻ doanh thu và quản trị danh mục nhãn chủ đề.

---

## 2. USER MANUAL (HƯỚNG DẪN SỬ DỤNG CHI TIẾT)

### 2.1. Dashboard (Bảng điều khiển tổng quan dành cho Quản trị viên)

#### 2.1.1. Mục đích
Cung cấp cho Quản trị viên cái nhìn toàn cảnh về tình hình kinh doanh, số lượng tài khoản mới đăng ký, doanh thu tích lũy và hiệu suất hoạt động của hệ thống thông qua các chỉ số và biểu đồ thống kê trực quan được cập nhật liên tục.

#### 2.1.2. Hướng dẫn thao tác chi tiết
1.  **Lọc dữ liệu theo thời gian**:
    *   Tại màn hình chính, ở góc trên bên phải, nhấn vào thanh chọn bộ lọc thời gian.
    *   Lựa chọn khoảng thời gian cần báo cáo:
        *   **Hôm nay**: Thống kê phát sinh trong ngày hiện tại.
        *   **7 ngày qua**: Chu kỳ thống kê 7 ngày gần nhất.
        *   **30 ngày qua**: Chu kỳ thống kê 30 ngày gần nhất (Mặc định).
        *   **Năm nay**: Tổng hợp từ ngày đầu tiên của năm hiện tại đến nay.
        *   **Tất cả thời gian**: Tổng số liệu lũy kế từ khi hệ thống bắt đầu vận hành.
2.  **Xem các chỉ số đo lường hiệu năng**:
    *   **Tổng doanh thu**: Số tiền thu được từ tất cả các đơn mua khóa học đã thanh toán thành công.
    *   **Tổng số học viên / Giảng viên**: Số lượng tài khoản thuộc từng vai trò đang hoạt động trên hệ thống.
    *   **Tổng số khóa học**: Tổng số khóa học hiện có.
    *   **Số đơn giảng viên chờ duyệt**: Số lượng hồ sơ xin làm giảng viên đang chờ xử lý.
    *   **Số khóa học chờ duyệt**: Số lượng bài giảng mới hoặc phiên bản cập nhật cần được kiểm duyệt nội dung.
3.  **Đọc biểu đồ thống kê**:
    *   **Biểu đồ xu hướng doanh thu**: Thể hiện sự biến động doanh thu theo từng mốc ngày cụ thể. Kể cả những ngày không phát sinh đơn hàng, hệ thống vẫn hiển thị mức doanh thu là 0 đồng để đảm bảo biểu đồ diễn ra liên tục, giúp Quản trị viên đánh giá xu hướng chính xác.
    *   **Biểu đồ phân bổ nhãn học tập**: Thể hiện tỷ lệ học viên đăng ký khóa học dựa theo các nhãn chủ đề. Quản trị viên có thể di chuột vào từng phần trên biểu đồ để xem số lượng và tỷ lệ phần trăm cụ thể nhằm biết được lĩnh vực nào đang thu hút nhất.
4.  **Xem danh sách xếp hạng & giao dịch mới nhất**:
    *   Xem danh sách các khóa học bán chạy nhất, danh sách giảng viên xuất sắc, và các hóa đơn giao dịch hoặc yêu cầu mới nhất cần xử lý gấp.

---

### 2.2. Quản lý Người dùng (Dành cho Quản trị viên)

#### 2.2.1. Mục đích
Hỗ trợ Quản trị viên theo dõi thông tin chi tiết, tìm kiếm nhanh, gán vai trò, khóa hoặc mở khóa hoạt động tài khoản của toàn bộ thành viên trong hệ thống.

#### 2.2.2. Hướng dẫn thao tác chi tiết
1.  **Tìm kiếm và lọc danh sách thành viên**:
    *   Truy cập mục quản lý người dùng từ menu điều hướng.
    *   Nhập Họ tên hoặc Email cần tìm vào ô tìm kiếm (hệ thống hỗ trợ tìm kiếm không phân biệt chữ hoa hay chữ thường).
    *   Sử dụng bộ lọc vai trò để phân loại nhanh danh sách theo nhóm học viên hoặc giảng viên.
    *   Sử dụng bộ lọc trạng thái để tìm kiếm tài khoản đang hoạt động hoặc tài khoản đang bị khóa.
    *   Bấm nút tìm kiếm để hiển thị kết quả. Có thể chuyển trang ở cuối bảng dữ liệu nếu danh sách dài.
2.  **Xem chi tiết thông tin người dùng**:
    *   Nhấp vào nút xem chi tiết ở dòng thông tin người dùng tương ứng.
    *   Màn hình sẽ hiển thị hồ sơ cá nhân, liên kết mạng xã hội, thông tin ngân hàng nhận tiền, số dư ví hiện tại và toàn bộ lịch sử nạp tiền, rút tiền, mua khóa học của tài khoản đó.
3.  **Khóa hoặc Mở khóa tài khoản**:
    *   **Thao tác đơn lẻ**: Tại danh sách người dùng hoặc trong trang chi tiết, click nút khóa để tạm dừng hoạt động của tài khoản vi phạm. Click nút mở khóa để tài khoản hoạt động bình thường trở lại.
    *   **Thao tác hàng loạt**:
        *   Tích chọn các ô vuông đầu dòng của những người dùng cần xử lý. Tích chọn ô vuông ở đầu tiêu đề bảng để chọn tất cả người dùng hiển thị trên trang hiện tại.
        *   Thanh tác vụ hàng loạt sẽ xuất hiện ở phía dưới màn hình.
        *   Click nút khóa hàng loạt hoặc mở khóa hàng loạt.
        *   Xác nhận đồng ý trong thông báo hiển thị để thực hiện.
    *   *Ngăn ngừa tự khóa tài khoản*: Để tránh trường hợp Quản trị viên vô tình tự khóa tài khoản của chính mình, hệ thống sẽ vô hiệu hóa ô chọn và nút khóa đối với tài khoản của Quản trị viên đang đăng nhập.
4.  **Đổi vai trò người dùng**:
    *   Tại dòng thông tin của người dùng mong muốn, nhấp nút đổi vai trò.
    *   Lựa chọn vai trò mới là học viên hoặc giảng viên rồi bấm lưu. Hệ thống không cho phép chuyển đổi vai trò hoặc gán quyền thành quản trị viên tại màn hình này để bảo mật vai trò cao nhất của hệ thống.
5.  **Tạo tài khoản thủ công**:
    *   Nhấp nút thêm người dùng mới.
    *   Điền đầy đủ thông tin: Email (chưa từng đăng ký trên hệ thống), Mật khẩu, Họ tên, Số điện thoại, Vai trò (Học viên hoặc Giảng viên) và Trạng thái ban đầu.
    *   Nhấp lưu. Hệ thống sẽ mã hóa bảo mật mật khẩu và tự động tạo một ví tiền cá nhân đi kèm với tài khoản này với số dư ban đầu là 0 đồng.

---

### 2.3. Duyệt Giảng viên (Dành cho Quản trị viên)

#### 2.3.1. Mục đích
Xem xét thông tin năng lực, hồ sơ cá nhân và tệp tin giới thiệu bản thân (CV) đính kèm của học viên gửi lên để quyết định phê duyệt nâng cấp lên làm giảng viên hoặc từ chối đơn.

#### 2.3.2. Hướng dẫn thao tác chi tiết
1.  **Xem danh sách đơn đăng ký**:
    *   Truy cập mục duyệt đơn giảng viên trên trang quản trị.
    *   Danh sách mặc định hiển thị các đơn đăng ký đang chờ xử lý, xếp theo thời gian nộp từ mới nhất đến cũ nhất.
2.  **Kiểm tra hồ sơ ứng viên**:
    *   Nhấp vào nút xem chi tiết đơn đăng ký.
    *   Hệ thống sẽ hiển thị các thông tin bao gồm: Tiêu đề chuyên môn, Tiểu sử, Động lực giảng dạy, liên kết mạng xã hội để đối chiếu và các nhãn chủ đề đăng ký dạy.
    *   Nhấp vào liên kết hồ sơ đính kèm (dạng tệp tin PDF) để xem trực tiếp hoặc tải về máy tính để thẩm định chi tiết.
3.  **Phê duyệt đơn**:
    *   Nếu hồ sơ đạt yêu cầu, nhập nhận xét của quản trị viên (ví dụ: "Thông tin rõ ràng, hồ sơ phù hợp").
    *   Nhấn nút Phê duyệt và xác nhận.
    *   **Kết quả**: Hệ thống tự động nâng cấp vai trò của người dùng này từ học viên lên thành giảng viên. Giao diện của người dùng sẽ nhận được thông báo chúc mừng và hệ thống sẽ gửi một thư điện tử thông báo kết quả chi tiết đến hòm thư của giảng viên mới.
4.  **Từ chối đơn**:
    *   Nếu hồ sơ không đạt yêu cầu, Quản trị viên bắt buộc phải điền lý do từ chối vào ô nhận xét (ví dụ: "Tệp tin hồ sơ bị mờ, vui lòng nộp lại bản rõ nét hơn").
    *   Nhấn nút Từ chối và xác nhận.
    *   **Kết quả**: Đơn được chuyển sang trạng thái bị từ chối. Người dùng vẫn giữ nguyên vai trò là học viên. Hệ thống tự động gửi thông báo trên trang cá nhân của học viên và gửi một thư điện tử nêu rõ lý do từ chối để học viên biết cách điều chỉnh khi nộp lại đơn sau này.

---

### 2.4. Quản lý Tag (Quản lý Nhãn chủ đề - Dành cho Quản trị viên)

#### 2.4.1. Mục đích
Quản lý các nhãn chủ đề của hệ thống (ví dụ: Java, Python, Thiết kế đồ họa...) giúp phân loại khóa học và định hướng chuyên môn giảng dạy của giảng viên.

#### 2.4.2. Hướng dẫn thao tác chi tiết
Giao diện quản lý nhãn chủ đề được chia làm hai phần:
1.  **Thêm mới nhãn**:
    *   Ở khung thông tin bên trái màn hình, nhập tên nhãn mới và mô tả ngắn gọn. Tên nhãn là duy nhất và không được trùng lặp.
    *   Bấm nút lưu để hoàn thành. Nhãn mới sẽ hiển thị ở danh sách bên phải.
    *   *Cơ chế tự động khôi phục nhãn*: Nếu quản trị viên thêm một nhãn chủ đề có tên trùng với nhãn đã từng bị xóa trước đó, hệ thống sẽ tự động khôi phục nhãn cũ này và cập nhật lại mô tả mới, giúp tránh phát sinh lỗi trùng tên nhãn và giữ sạch dữ liệu.
2.  **Chỉnh sửa nhãn**:
    *   Tại danh sách nhãn ở phía bên phải, nhấp nút chỉnh sửa tại dòng của nhãn cần cập nhật.
    *   Thông tin của nhãn sẽ hiển thị ở khung nhập liệu bên trái.
    *   Tiến hành chỉnh sửa thông tin mô tả hoặc tên nhãn rồi bấm nút cập nhật để lưu lại.
3.  **Xóa nhãn**:
    *   Tại danh sách nhãn, click vào biểu tượng thùng rác ở dòng của nhãn cần xóa.
    *   Nhấp đồng ý trên thông báo xác nhận.
    *   **Cơ chế an toàn**: Để tránh lỗi hệ thống khi nhãn đang được gắn cho các khóa học hiện tại hoặc các đơn đăng ký giảng viên đang xử lý, hệ thống sẽ tự động dọn sạch toàn bộ các liên kết liên quan của nhãn này trước khi đánh dấu xóa nhãn ra khỏi danh sách hiển thị hoạt động.

---

### 2.5. Quản lý Khóa học (Dành cho Quản trị viên)

#### 2.5.1. Mục đích
Giúp quản trị viên kiểm duyệt chất lượng bài giảng của giảng viên, tạm khóa các khóa học vi phạm chính sách của nền tảng và mở khóa hoặc xóa khóa học khi cần thiết.

#### 2.5.2. Hướng dẫn thao tác chi tiết
1.  **Tìm kiếm và lọc khóa học**:
    *   Truy cập mục quản lý khóa học.
    *   Tìm kiếm khóa học theo tên khóa học hoặc theo tên giảng viên sở hữu.
    *   Sử dụng bộ lọc để phân loại theo trạng thái khóa học (Chờ duyệt, Đã xuất bản, Bản nháp) hoặc theo trạng thái khóa (Đang bị khóa, Đang hoạt động).
    *   Bấm tìm kiếm để xem kết quả.
2.  **Xem chi tiết giáo trình khóa học**:
    *   Nhấp chọn khóa học cụ thể để xem chi tiết.
    *   Danh sách chương trình học hiển thị rõ ràng theo danh sách phân cấp. Quản trị viên nhấp vào tiêu đề từng chương học để mở rộng xem các bài học cụ thể bên trong nhằm đánh giá cấu trúc bài học, thời lượng video học tập hoặc tài liệu tham khảo đính kèm.
3.  **Tạm khóa khóa học vi phạm**:
    *   Nếu phát hiện khóa học vi phạm chính sách hoặc chứa nội dung không phù hợp, nhấp nút tạm khóa.
    *   Một cửa sổ yêu cầu nhập lý do tạm khóa sẽ xuất hiện (Quản trị viên bắt buộc phải nhập lý do).
    *   Bấm xác nhận để khóa khóa học.
    *   **Kết quả**: Khóa học sẽ bị ẩn hoàn toàn khỏi danh mục tìm kiếm của hệ thống và trang chủ học viên, ngăn không cho học viên mới mua khóa học này.
    *   *Bảo đảm quyền lợi học viên cũ*: Những học viên đã thanh toán mua khóa học này trước thời điểm khóa học bị khóa vẫn được quyền truy cập học tập bình thường.
4.  **Mở khóa khóa học**:
    *   Sau khi giảng viên đã chỉnh sửa nội dung vi phạm, Quản trị viên có thể bấm nút mở khóa để khóa học hiển thị và bán bình thường trở lại.
5.  **Xóa khóa học**:
    *   Quản trị viên bấm nút xóa để loại bỏ khóa học khỏi hệ thống nếu nội dung không còn phù hợp.

---

### 2.6. Quản lý Ví và Giao dịch (Dành cho Quản trị viên)

#### 2.6.1. Mục đích
Giúp Quản trị viên giám sát dòng tiền của hệ thống, kiểm tra lịch sử nạp/rút/thanh toán của các thành viên và cấu hình tỷ lệ chia sẻ doanh thu tự động giữa giảng viên và hệ thống.

#### 2.6.2. Hướng dẫn thao tác chi tiết
1.  **Kiểm tra lịch sử giao dịch**:
    *   Truy cập mục quản lý ví và giao dịch trên trang quản trị.
    *   Bảng hiển thị chi tiết mọi hoạt động dòng tiền gồm: Mã giao dịch, Tên tài khoản, Số tiền, Loại giao dịch (Nạp tiền, Rút tiền, Thanh toán mua khóa học), Trạng thái giao dịch (Thành công, Thất bại, Chờ xử lý) và thời gian thực hiện.
    *   **Phân biệt màu sắc trực quan**:
        *   Các giao dịch nạp tiền vào hệ thống được hiển thị với dấu cộng màu xanh lá cây (Ví dụ: `+ 200.000đ`).
        *   Các giao dịch dòng tiền đi ra như rút tiền hoặc thanh toán khóa học được hiển thị với dấu trừ màu đỏ (Ví dụ: `- 450.000đ`), giúp quản trị viên dễ dàng đối soát.
2.  **Cấu hình tỷ lệ chia sẻ doanh thu cho Giảng viên**:
    *   Tại màn hình quản lý ví, nhập số phần trăm mong muốn vào ô cấu hình tỷ lệ chia sẻ cho giảng viên (giá trị từ 0 đến 100%).
        *   *Ví dụ*: Nhập `80` nghĩa là giảng viên nhận 80% doanh thu thực tế từ bán khóa học, hệ thống thu phí vận hành sàn là 20%.
    *   Bấm lưu cài đặt.
    *   Sau khi lưu thành công, toàn bộ giao dịch mua khóa học kể từ lúc đó sẽ tự động áp dụng tỷ lệ chia sẻ mới này để tính toán chia tiền vào ví giảng viên và ví quản trị viên tự động.

---

### 2.7. Nạp tiền / Rút tiền (Dành cho Học viên & Giảng viên)

#### 2.7.1. Hướng dẫn Nạp tiền vào Ví cá nhân
1.  Đăng nhập tài khoản, nhấp vào ảnh đại diện cá nhân ở góc trên và chọn **Quản lý Ví**.
2.  Nhấp nút **Nạp tiền**.
3.  Nhập số tiền cần nạp (số tiền phải lớn hơn 0 đồng) và chọn cổng thanh toán ngân hàng điện tử **VNPay**. Bấm xác nhận.
4.  Hệ thống sẽ tự động dẫn hướng người dùng sang giao diện thanh toán bảo mật của cổng VNPay.
5.  Thực hiện thanh toán theo hướng dẫn trên cổng VNPay (quét mã QR hoặc nhập thông tin tài khoản thẻ ngân hàng).
6.  Sau khi thanh toán thành công, cổng thanh toán sẽ tự động quay trở về hệ thống học tập. Ví cá nhân của người dùng sẽ được tự động cộng thêm số tiền đã nạp và trạng thái giao dịch cập nhật thành công.

#### 2.7.2. Hướng dẫn Rút tiền từ Ví về tài khoản Ngân hàng
1.  **Cập nhật tài khoản ngân hàng nhận tiền**:
    *   Người dùng truy cập vào phần thông tin cá nhân.
    *   Cung cấp chính xác thông tin bao gồm: Tên ngân hàng, Số tài khoản và Họ tên chủ tài khoản (viết hoa không dấu) và lưu lại.
2.  **Gửi yêu cầu rút tiền**:
    *   Truy cập mục **Quản lý Ví** -> Chọn phần **Rút tiền**.
    *   Nhập số tiền muốn rút (số tiền rút không được lớn hơn số dư hiện tại trong ví).
    *   Bấm nút gửi yêu cầu rút tiền.
    *   Số tiền này sẽ được trừ trực tiếp và ngay lập tức khỏi số dư ví trên hệ thống để thực hiện chuyển khoản về tài khoản ngân hàng của người dùng mà không cần chờ quản trị viên duyệt thủ công.

---

### 2.8. Gửi Đơn trở thành giảng viên (Dành cho Học viên)

#### 2.8.1. Mục đích
Học viên nộp hồ sơ giới thiệu năng lực cá nhân để xin nâng cấp tài khoản của mình thành giảng viên của hệ thống nhằm tham gia đăng bán các khóa học.

#### 2.8.2. Điều kiện nộp đơn
*   Tài khoản nộp đơn phải đang hoạt động bình thường, chưa phải là giảng viên hoặc quản trị viên.
*   Tại một thời điểm, học viên chỉ được có duy nhất một đơn đăng ký đang chờ duyệt. Hệ thống sẽ khóa chức năng nộp đơn nếu có đơn cũ chưa xử lý xong.

#### 2.8.3. Hướng dẫn thao tác chi tiết
1.  Trên thanh điều hướng của học viên, chọn mục **Đăng ký làm Giảng viên**.
2.  Điền đầy đủ thông tin vào biểu mẫu đăng ký:
    *   **Tiêu đề chuyên môn**: Lĩnh vực thế mạnh của bạn (Ví dụ: "Chuyên gia lập trình Web").
    *   **Tiểu sử cá nhân**: Giới thiệu về quá trình làm việc, kinh nghiệm thực tế.
    *   **Động lực tham gia**: Lý do bạn muốn giảng dạy trên hệ thống.
    *   **Đường dẫn LinkedIn**: Điền link hồ sơ cá nhân để quản trị viên tham khảo kiểm chứng lý lịch.
    *   **Nhãn chủ đề đăng ký dạy**: Tích chọn những chủ đề mà bạn có kế hoạch soạn bài giảng.
3.  **Tải lên Hồ sơ lý lịch (CV)**:
    *   Tải tệp tin hồ sơ cá nhân ở định dạng **PDF** (dung lượng tối đa cho phép là 10MB).
    *   *Lưu ý bảo mật*: Hệ thống chỉ chấp nhận tệp tin PDF để đảm bảo an toàn. Hệ thống sẽ tự động đổi tên tệp tin và gắn thêm mã số bảo mật ngầm khi lưu trữ để tránh tình trạng ghi đè hoặc lộ thông tin của người dùng khác.
4.  Bấm nút **Gửi đơn**. Đơn sẽ chuyển sang trạng thái chờ duyệt của quản trị viên.
5.  **Cơ chế nộp lại đơn**:
    *   Nếu đơn trước đó từng bị từ chối, khi bạn quay lại giao diện đăng ký, hệ thống sẽ tự động điền sẵn các thông tin cũ để bạn dễ dàng sửa nhanh mà không cần nhập lại từ đầu.
    *   Khi bạn tiến hành tải lên một tệp tin CV mới, hệ thống sẽ tự động dọn dẹp và xóa tệp tin CV cũ của đơn bị từ chối trước đó nhằm tối ưu hóa dung lượng lưu trữ hệ thống.
