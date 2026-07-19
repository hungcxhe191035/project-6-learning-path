# Tài liệu Phân tích Chi tiết Chức năng - My Learning Path

Tài liệu này tổng hợp toàn bộ các phân tích chi tiết về mặt nghiệp vụ, mã nguồn, cơ sở dữ liệu, bảo mật và ví dụ thực tế của 9 chức năng cốt lõi thuộc 3 cấu phần: **ADMIN**, **STUDENT**, và **WALLET** trong hệ thống **My Learning Path**. Tất cả các chức năng đều được trình bày đồng nhất theo định dạng 8 phần chuyên sâu, tích hợp đầy đủ các đoạn code ví dụ thực tế và giải thích chi tiết từng dòng code cốt lõi.

---

## MỤC LỤC
1. [ADMIN - 1. Dashboard Tổng quan](#admin---1-dashboard-tổng-quan)
2. [ADMIN - 2. Quản lý Người dùng](#admin---2-quản-lý-người-dùng)
3. [ADMIN - 3. Duyệt Giảng viên](#admin---3-duyệt-giảng-viên)
4. [ADMIN - 4. Quản lý Tag](#admin---4-quản-lý-tag)
5. [ADMIN - 5. Quản lý Khóa học](#admin---5-quản-lý-khóa-học)
6. [ADMIN - 6. Quản lý Ví và Giao dịch](#admin---6-quản-lý-ví-và-giao-dịch)
7. [STUDENT - 1. Đăng ký trở thành Giảng viên](#student---1-đăng-ký-trở-thành-giảng-viên)
8. [WALLET - 1. Nạp tiền vào Ví](#wallet---1-nạp-tiền-vào-ví)
9. [WALLET - 2. Thanh toán bằng Ví](#wallet---2-thanh-toán-bằng-ví)

---

## ADMIN - 1. Dashboard Tổng quan

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cung cấp cho Admin cái nhìn toàn cảnh về tình hình kinh doanh và phát triển của hệ thống thông qua các chỉ số KPIs chính (tổng doanh thu, tổng số học viên, giảng viên, khóa học) và các thống kê trực quan bằng biểu đồ (xu hướng doanh thu, cơ cấu phân bổ học viên đăng ký theo tag chủ đề). Đồng thời, hiển thị các đơn đăng ký, khóa học đang chờ phê duyệt, các hóa đơn giao dịch mới nhất và danh sách xếp hạng top bán chạy.
*   **Đối tượng sử dụng**: Người quản trị hệ thống (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đã đăng nhập và được xác thực quyền Admin.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: dashboard.html] (Admin truy cập trang chủ admin hoặc chọn bộ lọc thời gian: 7days)
   ↓ (Gửi HTTP GET đến /admin/dashboard?period=7days)
[Controller: AdminController.java] (Hàm dashboard)
   ↓ (Chuyển giao cho Service thu thập dữ liệu)
[Service: AdminDashboardServiceImpl.java] (Hàm getDashboardData)
   ↓ (Sử dụng các Repositories để truy vấn DB đồng thời)
[DAOs: OrderRepository, UserRepository, CourseRepository, etc.]
   ↓ (Ghi nhận dữ liệu và map sang AdminDashboardDTO)
[Controller: AdminController.java] (Đẩy DTO vào Model)
   ↓ (Thymeleaf render mã HTML và nhúng dữ liệu vào mã script JS)
[UI: dashboard.html] (Chart.js nhận dữ liệu vẽ biểu đồ và hiển thị bảng dữ liệu)
```

#### **Ví dụ thực tế**:
Admin chọn xem thống kê theo bộ lọc **`7days`** (7 ngày gần nhất).
1.  **Xử lý tại Controller**:
    *   Hàm `dashboard` trong `AdminController` nhận tham số `period = "7days"`.
    *   Gọi `dashboardService.getDashboardData("7days")`.
2.  **Xử lý tại Service**:
    *   Xác định mốc thời gian: `start` = ngày hiện tại trừ 6 ngày (lúc 00:00:00), `end` = thời điểm hiện tại.
    *   Truy vấn tổng doanh thu tích lũy qua `orderRepository.sumTotalRevenue(ETransactionStatus.SUCCESS)`.
    *   Đếm số lượng học viên: `userRepository.countByRoleAndDeleteFlagFalse(ERole.STUDENT)`.
    *   Lấy toàn bộ danh sách hóa đơn thành công trong khoảng 7 ngày qua: `orderRepository.findByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtAsc(...)`.
    *   Gọi hàm hỗ trợ `generateRevenueChart("7days", orders)` để nhóm doanh thu. Hàm này khởi tạo sẵn một `LinkedHashMap` chứa 7 key đại diện cho chuỗi ngày (ví dụ từ `"2026-07-07"` đến `"2026-07-13"`) với giá trị ban đầu là `0đ`. Sau đó, duyệt qua các hóa đơn, lấy ngày tạo hóa đơn làm key và cộng dồn số tiền `totalAmount`.
    *   Gọi `enrollmentRepository.countEnrollmentsByTag()` thực thi JPQL đếm số lượt đăng ký theo tag chủ đề.
    *   Đóng gói toàn bộ thông tin vào `AdminDashboardDTO` trả về cho Controller.
3.  **Render giao diện**:
    *   Thymeleaf nhúng mảng nhãn ngày `["2026-07-07", ..., "2026-07-13"]` và mảng doanh thu `[500000.00, 0.00, ..., 1200000.00]` vào cấu hình của Chart.js để vẽ biểu đồ đường (Line Chart) mô tả xu hướng doanh thu.

---

### 3. Danh sách file liên quan
*   **Controller**: `AdminController.java`
*   **Service**: `AdminDashboardService.java`, `AdminDashboardServiceImpl.java`
*   **Repository (DAO)**: `OrderRepository.java`, `UserRepository.java`, `CourseRepository.java`, `CourseVersionRepository.java`, `EnrollmentRepository.java`, `OrderItemRepository.java`, `InstructorApplicationRepository.java`
*   **DTO**: `AdminDashboardDTO.java`, `ChartDataPointDTO.java`, `TopCourseDTO.java`, `TopInstructorDTO.java`
*   **Entity**: `User`, `Course`, `CourseVersion`, `Order`, `OrderItem`, `Enrollment`, `InstructorApplication`
*   **UI Template**: `dashboard.html`, `admin-layout.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Controller Endpoint (`AdminController.java`)**:
```java
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(name = "period", required = false, defaultValue = "30days") String period,
            Model model) {
        model.addAttribute("pageTitle", "Dashboard Quản trị");
        model.addAttribute("currentPeriod", period);

        AdminDashboardDTO dto = dashboardService.getDashboardData(period);
        model.addAttribute("dashboard", dto);

        return "pages/admin/dashboard";
    }
```
*   **Giải thích code**:
    *   `@RequestParam(name = "period", ...)`: Hứng tham số thời gian lọc từ URL. Nếu Client không truyền, mặc định sẽ lấy `"30days"`.
    *   `dashboardService.getDashboardData(period)`: Gọi tầng Service xử lý tính toán số liệu và trả về một DTO chứa toàn bộ dữ liệu Dashboard.
    *   `model.addAttribute("dashboard", dto)`: Đẩy DTO này vào Spring Model để Thymeleaf có thể parse dữ liệu và hiển thị lên giao diện HTML.

#### **Service Method (`AdminDashboardServiceImpl.java`)**:
```java
    @Override
    public AdminDashboardDTO getDashboardData(String period) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        // 1. Phân tích khoảng thời gian
        if ("today".equalsIgnoreCase(period)) {
            start = LocalDate.now().atStartOfDay();
            end = LocalDate.now().atTime(LocalTime.MAX);
        } else if ("7days".equalsIgnoreCase(period)) {
            start = LocalDate.now().minusDays(6).atStartOfDay();
        } else if ("30days".equalsIgnoreCase(period)) {
            start = LocalDate.now().minusDays(29).atStartOfDay();
        } else if ("thisyear".equalsIgnoreCase(period)) {
            start = LocalDate.now().withDayOfYear(1).atStartOfDay();
        } else {
            start = LocalDateTime.of(2000, 1, 1, 0, 0); // All time
        }

        // 2. Query KPIs và Doanh thu
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue(ETransactionStatus.SUCCESS);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        long totalStudents = userRepository.countByRoleAndDeleteFlagFalse(ERole.STUDENT);
        long totalInstructors = userRepository.countByRoleAndDeleteFlagFalse(ERole.INSTRUCTOR);
        long totalCourses = courseRepository.count();
        
        long pendingInstructorsCount = applicationRepository.countByStatus(EApplicationStatus.PENDING);
        long pendingCoursesCount = courseVersionRepository.countByStatusAndCourseDeleteFlagFalse(ECourseStatus.PENDING_APPROVAL);

        List<Order> ordersInPeriod = orderRepository.findByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtAsc(
                ETransactionStatus.SUCCESS, start, end
        );

        // 3. Khởi tạo dữ liệu đồ thị
        List<ChartDataPointDTO> revenueChartData = generateRevenueChart(period, ordersInPeriod);

        // ... Lấy dữ liệu top sellers và trả về DTO
        return AdminDashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalCourses(totalCourses)
                .pendingInstructorsCount(pendingInstructorsCount)
                .pendingCoursesCount(pendingCoursesCount)
                .revenueChartData(revenueChartData)
                .build();
    }
```
*   **Giải thích code**:
    *   **Phân tích khoảng thời gian**: Service thiết lập thời điểm bắt đầu (`start`) và kết thúc (`end`) dựa trên chuỗi `period`. Ví dụ: `"7days"` sẽ lấy ngày hiện tại trừ đi 6 ngày (tính từ 0 giờ sáng `atStartOfDay()`) để lấy đúng chu kỳ 7 ngày.
    *   `sumTotalRevenue(ETransactionStatus.SUCCESS)`: Lấy ra tổng số tiền của tất cả các đơn hàng đã thanh toán thành công trong DB.
    *   `findByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtAsc(...)`: Query tất cả các hóa đơn thành công nằm trong khoảng `start` và `end`, sắp xếp tăng dần theo thời gian tạo để làm nguyên liệu vẽ đồ thị.

#### **Hàm nhóm Doanh thu vẽ Đồ thị (`generateRevenueChart`)**:
```java
    private List<ChartDataPointDTO> generateRevenueChart(String period, List<Order> orders) {
        Map<String, BigDecimal> chartMap = new LinkedHashMap<>();

        if ("7days".equalsIgnoreCase(period)) {
            for (int i = 6; i >= 0; i--) {
                chartMap.put(LocalDate.now().minusDays(i).toString(), BigDecimal.ZERO);
            }
            for (Order order : orders) {
                if (order.getCreatedAt() != null) {
                    String key = order.getCreatedAt().toLocalDate().toString();
                    if (chartMap.containsKey(key)) {
                        chartMap.put(key, chartMap.get(key).add(order.getTotalAmount()));
                    }
                }
            }
        }
        // ... Xử lý tương tự cho các khoảng thời gian khác ...
        List<ChartDataPointDTO> points = new ArrayList<>();
        chartMap.forEach((label, val) -> points.add(new ChartDataPointDTO(label, val)));
        return points;
    }
```
*   **Giải thích code**:
    *   `Map<String, BigDecimal> chartMap = new LinkedHashMap<>()`: Sử dụng `LinkedHashMap` thay vì `HashMap` thông thường để **bảo toàn thứ tự chèn của các key** (thứ tự thời gian từ cũ đến mới).
    *   `chartMap.put(..., BigDecimal.ZERO)`: Điền trước mốc thời gian kèm giá trị mặc định là `0đ`. Việc này giúp đồ thị hiển thị mốc thời gian liên tục kể cả những ngày không phát sinh doanh thu.
    *   Vòng lặp duyệt `Order`: Lấy ngày tạo đơn hàng (`order.getCreatedAt().toLocalDate().toString()`) làm key, cộng dồn doanh thu của đơn hàng đó vào ngày tương ứng trong map.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
Truy vấn dữ liệu thô từ nhiều bảng để tổng hợp:
*   `orders`: Tính tổng doanh thu và lấy dữ liệu vẽ biểu đồ theo thời gian.
*   `users`: Đếm số lượng theo cột `role` và lọc `delete_flag = 0`.
*   `courses`: Đếm tổng số khóa học và lấy thông tin xếp hạng top giảng viên/khóa học.
*   `enrollments`: Đếm số lượng ghi danh để phân bổ tỷ lệ tag chủ đề.

### 6. Phân tích UI & JS (Thymeleaf & Chart.js)
*   **Tích hợp Chart.js**: Dữ liệu từ DTO được đưa trực tiếp vào thẻ `<script>` của Thymeleaf thông qua biểu thức inline:
    ```javascript
    const labels = [[${dashboard.revenueChartData.![label]}]];
    const data = [[${dashboard.revenueChartData.![value]}]];
    ```
    Biểu thức `![label]` và `![value]` là cú pháp Thymeleaf Projection giúp chuyển đổi List đối tượng DTO thành mảng String/Number thô trực quan cho JavaScript.
*   **Vẽ Biểu đồ tròn (Doughnut Chart)**: Mô tả thị phần đăng ký các tag học tập, giúp Admin biết lĩnh vực nào đang thu hút nhiều học viên nhất.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Khởi tạo Map rỗng có thứ tự (LinkedHashMap) khi vẽ chart**:
    *   *Ý nghĩa*: Đảm bảo tính liên tục của đồ thị. Nếu trong 7 ngày qua, có ngày 2 và ngày 3 không phát sinh đơn hàng nào, đồ thị vẫn phải hiển thị mốc ngày đó với doanh thu cột mốc bằng `0đ`, tránh trường hợp đồ thị bị co rúm, nhảy cóc mốc thời gian gây hiểu lầm cho người đọc.

### 8. Security (Bảo mật)
*   Quyền truy cập được cấu hình kiểm soát chặt chẽ thông qua Spring Security trong lớp `SecurityConfig` (chỉ cho phép user có role `ADMIN` truy cập `/admin/**`).
*   Các truy vấn thống kê sử dụng JPQL Parameterized và Spring Data JPA giúp ngăn ngừa hoàn toàn các nguy cơ tấn công SQL Injection.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Gom nhóm dữ liệu thống kê cực kỳ quy củ, sạch sẽ thông qua cấu trúc DTO chuyên biệt. Logic sinh biểu đồ thông minh, linh hoạt theo từng bộ lọc thời gian.
*   *Điểm yếu / Hạn chế*: Việc thực hiện quét và tính tổng tiền trực tiếp trên bảng `orders` của Database mỗi khi tải trang Dashboard sẽ gây thắt nút cổ chai hiệu năng khi số lượng hóa đơn tăng lên hàng triệu bản ghi.
*   *Đề xuất*: Nên sử dụng cơ chế Caching (như Redis) để lưu trữ tạm thời dữ liệu dashboard và cập nhật sau mỗi 10-15 phút.

---

## ADMIN - 2. Quản lý Người dùng

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép Admin tìm kiếm, lọc phân trang, xem chi tiết thông tin, gán vai trò (role) và thay đổi trạng thái hoạt động (khóa/mở khóa) đơn lẻ hoặc hàng loạt của toàn bộ thành viên trong hệ thống (Học viên, Giảng viên). Đồng thời hỗ trợ tạo thủ công tài khoản mới cho người dùng.
*   **Đối tượng sử dụng**: Người quản trị hệ thống (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đã đăng nhập và được xác thực quyền Admin.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: users.html] (Admin điền form đăng ký người dùng mới và nhấn submit)
   ↓ (Gửi HTTP POST đến /admin/users/create)
[Controller: AdminController.java] (Hàm createUser)
   ↓ (Sử dụng DTO CreateUserRequest để hứng và validate)
[Service: AdminServiceImpl.java] (Hàm createUser)
   ↓ (Kiểm tra trùng email, mã hóa mật khẩu và tự động khởi tạo Ví Wallet)
[DAOs: UserRepository & WalletRepository]
   ↓ (Thực thi lưu User, JPA tự động cascade lưu Wallet đi kèm)
[Database: Bảng users và wallets]
   ↓ (Redirect về trang danh sách kèm thông điệp Flash)
[Controller: AdminController.java] → Quay lại view danh sách users.html
```

#### **Ví dụ thực tế**:
Admin tạo mới một tài khoản Giảng viên tên là **Lê Hoàng**, email `lehoang@gmail.com`, mật khẩu `12345678`.
1.  **Xử lý tại Controller**:
    *   Hàm `createUser` nhận dữ liệu. Lớp validation `@Valid` kiểm tra định dạng email và mật khẩu. Nếu hợp lệ, gọi `adminService.createUser(request)`.
2.  **Xử lý tại Service**:
    *   Kiểm tra email trùng: Gọi `userRepository.existsByEmail("lehoang@gmail.com")` -> Trả về `false` (Hợp lệ).
    *   Băm mật khẩu: Gọi `passwordEncoder.encode("12345678")` sinh chuỗi BCrypt an toàn.
    *   Tự động gán `deleteFlag = false` và trạng thái `ACTIVE`.
    *   Tạo ví rỗng đi kèm: Dựng thực thể `Wallet` với `balance = 0` và gán mối liên kết 1-1.
    *   Gọi `userRepository.save(user)`. Nhờ JPA cascading, database thực hiện 2 câu lệnh chèn dữ liệu đồng thời vào bảng `users` và `wallets`.

---

### 3. Danh sách file liên quan
*   **Controller**: `AdminController.java`
*   **Service**: `AdminService.java`, `AdminServiceImpl.java`
*   **Repository (DAO)**: `UserRepository.java`
*   **DTO**: `CreateUserRequest.java`, `AssignRoleRequest.java`
*   **Entity**: `User.java`, `Wallet.java`
*   **UI Template**: `users.html`, `user-details.html`, `admin-layout.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Tạo người dùng mới (`AdminServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        // Tạo User
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPlainPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getStatus())
                .build();
        user.setDeleteFlag(false);

        // Tạo Ví liên kết
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        user.setWallet(wallet);

        userRepository.save(user); // JPA Cascade tự động lưu ví đi kèm
    }
```
*   **Giải thích code**:
    *   `existsByEmail(...)`: Kiểm tra email đã có người đăng ký chưa.
    *   `passwordEncoder.encode(...)`: Mã hóa mật khẩu plain text thành chuỗi bảo mật BCrypt trước khi lưu xuống.
    *   `Wallet.builder().balance(BigDecimal.ZERO).user(user).build()`: Thiết lập mối liên kết hai chiều giữa thực thể `User` và `Wallet`. 
    *   `userRepository.save(user)`: Lưu user xuống DB, JPA Cascade tự động insert bản ghi Ví vào bảng `wallets` giúp đảm bảo tính nhất quán dữ liệu ví luôn đi kèm user.

#### **Hàm Khóa hàng loạt loại trừ Admin (`AdminServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void bulkLockUsers(List<Long> userIds, String adminEmail) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            // Ngăn chặn tự khóa chính mình (Self-lock protection)
            if (user.isDeleteFlag() || user.getEmail().equalsIgnoreCase(adminEmail)) {
                continue;
            }
            user.setStatus(EAccountStatus.INACTIVE);
        }
        userRepository.saveAll(users);
    }
```
*   **Giải thích code**:
    *   `userRepository.findAllById(userIds)`: Tải toàn bộ danh sách các người dùng được chọn từ DB lên bộ nhớ qua IN query.
    *   `user.getEmail().equalsIgnoreCase(adminEmail)`: Kiểm tra nếu email của user trong danh sách trùng với email của admin đang thực hiện request.
    *   `continue`: Bỏ qua (không cập nhật trạng thái) đối với bản ghi của chính Admin đang thao tác để tránh lỗi tự khóa bản thân.
    *   `userRepository.saveAll(users)`: Thực hiện cập nhật trạng thái hàng loạt xuống DB.

#### **Query JPQL tìm kiếm (`UserRepository.java`)**:
```java
    @Query("SELECT u FROM User u WHERE u.deleteFlag = false AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("role") ERole role, @Param("status") EAccountStatus status, @Param("search") String search, Pageable pageable);
```
*   **Giải thích code**:
    *   `u.deleteFlag = false`: Hỗ trợ soft-delete, chỉ lấy ra những người dùng chưa bị xóa.
    *   `(:role IS NULL OR u.role = :role)`: Kỹ thuật kiểm tra tham số động. Nếu biến Java `role` truyền vào là `null`, mệnh đề bên trái đúng và JPA bỏ qua bộ lọc theo vai trò.
    *   `LOWER(...) LIKE LOWER(CONCAT('%', :search, '%'))`: Tìm kiếm khớp một phần không phân biệt chữ hoa/chữ thường trên cả hai cột Họ tên và Email.

#### **JavaScript xử lý Bulk Action (`users.html`)**:
```javascript
        function submitBulkAction(actionUrl, actionName) {
            const checkedCheckboxes = document.querySelectorAll('.user-checkbox:checked');
            const confirmMsg = `Bạn có chắc chắn muốn ${actionName.toLowerCase()} ${checkedCheckboxes.length} tài khoản đã chọn?`;
            
            showConfirm(confirmMsg, function() {
                const form = document.getElementById('bulkActionForm');
                form.action = actionUrl;
                form.innerHTML = ''; // Clear

                // Chèn CSRF Token
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                if (csrfToken) {
                    const csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = csrfToken;
                    form.appendChild(csrfInput);
                }

                checkedCheckboxes.forEach(cb => {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = 'userIds';
                    input.value = cb.value;
                    form.appendChild(input);
                });
                form.submit();
            }, `Xác nhận thao tác hàng loạt`, true, actionName);
        }
```
*   **Giải thích code**:
    *   `document.querySelectorAll('.user-checkbox:checked')`: Thu thập tất cả các checkbox của người dùng đang được chọn.
    *   `form.action = actionUrl`: Gán đích gửi dữ liệu động (ví dụ `/admin/users/bulk-lock`).
    *   `csrfToken`: Lấy mã bảo mật CSRF từ thẻ meta của trang và nhét vào form ẩn dưới dạng thẻ `<input type="hidden">` để vượt qua bộ lọc Spring Security CSRF Filter.
    *   `checkedCheckboxes.forEach(...)`: Tạo động các thẻ input ẩn chứa ID người dùng và nhét vào form trước khi submit.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`users`**: Bảng chính lưu trữ thông tin cá nhân của người dùng, vai trò (`role`) và cờ xóa (`delete_flag`).
*   **`wallets`**: Lưu trữ số dư ví liên kết 1-1 với user thông qua khóa ngoại `user_id`.

### 6. Phân tích UI & JS (Thymeleaf & Bulk Actions JavaScript)
*   **Đồng bộ Checkbox Chọn tất cả (Select All)**: Sử dụng JavaScript để lắng nghe sự kiện trên checkbox đầu bảng, tự động tích chọn hoặc bỏ tích các checkbox con `.user-checkbox` (ngoại trừ các checkbox bị disable).
*   **Hộp thao tác hàng loạt (Bulk Action Bar)**: Khi có ít nhất một người dùng được chọn, JS hiển thị một thanh công cụ trượt lên cung cấp nút bấm "Khóa hàng loạt" / "Mở khóa hàng loạt". Khi nhấn, JS gom toàn bộ user IDs được tích chọn đưa vào form ẩn và submit.
*   **Form gán vai trò dynamic**: Modal gán vai trò dùng chung `#globalAssignRoleModal` được chèn sẵn vào file layout. JS sẽ ghi đè thuộc tính `action` của Form để submit đúng API cụ thể của user được click.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Bảo vệ bản thân khỏi thao tác Khóa (Self-lock protection)**:
    *   *Ý nghĩa*: Giao diện HTML kiểm tra nếu dòng thông tin là của Admin đang đăng nhập (`#authentication.name == user.email`), nút khóa tài khoản sẽ bị ẩn đi và checkbox bị disabled. Dưới Service, hàm `bulkLockUsers` duyệt danh sách ID cũng tự động loại trừ email trùng với email của admin đang thực hiện. Logic này ngăn tình trạng Admin tự khóa chính mình khiến hệ thống rơi vào trạng thái bế tắc (deadlock) không ai đăng nhập quản trị được nữa.

### 8. Security (Bảo mật)
*   **Ngăn chặn leo thang đặc quyền (Privilege Escalation)**: 
    *   API gán vai trò (`assignRole`) trong lớp Service kiểm tra cứng: `if (role == ERole.ADMIN) { throw new RuntimeException(...); }`. Dù hacker cố ý sửa đổi payload HTTP request để nâng cấp quyền thành `ADMIN`, API vẫn chặn lại, bảo vệ tính toàn vẹn hệ thống.
*   **Bảo mật dữ liệu mật khẩu**: Sử dụng thuật toán băm mã hóa BCrypt mạnh mẽ để bảo vệ mật khẩu người dùng trước nguy cơ rò rỉ dữ liệu.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Áp dụng tốt các mẫu thiết kế DTO bảo vệ dữ liệu. Cơ chế bảo vệ Admin và chống leo thang đặc quyền cực kỳ chặt chẽ. Trải nghiệm Bulk Action thông minh.
*   *Điểm yếu*: Tại trang chi tiết người dùng, hệ thống hiển thị danh sách lịch sử giao dịch bằng cách lấy trực tiếp từ List thực thể `user.wallet.transactions` (nạp dữ liệu `@OneToMany` toàn bộ). Nếu người dùng có hàng ngàn giao dịch, việc này sẽ gây tràn bộ nhớ.
*   *Đề xuất*: Nên viết một query JPA phân trang riêng để tải danh sách giao dịch ví của người dùng theo trang thay vì load trực tiếp danh sách liên kết.

---

## ADMIN - 3. Duyệt Giảng viên

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép Admin xem xét hồ sơ năng lực của các Học viên đăng ký trở thành Giảng viên hệ thống (gồm CV bản PDF, lĩnh vực muốn dạy, tiểu sử cá nhân và động lực giảng dạy). Admin đưa ra quyết định **Phê duyệt (APPROVED)** hoặc **Từ chối (REJECTED)** đơn đăng ký đó.
*   **Đối tượng sử dụng**: Người quản trị (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đăng nhập hệ thống với quyền Admin và có đơn đăng ký giảng viên ở trạng thái `PENDING`.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: application-detail.html] (Admin nhấn nút Duyệt/Từ chối trên Form)
   ↓ (Gửi HTTP POST đến /admin/applications/{id}/review)
[Controller: AdminController.java] (Hàm reviewApplication)
   ↓ (Giao tiếp qua DTO ReviewApplicationRequest)
[Service: InstructorApplicationServiceImpl.java] (Hàm reviewApplication)
   ↓ (Truy vấn / Lưu dữ liệu qua Repositories)
[DAO: InstructorApplicationRepository & UserRepository] 
   ↓ (Ghi nhận dữ liệu vật lý)
[Database: Bảng users và instructor_applications]
   ↓ (Gọi Service gửi thông báo)
[Service: NotificationServiceImpl & EmailService] (Gửi thông báo & Email cho ứng viên)
   ↓ (Chuyển hướng trang)
[Controller: AdminController.java] → Redirect về /admin/applications
```

#### **Ví dụ thực tế**:
Học viên Nguyễn Văn A (`userId = 25`, email: `nguyenvana@gmail.com`) nộp đơn và được cấp mã đơn `applicationId = 4` ở trạng thái `PENDING`. Admin đọc hồ sơ và phê duyệt.
1.  **Xử lý tại Controller**:
    *   Admin gửi form phê duyệt với `decision` = `APPROVED` và `reviewNote` = `"Hồ sơ rất tốt"`.
    *   Hàm `reviewApplication` tiếp nhận request và gọi `applicationService.reviewApplication(4L, request)`.
2.  **Xử lý tại Service**:
    *   Lấy đơn đăng ký từ Repo: `applicationRepository.findById(4L)`.
    *   Kiểm tra đơn hợp lệ (trạng thái là PENDING).
    *   Cập nhật trạng thái đơn thành `APPROVED` và ghi nhận nhận xét.
    *   Lấy user liên kết từ đơn, nâng cấp vai trò: `user.setRole(ERole.INSTRUCTOR)`.
    *   Gọi `userRepository.save(user)` và `applicationRepository.save(application)` lưu xuống DB.
    *   Gọi `notificationService` tạo chuông báo trên web và `emailService` gửi mail thông báo kết quả cho học viên.

---

### 3. Danh sách file liên quan
*   **Controller**: `AdminController.java`
*   **Service**: `InstructorApplicationService.java`, `InstructorApplicationServiceImpl.java`, `NotificationService.java`, `EmailService.java`
*   **Repository (DAO)**: `InstructorApplicationRepository.java`, `UserRepository.java`, `TagRepository.java`
*   **DTO**: `ReviewApplicationRequest.java`
*   **Entity**: `InstructorApplication`, `User`, `Tag`
*   **UI Template**: `applications.html`, `application-detail.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Duyệt ứng viên của Service (`InstructorApplicationServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void reviewApplication(Long applicationId, ReviewApplicationRequest request) {
        InstructorApplication application = getApplicationById(applicationId);

        // Validate trạng thái
        if (application.getStatus() != EApplicationStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý trước đó.");
        }
        if (request.getDecision() == EApplicationStatus.PENDING) {
            throw new RuntimeException("Quyết định không hợp lệ.");
        }
        if (request.getDecision() == EApplicationStatus.REJECTED &&
                (request.getReviewNote() == null || request.getReviewNote().trim().isEmpty())) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối.");
        }

        // Cập nhật trạng thái đơn
        application.setStatus(request.getDecision());
        application.setReviewNote(request.getReviewNote());

        User user = application.getUser();
        boolean isApproved = request.getDecision() == EApplicationStatus.APPROVED;

        // Thăng cấp quyền
        if (isApproved) {
            user.setRole(ERole.INSTRUCTOR);
            userRepository.save(user);
        }

        applicationRepository.save(application);

        // Gửi thông báo hệ thống và email
        String title = "Kết quả duyệt đơn đăng ký Giảng viên";
        String content = isApproved 
                ? "Chúc mừng! Đơn đăng ký giảng viên của bạn đã được duyệt thành công. Bạn hiện đã là Giảng viên."
                : "Đơn đăng ký giảng viên của bạn đã bị từ chối. Lý do: " + request.getReviewNote();
        notificationService.sendNotification(user, title, content);

        emailService.sendApplicationResultEmail(user.getEmail(), user.getFullName(), isApproved, request.getReviewNote());
    }
```
*   **Giải thích code**:
    *   `if (application.getStatus() != EApplicationStatus.PENDING)`: Bảo mật dữ liệu, tránh duyệt lại một đơn đã xử lý trước đó.
    *   `user.setRole(ERole.INSTRUCTOR)`: Nâng cấp vai trò người dùng trực tiếp trong code từ học viên lên giảng viên ngay khi đơn được phê duyệt.
    *   `notificationService.sendNotification(...)` & `emailService.sendApplicationResultEmail(...)`: Các phương thức phụ trách gửi thông tin thông báo phản hồi cho học viên nộp hồ sơ.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`instructor_applications`**: Cập nhật trạng thái `status` thành `APPROVED` hoặc `REJECTED`, ghi lý do vào `review_note`.
*   **`users`**: Cập nhật cột `role` từ `'STUDENT'` thành `'INSTRUCTOR'` (khi duyệt thành công).

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   **Tải tài liệu CV (PDF)**: Giao diện hiển thị liên kết tải trực tiếp tệp tin CV PDF của ứng viên lưu trên đĩa cứng server.
*   **Form phê duyệt**: Cung cấp textarea nhập nhận xét và 2 nút submit đại diện cho 2 lựa chọn (APPROVED/REJECTED) gửi cùng lên 1 form xử lý.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Yêu cầu bắt buộc lý do khi Từ chối đơn**:
    *   *Ý nghĩa*: Service kiểm tra nếu `decision == REJECTED` thì `reviewNote` không được để trống. Điều này đảm bảo học viên biết rõ lý do hồ sơ bị loại (ví dụ: "CV bị mờ") để bổ sung nộp lại đơn khác sau này, tránh gây ức chế cho người dùng.
*   **Đổi vai trò người dùng tự động**:
    *   *Ý nghĩa*: Đổi role học viên thành giảng viên ngay khi duyệt đơn giúp giảng viên đăng nhập lại là có quyền soạn khóa học lập tức, không cần Admin phải thao tác thủ công một bước thứ hai ở trang quản trị user.

### 8. Security (Bảo mật)
*   **Ngăn chặn duyệt đơn trùng lặp**:
    *   *Ý nghĩa*: Code kiểm tra trạng thái đơn phải là `PENDING` mới xử lý. Nếu đơn đã xử lý trước đó, hệ thống ném ngoại lệ chặn lại để tránh hacker gửi request phê duyệt liên tục làm xáo trộn dữ liệu role của user.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Thiết kế phân tách module thông báo và gửi mail rất tốt, giúp code mạch lạc.
*   *Điểm yếu*: Việc gửi email chúc mừng được thực hiện đồng bộ (Synchronous) làm thắt nút cổ chai luồng xử lý chính. Admin sẽ phải chờ màn hình quay tròn từ 2-3 giây cho đến khi email gửi thành công qua SMTP Server.
*   *Đề xuất*: Sử dụng annotation `@Async` của Spring Boot để chuyển tác vụ gửi mail và thông báo sang chạy ngầm bất đồng bộ.

---

## ADMIN - 4. Quản lý Tag

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép Admin quản lý danh sách các nhãn danh mục (Tags) chủ đề giảng dạy trong hệ thống E-Learning (thêm mới, cập nhật thông tin và xóa mềm tag).
*   **Đối tượng sử dụng**: Người quản trị (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đăng nhập hệ thống với quyền Admin.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: tags.html] (Admin nhấn nút Trash/Thùng rác của tag ReactJS)
   ↓ (JS confirmDeleteTag hiển thị cảnh báo -> Admin đồng ý -> Submit Form)
[Controller: AdminController.java] (Hàm deleteTag)
   ↓ 
[Service: TagServiceImpl.java] (Hàm deleteTag)
   ↓ (Xóa các bản ghi liên kết ở bảng trung gian N-N trước bằng Native Query)
[DAO: TagRepository] (deleteCourseVersionTagMappingsByTagId và deleteApplicationTagMappingsByTagId)
   ↓ (Đổi deleteFlag = true của Tag)
[DAO: TagRepository.save] 
   ↓ (Cập nhật DB)
[Database: Bảng course_version_tag_mappings, application_tag_mappings, tags]
```

#### **Ví dụ thực tế**:
Admin muốn xóa tag "**ReactJS**" (`tagId = 5`) đang được gắn cho nhiều khóa học và đơn đăng ký.
1.  **Xử lý tại Controller**:
    *   Admin click nút xóa. JS popup xác nhận. Bấm đồng ý submit form POST lên `/admin/tags/5/delete`.
    *   Hàm `deleteTag` nhận ID và gọi `tagService.deleteTag(5L)`.
2.  **Xử lý tại Service**:
    *   Gọi `tagRepository.deleteCourseVersionTagMappingsByTagId(5L)`. SQL Native: `DELETE FROM course_version_tag_mappings WHERE tag_id = 5` (dọn dẹp liên kết khóa học).
    *   Gọi `tagRepository.deleteApplicationTagMappingsByTagId(5L)`. SQL Native: `DELETE FROM application_tag_mappings WHERE tag_id = 5` (dọn dẹp liên kết đơn đăng ký).
    *   Cập nhật cờ xóa tag: `tag.setDeleteFlag(true)`.
    *   Gọi `tagRepository.save(tag)`. SQL: `UPDATE tags SET delete_flag = 1 WHERE tag_id = 5` (Xóa mềm tag).

---

### 3. Danh sách file liên quan
*   **Controller**: `AdminController.java`
*   **Service**: `TagService.java`, `TagServiceImpl.java`
*   **Repository (DAO)**: `TagRepository.java`
*   **DTO**: `TagRequest.java`
*   **Entity**: `Tag.java`
*   **UI Template**: `tags.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Hồi sinh / Tạo tag mới (`TagServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public Tag createTag(TagRequest request) {
        String name = request.getTagName().trim();
        Optional<Tag> existingOpt = tagRepository.findByTagName(name);
        
        if (existingOpt.isPresent()) {
            Tag existing = existingOpt.get();
            if (!existing.isDeleteFlag()) {
                throw new RuntimeException("Tag \"" + name + "\" đã tồn tại.");
            }
            // Kích hoạt lại tag đã bị soft-deleted (Unique Constraint Bypass)
            existing.setDeleteFlag(false);
            existing.setDescription(request.getDescription());
            return tagRepository.save(existing);
        }
        
        Tag tag = Tag.builder()
                .tagName(name)
                .description(request.getDescription())
                .build();
        tag.setDeleteFlag(false);
        return tagRepository.save(tag);
    }
```
*   **Giải thích code**:
    *   `tagRepository.findByTagName(name)`: Tìm kiếm tag đã tồn tại theo tên để chặn trùng lặp.
    *   `existing.setDeleteFlag(false)`: Kích hoạt lại tag đã bị xóa mềm trước đó bằng cách đặt lại cờ xóa thành false và cập nhật mô tả mới, giúp vượt qua lỗi Unique Constraint của DB.
    *   `Tag.builder().deleteFlag(false).build()`: Nếu là tag hoàn toàn mới, khởi tạo bình thường và lưu.

#### **Hàm Xóa mềm Tag (`TagServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = getTagById(id);
        tag.setDeleteFlag(true); // Xóa mềm
        
        // Dọn sạch bảng trung gian tránh lỗi Foreign Key Constraint
        tagRepository.deleteCourseVersionTagMappingsByTagId(id);
        tagRepository.deleteApplicationTagMappingsByTagId(id);
        
        tagRepository.save(tag);
    }
```
*   **Giải thích code**:
    *   `tag.setDeleteFlag(true)`: Gán cờ xóa mềm tag.
    *   `deleteCourseVersionTagMappingsByTagId(id)` & `deleteApplicationTagMappingsByTagId(id)`: Thực thi Native SQL xóa bỏ các bản ghi tham chiếu đến tag này ở các bảng liên kết trung gian nhiều-nhiều trước để tránh lỗi khóa ngoại của SQL Server khi xóa.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`tags`**: Lưu thông tin tag và cờ xóa mềm `delete_flag`.
*   **`course_version_tag_mappings`** & **`application_tag_mappings`**: Các bảng liên kết trung gian nhiều-nhiều cần dọn sạch bản ghi liên quan trước khi xóa mềm tag để tránh lỗi ràng buộc khóa ngoại.

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   **Split Layout (Giao diện chia cột)**: Giao diện hiển thị form nhập bên trái và bảng danh sách bên phải giúp Admin thực hiện thêm/sửa/xóa tag nhanh chóng mà không cần di chuyển sang các trang khác.
*   **Popup xác nhận**: Sử dụng thư viện modal xác nhận để tránh việc Admin lỡ tay click nút xóa nhầm gây ảnh hưởng dữ liệu liên kết.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Cơ chế tự động kích hoạt lại tag bị soft-deleted**:
    *   *Ý nghĩa*: Cột `tag_name` có ràng buộc Unique Constraint. Nếu Admin thêm tag `"React"` đã bị xóa mềm trước đó (`delete_flag = 1`), Service sẽ tìm thấy bản ghi cũ, đổi `deleteFlag = false`, cập nhật mô tả mới và lưu. Cách xử lý này vừa tránh được lỗi trùng lặp DB, vừa giữ sạch được kho khóa chính.

### 8. Security (Bảo mật)
*   Mặc dù sử dụng truy vấn SQL nguyên bản (Native Query) để xóa các bảng trung gian, tham số vẫn được truyền an toàn qua Prepared Statement của JPA (`:tagId`) giúp chống lại các cuộc tấn công SQL Injection.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Xử lý triệt để lỗi ràng buộc khóa ngoại của DB bằng việc chủ động dọn dẹp bảng trung gian. Cơ chế phục hồi tag đã xóa mềm rất thông minh.
*   *Điểm yếu*: Thiếu ô tìm kiếm tag ở bảng danh sách.
*   *Đề xuất*: Bổ dung hộp tìm kiếm tag theo từ khóa.

---

## ADMIN - 5. Quản lý Khóa học

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép Admin theo dõi danh sách toàn bộ khóa học, xem chi tiết giáo trình bài giảng và thực hiện các quyền quản trị như: Khóa (Block) khóa học vi phạm kèm lý do, Mở khóa (Unblock), hoặc Xóa mềm (Delete) khóa học.
*   **Đối tượng sử dụng**: Người quản trị (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đăng nhập hệ thống với quyền Admin.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: courses.html] (Admin click "Khóa", điền lý do "Vi phạm bản quyền" và confirm)
   ↓ (Gửi HTTP POST đến /admin/courses/{id}/block?reason=...)
[Controller: AdminCourseController.java] (Hàm blockCourse)
   ↓ 
[Service: AdminServiceImpl.java] (Hàm blockCourse)
   ↓ 
[DAO: CourseRepository] 
   ↓ (Thực thi SQL UPDATE)
[Database: Bảng courses]
   ↓ (Redirect về danh sách khóa học kèm thông báo Flash)
[Controller: AdminCourseController.java] → Quay lại view courses.html
```

#### **Ví dụ thực tế**:
Khóa học *"Lập trình Python căn bản"* (`courseId = 8`) bị báo cáo chứa mã độc. Admin tiến hành khóa khóa học này.
1.  **Xử lý tại Controller**:
    *   Nhận request POST lên `/admin/courses/8/block` kèm param `reason = "Chứa mã nguồn độc hại"`.
    *   Gọi `adminService.blockCourse(8L, "Chứa mã nguồn độc hại")`.
2.  **Xử lý tại Service**:
    *   Lấy thực thể khóa học: `courseRepository.findById(8L)`.
    *   Cập nhật trạng thái khóa: `course.setIsBlocked(true)` và `course.setBlockReason("Chứa mã nguồn độc hại")`.
    *   Lưu xuống DB. SQL tương ứng: `UPDATE courses SET is_blocked = 1, block_reason = N'Chứa mã nguồn độc hại' WHERE course_id = 8`.
    *   Khóa học lập tức bị ẩn khỏi trang chủ tìm kiếm của học viên.

---

### 3. Danh sách file liên quan
*   **Controller**: `AdminCourseController.java`
*   **Service**: `AdminService.java`, `AdminServiceImpl.java`, `CourseService.java`, `CourseServiceImpl.java`
*   **Repository (DAO)**: `CourseRepository.java`
*   **Entity**: `Course`, `CourseVersion`, `CourseSection`, `Lesson`
*   **UI Template**: `courses.html`, `course-detail.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Khóa Khóa học của Service (`AdminServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void blockCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        course.setIsBlocked(true);
        course.setBlockReason(reason);
        courseRepository.save(course);
    }
```
*   **Giải thích code**:
    *   `course.setIsBlocked(true)`: Đặt thuộc tính đánh dấu khóa học bị khóa.
    *   `course.setBlockReason(reason)`: Lưu nhận xét lý do khóa từ Admin.
    *   `courseRepository.save(course)`: Ghi nhận cập nhật vào cơ sở dữ liệu.

#### **Query Admin Tìm kiếm và Phân trang Khóa học (`CourseRepository.java`)**:
```java
    @org.springframework.data.jpa.repository.Query(
        "SELECT c FROM Course c WHERE c.deleteFlag = false AND c.currentPublishedVersion IS NOT NULL AND " +
        "(:blocked IS NULL OR c.isBlocked = :blocked) AND " +
        "(:status IS NULL OR EXISTS (SELECT cv FROM CourseVersion cv WHERE cv.course = c AND cv.status = :status AND cv.createdAt = (SELECT MAX(cv2.createdAt) FROM CourseVersion cv2 WHERE cv2.course = c))) AND " +
        "(:search IS NULL OR EXISTS (SELECT cv FROM CourseVersion cv WHERE cv.course = c AND LOWER(cv.title) LIKE LOWER(CONCAT('%', :search, '%'))) OR LOWER(c.instructor.fullName) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    org.springframework.data.domain.Page<Course> searchCoursesAdmin(
            @org.springframework.data.repository.query.Param("status") ECourseStatus status,
            @org.springframework.data.repository.query.Param("blocked") Boolean blocked,
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable
    );
```
*   **Giải thích code**:
    *   `cv.createdAt = (SELECT MAX(cv2.createdAt)...)`: Subquery đảm bảo hệ thống chỉ lọc trạng thái của **phiên bản khóa học mới nhất** (tránh lấy nhầm thông tin của các phiên bản cũ đã lưu lịch sử).
    *   `(:blocked IS NULL OR c.isBlocked = :blocked)`: Lọc linh hoạt theo trạng thái khóa.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`courses`**: Bảng chính lưu trữ trạng thái khóa (`is_blocked`), lý do khóa (`block_reason`) và cờ xóa mềm (`delete_flag`).
*   **`course_versions`**: Chứa thông tin chi tiết tiêu đề, giá tiền của từng phiên bản khóa học.

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   **Modal nhập lý do khóa**: Khi click "Khóa", JS gán động ID khóa học vào thuộc tính `action` của Form trong modal `#blockCourseModal` rồi hiển thị form để Admin nhập lý do.
*   **Accordion Giáo trình**: Trang chi tiết khóa học sử dụng Accordion của Bootstrap để hiển thị cấu trúc bài học phân tầng theo từng chương (Sections -> Lessons) giúp Admin dễ thẩm định nội dung.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Phân biệt rõ ràng Khóa (Block) và Xóa mềm (Delete)**:
    *   *Ý nghĩa*: Khóa (Block) là hành động tạm thời xử lý vi phạm. Khóa học bị ẩn khỏi trang chủ để học viên mới không mua được, nhưng học viên cũ đã mua khóa học từ trước vẫn có quyền học bình thường (đảm bảo quyền lợi khách hàng). Còn Xóa mềm (Delete) là loại bỏ hoàn toàn khóa học khỏi hệ thống.

### 8. Security (Bảo mật)
*   Xem chi tiết khóa học và thao tác thay đổi trạng thái được bảo mật qua Parameterized JPA Query, chống lại các nguy cơ tấn công SQL Injection.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Xử lý nghiệp vụ chặt chẽ, tối ưu truy vấn SQL phân trang khi lấy trạng thái phiên bản mới nhất của khóa học.
*   *Điểm yếu*: Khi Admin khóa khóa học, hệ thống không tự động gửi thông báo hay email giải thích cho Giảng viên sở hữu khóa học biết lý do họ bị khóa.
*   *Đề xuất*: Tích hợp thêm dịch vụ gửi mail/thông báo cho giảng viên khi khóa học bị Block.

---

## ADMIN - 6. Quản lý Ví và Giao dịch

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép Admin kiểm soát toàn bộ dòng tiền hệ thống bằng cách theo dõi lịch sử mọi giao dịch (Nạp, Rút, Thanh toán) của tất cả người dùng, hỗ trợ tìm kiếm phân trang. Đồng thời cung cấp tính năng cấu hình tỉ lệ phần trăm chia sẻ doanh thu bài giảng cho Giảng viên.
*   **Đối tượng sử dụng**: Người quản trị (`ROLE_ADMIN`).
*   **Điều kiện sử dụng**: Đăng nhập hệ thống với quyền Admin.

*(Lưu ý: Nghiệp vụ phê duyệt và từ chối rút tiền của Admin đã bị loại bỏ theo yêu cầu trước đó, dòng tiền rút được thực hiện trực tiếp và thành công ngay lập tức ở phía người dùng).*

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: transactions.html] (Admin nhập tỉ lệ chia sẻ 85% và nhấn "Lưu")
   ↓ (Gửi yêu cầu AJAX POST đến /api/admin/settings/revenue-share?sharePercent=85)
[Controller: WalletApiController.java] (Hàm updateRevenueShare)
   ↓ (Kiểm tra quyền Admin và Validate khoảng giá trị [0 - 100])
[Service: SystemSettingServiceImpl.java] (Hàm saveSetting)
   ↓ 
[DAO: SystemSettingRepository] 
   ↓ (Thực thi SQL UPDATE hoặc INSERT)
[Database: Bảng system_settings]
   ↓ (Trả về JSON phản hồi thành công)
[UI: transactions.html] (JS hiển thị Toastr thông báo thành công và reload trang)
```

#### **Ví dụ thực tế**:
Admin muốn nâng tỉ lệ chia sẻ doanh thu bài giảng cho Giảng viên lên **`85%`** (Admin hệ thống chỉ thu 15% phí vận hành).
1.  **Xử lý tại Controller**:
    *   Hàm `updateRevenueShare` tiếp nhận tham số `sharePercent = 85`.
    *   Kiểm tra bảo mật và validate giá trị nằm trong khoảng `0` - `100`.
    *   Gọi `systemSettingService.saveSetting("INSTRUCTOR_REVENUE_SHARE_PERCENT", "85", "Tỉ lệ chia sẻ doanh thu giảng viên (%)")`.
2.  **Xử lý tại Service**:
    *   Kiểm tra key `INSTRUCTOR_REVENUE_SHARE_PERCENT` trong bảng `system_settings`.
    *   Cập nhật giá trị thành `"85"`.
    *   Lưu xuống DB. SQL: `UPDATE system_settings SET setting_value = '85' WHERE setting_key = 'INSTRUCTOR_REVENUE_SHARE_PERCENT'`.
    *   Từ thời điểm này, mọi giao dịch mua bài học mới phát sinh sẽ được nhân với hệ số chiết khấu mới là `0.85` cho Giảng viên.

---

### 3. Danh sách file liên quan
*   **Controller**: `WalletController.java`, `WalletApiController.java`
*   **Service**: `WalletService.java`, `WalletServiceImpl.java`, `SystemSettingService.java`, `SystemSettingServiceImpl.java`
*   **Repository (DAO)**: `WalletTransactionRepository.java`, `SystemSettingRepository.java`
*   **Entity**: `WalletTransaction`, `SystemSetting`
*   **UI Template**: `transactions.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **API Cấu hình tỉ lệ chiết khấu (`WalletApiController.java`)**:
```java
    @PostMapping("/admin/settings/revenue-share")
    public ResponseEntity<?> updateRevenueShare(
            @RequestParam("sharePercent") Integer sharePercent,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền thực hiện!"));
        }
        if (sharePercent == null || sharePercent < 0 || sharePercent > 100) {
            return ResponseEntity.badRequest().body(Map.of("message", "Phần trăm chia sẻ phải từ 0 đến 100%"));
        }
        systemSettingService.saveSetting(
                "INSTRUCTOR_REVENUE_SHARE_PERCENT",
                String.valueOf(sharePercent),
                "Tỉ lệ chia sẻ doanh thu giảng viên (%)"
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật tỉ lệ chia sẻ doanh thu thành công!"));
    }
```
*   **Giải thích code**:
    *   `@AuthenticationPrincipal`: Nhận diện thông tin người dùng đang gửi request. Chỉ chấp nhận role `ADMIN`.
    *   `sharePercent < 0 || sharePercent > 100`: Validate khoảng dữ liệu phần trăm chiết khấu hợp lý.
    *   `systemSettingService.saveSetting(...)`: Lưu giá trị cấu hình xuống DB dưới dạng key-value động.

#### **Hàm lưu Cài đặt Hệ thống (`SystemSettingServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void saveSetting(String key, String value, String description) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElse(SystemSetting.builder()
                        .settingKey(key)
                        .build());
        setting.setSettingValue(value);
        if (description != null) {
            setting.setDescription(description);
        }
        setting.setDeleteFlag(false);
        systemSettingRepository.save(setting);
    }
```
*   **Giải thích code**:
    *   `findBySettingKey(key)`: Tìm kiếm khóa cấu hình có sẵn trong DB.
    *   `.orElse(...)`: Nếu khóa chưa tồn tại, khởi tạo một thực thể `SystemSetting` mới.
    *   `setting.setSettingValue(value)`: Ghi nhận giá trị cấu hình mới và lưu xuống DB.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`wallet_transactions`**: Lưu trữ lịch sử giao dịch. Chứa số tiền `amount`, loại giao dịch `transaction_type` (DEPOSIT, WITHDRAW, PAYMENT), trạng thái `status` (PENDING, SUCCESS, FAIL).
*   **`system_settings`**: Lưu trữ các cặp key-value cấu hình của hệ thống (trong đó có `INSTRUCTOR_REVENUE_SHARE_PERCENT`).

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   **Phân biệt màu sắc dòng tiền**: Sử dụng màu sắc trực quan qua các CSS badge để Admin dễ nhận biết loại giao dịch (Nạp tiền hiển thị dấu `+` màu xanh lá, Rút tiền/Thanh toán hiển thị dấu `-` màu đỏ).
*   **Xác nhận thay đổi cấu hình**: JS chặn sự kiện click và hiển thị popup cảnh báo tính toán rõ phần trăm sàn sẽ nhận được trước khi Admin bấm confirm lưu tỉ lệ chiết khấu mới.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Lưu cấu hình chiết khấu động trong Database**:
    *   *Ý nghĩa*: Giúp Admin có thể thay đổi chính sách kinh doanh (tăng/giảm chiết khấu cho giảng viên) trực tiếp trên giao diện quản trị bất kỳ lúc nào mà không cần lập trình viên phải sửa file cấu hình, rebuild mã nguồn và khởi động lại Server (tránh downtime hệ thống).

### 8. Security (Bảo mật)
*   Kiểm tra quyền ADMIN chặt chẽ tại API cập nhật cấu hình, ngăn chặn tin tặc hoặc các giảng viên cố ý đoán URL API để tự cấu hình nâng tỉ lệ chiết khấu có lợi cho họ.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Xử lý chữ ký bảo mật và validate số liệu cực kỳ nghiêm ngặt ở cả hai đầu client và server.
*   *Điểm yếu*: Thiếu ghi log hoạt động thay đổi cấu hình (Audit Log). Tỉ lệ chiết khấu doanh thu là cấu hình tài chính cốt lõi nên cần được ghi vết chỉnh sửa chi tiết (ai sửa, lúc nào, giá trị cũ và mới) để đối soát.
*   *Đề xuất*: Xây dựng bảng lưu log chỉnh sửa cấu hình hệ thống.

---

## STUDENT - 1. Đăng ký trở thành Giảng viên

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép một Học viên (Student) gửi hồ sơ đăng ký bao gồm tiêu đề nghề nghiệp, tiểu sử, động lực giảng dạy, tài khoản LinkedIn và tệp tin CV PDF (dung lượng tối đa 10MB) lên hệ thống, chờ Admin duyệt nâng cấp tài khoản.
*   **Đối tượng sử dụng**: Học viên hệ thống (`ROLE_STUDENT`).
*   **Điều kiện sử dụng**: Tài khoản đang hoạt động, chưa phải giảng viên hoặc admin, và không có đơn nào khác ở trạng thái `PENDING`.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: apply.html] (Học viên điền thông tin, chọn file CV PDF và click "Gửi đơn")
   ↓ (Gửi HTTP POST multipart/form-data đến /instructor/apply)
[Controller: InstructorApplicationController.java] (Hàm submitApply)
   ↓ (Validate thủ công định dạng file .pdf và dung lượng tệp tin)
[Service: InstructorApplicationServiceImpl.java] (Hàm submitApplication)
   ↓ (Yêu cầu lưu file vật lý)
[Service: FileStorageServiceImpl.java] (Hàm storeCvFile)
   ↓ (Tiến hành dọn dẹp file cũ nếu nộp đơn mới, sau đó lưu file mới vào uploads/cv/)
[Disk Storage: Thư mục uploads/cv/]
   ↓ (Lấy thông tin Tags muốn dạy từ tagIds)
[DAO: TagRepository & InstructorApplicationRepository]
   ↓ (Lưu bản ghi đơn đăng ký mới trạng thái PENDING)
[Database: Bảng instructor_applications, application_tag_mappings]
```

#### **Ví dụ thực tế**:
Học viên Trần Bính (`userId = 30`) nộp đơn xin làm Giảng viên. Anh Bính đã nộp đơn 1 lần trước đó và bị từ chối (`REJECTED`) do ảnh CV bị mờ. Lần này anh nộp lại.
1.  **Tải trang (GET)**:
    *   Hệ thống gọi `applicationService.getMyLatestApplication(30L)`. Tìm thấy đơn cũ bị REJECTED. Tự động đổ lại các thông tin thô cũ (Headline, Bio, Motivation) ra form UI để anh Bính chỉnh sửa nhanh.
2.  **Submit Đơn (POST)**:
    *   Anh Bính tải lên file `Tran_Binh_CV.pdf` (2MB).
    *   Hàm `submitApply` validate file PDF dung lượng hợp lệ.
    *   Gọi Service xử lý: Service phát hiện có file mới và đơn cũ là REJECTED, lập tức gọi `fileStorageService.deleteCvFile("old_cv.pdf")` để xóa file CV cũ mờ trên đĩa cứng server nhằm tiết kiệm bộ nhớ.
    *   Gọi `fileStorageService.storeCvFile(cvFile)` để làm sạch tên file, chèn UUID ngẫu nhiên và lưu file mới vào thư mục vật lý `uploads/cv/`.
    *   Lưu đơn đăng ký mới trạng thái `PENDING` xuống Database.

---

### 3. Danh sách file liên quan
*   **Controller**: `InstructorApplicationController.java`
*   **Service**: `InstructorApplicationService.java`, `InstructorApplicationServiceImpl.java`, `FileStorageService.java`, `FileStorageServiceImpl.java`
*   **Repository (DAO)**: `InstructorApplicationRepository.java`, `UserRepository.java`, `TagRepository.java`
*   **DTO**: `SubmitApplicationRequest.java`
*   **Entity**: `InstructorApplication`, `User`, `Tag`
*   **UI Template**: `apply.html`, `apply-status.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Submit đơn của Service (`InstructorApplicationServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void submitApplication(Long userId, SubmitApplicationRequest request, MultipartFile cvFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() == ERole.INSTRUCTOR) {
            throw new RuntimeException("Bạn đã là giảng viên, không cần nộp đơn.");
        }
        if (applicationRepository.existsByUserAndStatus(user, EApplicationStatus.PENDING)) {
            throw new RuntimeException("Bạn đang có một đơn đang chờ xét duyệt. Vui lòng chờ phản hồi.");
        }

        Optional<InstructorApplication> latestApp = applicationRepository.findTopByUserOrderByCreatedAtDesc(user);

        // Upload CV
        String cvFileName = null;
        String cvFilePath = null;
        boolean hasNewFile = cvFile != null && !cvFile.isEmpty();

        if (hasNewFile) {
            // Xóa file CV mờ của đơn bị từ chối cũ để dọn đĩa cứng
            latestApp.ifPresent(old -> {
                if (old.getStatus() == EApplicationStatus.REJECTED && old.getCvFilePath() != null) {
                    fileStorageService.deleteCvFile(old.getCvFilePath());
                }
            });

            String originalName = cvFile.getOriginalFilename();
            if (originalName != null && !originalName.toLowerCase().endsWith(".pdf")) {
                throw new RuntimeException("Chỉ chấp nhận file PDF cho CV.");
            }
            cvFilePath = fileStorageService.storeCvFile(cvFile);
            cvFileName = org.springframework.util.StringUtils.getFilename(originalName);
        } else {
            if (latestApp.isPresent()) {
                InstructorApplication old = latestApp.get();
                if (old.getStatus() == EApplicationStatus.REJECTED) {
                    cvFileName = old.getCvFileName();
                    cvFilePath = old.getCvFilePath();
                }
            }
        }

        if (cvFilePath == null) {
            throw new RuntimeException("Vui lòng tải lên CV của bạn.");
        }

        // Lấy tags
        Set<Tag> teachingTags = new HashSet<>();
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            teachingTags.addAll(tags);
        }

        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .headline(request.getHeadline())
                .bio(request.getBio())
                .motivation(request.getMotivation())
                .cvFileName(cvFileName)
                .cvFilePath(cvFilePath)
                .teachingTags(teachingTags)
                .status(EApplicationStatus.PENDING)
                .build();

        applicationRepository.save(application);
    }
```
*   **Giải thích code**:
    *   `fileStorageService.deleteCvFile(...)`: Phương thức xóa file CV cũ của đơn bị từ chối khỏi ổ đĩa server khi học viên upload file CV mới, giúp dọn dẹp dung lượng đĩa cứng.
    *   `fileStorageService.storeCvFile(cvFile)`: Lưu file CV nhị phân mới lên server và nhận về đường dẫn lưu trữ duy nhất.
    *   `applicationRepository.save(application)`: Tạo và lưu thực thể đơn ứng tuyển mới ở trạng thái `PENDING`.

#### **Hàm lưu file vật lý trên đĩa (`FileStorageServiceImpl.java`)**:
```java
    @Override
    public String storeCvFile(MultipartFile file) {
        String originalName = org.springframework.util.StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "cv.pdf"
        );

        if (originalName.contains("..")) {
            throw new RuntimeException("Tên file không hợp lệ: " + originalName);
        }

        // Loại bỏ ký tự đặc biệt tiếng Việt để an toàn trên OS File System
        String safeOriginalName = sanitizeFilename(originalName);

        // Ghép UUID ngăn chặn ghi đè trùng tên file
        String storedName = UUID.randomUUID() + "_" + safeOriginalName;
        Path targetPath = this.uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file CV: " + originalName, e);
        }
    }
```
*   **Giải thích code**:
    *   `originalName.contains("..")`: Chống lại lỗ hổng Path Traversal chèn ký tự lùi thư mục ghi đè file hệ thống.
    *   `UUID.randomUUID() + "_" + ...`: Sinh ra chuỗi định danh duy nhất (UUID) ghép vào trước tên file, đảm bảo các học viên khác nhau tải lên file trùng tên (ví dụ: `CV.pdf`) không bị ghi đè dữ liệu của nhau.
    *   `Files.copy(...)`: Thực hiện sao chép luồng nhị phân của file vào thư mục vật lý được định nghĩa trên ổ cứng máy chủ.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`instructor_applications`**: Bảng chính ghi nhận đơn xin làm giảng viên, thông tin bio và đường dẫn file CV lưu trên server.
*   **`application_tag_mappings`**: Bảng trung gian ghi nhận các tag chủ đề muốn giảng dạy.

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   Thẻ form bắt buộc phải khai báo thuộc tính `enctype="multipart/form-data"` để trình duyệt gửi file nhị phân của CV lên server.
*   Giao diện hiển thị cờ thông báo nếu đã có sẵn CV cũ từ đơn bị từ chối trước đó, cho phép học viên dùng lại CV cũ nếu không muốn tải tệp mới lên.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Chủ động xóa file CV cũ khi người dùng upload file mới**:
    *   *Ý nghĩa*: Tránh rác dữ liệu trên Server. Nếu học viên nộp đơn đi nộp đơn lại 5 lần do bị từ chối và tải lên 5 file CV mới, việc lưu trữ cả 5 file PDF (mỗi file vài MB) sẽ làm ổ đĩa cứng của máy chủ nhanh đầy. Bằng cách xóa tệp tin cũ bị từ chối trước khi lưu tệp tin mới, hệ thống giữ cho kho lưu trữ luôn sạch sẽ.

### 8. Security (Bảo mật)
*   **Chặn file độc hại**: Kiểm tra cứng đuôi tệp tin phải là `.pdf` để ngăn chặn hacker tải lên file script mã độc (như `.jsp`, `.exe`) thực thi mã trái phép trên máy chủ.
*   **Chống Path Traversal**: Chặn ký tự `..` trong tên file để tin tặc không thể điều hướng ghi đè các file hệ thống quan trọng ngoài thư mục chỉ định.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Trải nghiệm người dùng tốt nhờ điền lại thông tin cũ và giữ nguyên CV cũ. Mã hóa tên file bằng UUID loại bỏ khả năng ghi đè file trùng tên.
*   *Điểm yếu*: File CV đang được lưu cục bộ trên ổ cứng của server chạy ứng dụng. Nếu chạy hệ thống đa server (Auto-Scaling) đằng sau Load Balancer, một server A sẽ không đọc được file CV lưu trên ổ cứng của server B.
*   *Đề xuất*: Chuyển đổi cấu hình để lưu file CV lên các dịch vụ lưu trữ đám mây tập trung như Amazon S3.

---

## WALLET - 1. Nạp tiền vào Ví

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép học viên nạp tiền từ tài khoản ngân hàng cá nhân vào ví điện tử của hệ thống fCourse thông qua cổng thanh toán trung gian **VNPay Sandbox**. Số tiền trong ví sau khi nạp thành công sẽ được dùng để mua các khóa học.
*   **Đối tượng sử dụng**: Tất cả người dùng đã đăng nhập hệ thống (chủ yếu là Học viên).
*   **Điều kiện sử dụng**: Số tiền nạp phải lớn hơn 0đ.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: wallet.html] (Học viên nhập số tiền 200.000đ và click "Nạp tiền")
   ↓ (Gửi yêu cầu Ajax POST đến /api/wallet/deposit?amount=200000)
[Controller: WalletApiController.java] (Hàm deposit)
   ↓ (Lấy IP client, xây dựng callback url động: http://localhost:8080/wallet/callback)
[Service: WalletServiceImpl.java] (Hàm createDepositUrl)
   ↓ (Khởi tạo bản ghi giao dịch WalletTransaction trạng thái PENDING)
[DAO: WalletTransactionRepository] 
   ↓ (Thực thi INSERT vào DB, sinh ID giao dịch, ví dụ: transactionId = 42)
[Service: VNPayService.java] (Hàm createPaymentUrl)
   ↓ (Nhân số tiền x100, sắp xếp tham số, mã hóa HMAC-SHA512 để sinh SecureHash)
[Controller: WalletApiController.java] (Trả về JSON chứa redirectUrl)
   ↓ (JavaScript đón nhận JSON và chuyển hướng trình duyệt)
[Cổng thanh toán VNPay Sandbox] (Hiển thị trang quét mã QR / Nhập thẻ ngân hàng)
```

#### **Ví dụ thực tế**:
Học viên Nguyễn Văn A (`userId = 5`) có số dư ví hiện tại là `0đ`. Anh A muốn nạp **`200.000đ`** vào ví qua VNPay.
1.  **Tạo yêu cầu nạp**:
    *   Hàm `deposit` trong `WalletApiController` nhận `amount = 200000`. Gọi `walletService.createDepositUrl(5L, 200000, "127.0.0.1", returnUrl)`.
    *   Hàm `createDepositUrl` tạo thực thể `WalletTransaction` có `amount = 200000`, `status = PENDING`.
    *   Lưu giao dịch sinh ID `transactionId = 42`.
    *   Gọi `vnpayService.createPaymentUrl(42L, 200000, "127.0.0.1", returnUrl)`.
    *   Trong `VNPayService.java`: Nhân số tiền với 100 thành `20000000`. Sắp xếp các tham số alphabet, dùng khóa bí mật `hash-secret` mã hóa HMAC-SHA512 sinh SecureHash. Trả về URL thanh toán hoàn chỉnh.
    *   Client nhận URL và redirect anh A sang cổng VNPay.
2.  **Callback xử lý kết quả**:
    *   Anh A thanh toán thành công, VNPay redirect về `/wallet/callback?vnp_ResponseCode=00&vnp_TxnRef=42...`.
    *   Hàm `vnpayCallback` trong `WalletController` chuyển giao cho `walletService.processVNPayCallback`.
    *   Gọi `vnpayService.verifyCallback(fields)`. Chữ ký xác thực thành công.
    *   Đọc `vnp_ResponseCode` = `"00"` (Thành công) và `vnp_TxnRef` = `"42"`.
    *   Cập nhật trạng thái giao dịch `42` thành `SUCCESS` và cộng `200.000đ` vào số dư ví của anh A trong Database.

---

### 3. Danh sách file liên quan
*   **Controller**: `WalletApiController.java`, `WalletController.java`
*   **Service**: `WalletService.java`, `WalletServiceImpl.java`, `VNPayService.java`
*   **Repository (DAO)**: `WalletRepository.java`, `WalletTransactionRepository.java`
*   **Entity**: `Wallet`, `WalletTransaction`
*   **UI Template**: `wallet.html`, `wallet-callback.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm Tạo URL nạp tiền của VNPay (`VNPayService.java`)**:
```java
    public String createPaymentUrl(Long transactionId, long amount, String ipAddress, String returnUrl) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Nap tien vao vi fCourse - Giao dich ID: " + transactionId;
        String vnp_OrderType = "other";
        String vnp_TxnRef = String.valueOf(transactionId);
        
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân 100 lần gửi xu
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames); // Sắp xếp alphabet các trường dữ liệu

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            
            hashData.append(fieldName).append('=').append(queryUrlEncode(fieldValue));
            query.append(queryUrlEncode(fieldName)).append('=').append(queryUrlEncode(fieldValue));
            
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }

        String queryUrl = query.toString();
        // Sinh chữ ký bảo mật bảo vệ toàn vẹn dữ liệu
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnp_Url + "?" + queryUrl;
    }
```
*   **Giải thích code**:
    *   `amount * 100`: Chuyển đổi số tiền VNĐ thực tế sang đơn vị nhỏ nhất (cent/xu) của cổng thanh toán VNPay.
    *   `Collections.sort(fieldNames)`: Bắt buộc sắp xếp các trường dữ liệu theo thứ tự bảng chữ cái alphabet để tạo chuỗi hashing chuẩn hóa đồng bộ với VNPay.
    *   `hmacSHA512(vnp_HashSecret, hashData.toString())`: Sử dụng mã bảo mật secret key băm HMAC-SHA512 tạo chữ ký bảo vệ dữ liệu truyền đi không bị giả mạo.

#### **Hàm xử lý VNPay Callback (`WalletServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public boolean processVNPayCallback(Map<String, String> fields) {
        // 1. Xác thực chữ ký bảo mật callback từ VNPay
        if (!vnpayService.verifyCallback(fields)) {
            return false;
        }

        String responseCode = fields.get("vnp_ResponseCode");
        String txnRef = fields.get("vnp_TxnRef");
        if (txnRef == null) return false;

        Long transactionId = Long.parseLong(txnRef);
        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch: " + transactionId));

        if (transaction.getStatus() != ETransactionStatus.PENDING) {
            return "00".equals(responseCode); // Đơn đã được xử lý từ trước
        }

        Wallet wallet = transaction.getWallet();

        if ("00".equals(responseCode)) {
            // Thanh toán thành công -> Cộng tiền vào ví
            transaction.setStatus(ETransactionStatus.SUCCESS);
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(wallet);
            walletTransactionRepository.save(transaction);
            return true;
        } else {
            // Thất bại
            transaction.setStatus(ETransactionStatus.FAIL);
            walletTransactionRepository.save(transaction);
            return false;
        }
    }
```
*   **Giải thích code**:
    *   `vnpayService.verifyCallback(fields)`: Xác thực lại chữ ký do VNPay gửi trả về bằng cách băm lại mảng tham số nhận được. Nếu chữ ký không khớp, lập tức dừng và trả về thất bại (chống tấn công tự cộng tiền ảo).
    *   `"00".equals(responseCode)`: Cổng VNPay quy định mã `"00"` đại diện cho giao dịch thanh toán thành công.
    *   `wallet.getBalance().add(transaction.getAmount())`: Cộng dồn tiền nạp vào số dư ví của học viên và lưu lại DB.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`wallets`**: Lưu trữ số dư ví (`balance`) của người dùng bằng kiểu `decimal(18, 2)` để chống sai lệch làm tròn số tiền.
*   **`wallet_transactions`**: Lưu trữ chi tiết giao dịch nạp tiền. Lần 1 lưu trạng thái `PENDING`, lần 2 (Callback) cập nhật thành `SUCCESS` hoặc `FAIL`.

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   Trên giao diện `wallet.html`, nút "Nạp tiền" gọi Ajax POST gửi số tiền lên API để kiểm tra và nhận link redirect trước khi thực hiện chuyển trang bằng `window.location.href`.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Quy tắc nhân số tiền nạp với 100**:
    *   *Ý nghĩa*: VNPay quy định số tiền thanh toán không được có dấu phẩy thập phân và đơn vị gửi lên phải là đơn vị tiền tệ nhỏ nhất. Đối với Việt Nam Đồng (VND), đơn vị nhỏ nhất được ngầm hiểu là 100 xu = 1đ. Do đó, nếu nạp 200.000đ, ta phải gửi đi giá trị là `200000 * 100 = 20000000`. Cổng thanh toán VNPay sẽ tự động chia 100 để hiển thị số tiền thanh toán chính xác là 200.000đ trên UI của họ.

### 8. Security (Bảo mật)
*   **Xác thực chữ ký HMAC-SHA512**:
    *   *Ý nghĩa*: Ngăn chặn tin tặc giả lập request callback để tự cộng tiền ảo vào ví. Chữ ký HMAC-SHA512 sử dụng khóa bí mật chỉ có hệ thống fCourse và VNPay biết, đảm bảo dữ liệu không bị thay đổi trên đường truyền.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Xử lý chữ ký chuẩn xác, tự động sinh URL Callback động linh hoạt chạy trên cả local và production.
*   *Điểm yếu*: Khóa bí mật `vnpay.hash-secret` đang bị viết thô (hardcode) trong file `application.properties`, có nguy cơ bị lộ khi đẩy code lên Git.
*   *Đề xuất*: Chuyển khóa bí mật này vào biến môi trường hệ thống.

---

## WALLET - 2. Thanh toán bằng Ví

### 1. Mục đích nghiệp vụ
*   **Mô tả**: Cho phép học viên dùng số dư ví điện tử của mình để thanh toán mua một khóa học đơn lẻ (có áp dụng mã giảm giá voucher) hoặc thanh toán giỏ hàng (Cart). Sau khi thanh toán thành công, học viên được cấp quyền truy cập học, đồng thời dòng tiền thực tế thu được sẽ được tự động chia sẻ chiết khấu cho Giảng viên và Admin hệ thống.
*   **Đối tượng sử dụng**: Tất cả người dùng đã đăng nhập hệ thống (vai trò Học viên).
*   **Điều kiện sử dụng**: Học viên không được tự mua khóa học của chính mình, chưa sở hữu khóa học từ trước và số dư ví khả dụng phải lớn hơn hoặc bằng tổng số tiền thực tế cần thanh toán.

### 2. Luồng hoạt động chi tiết (Class to Class Flow) & Ví dụ minh họa
```
[UI: course-detail.html] (Học viên nhập mã giảm giá và click "Thanh toán ngay")
   ↓ (Gửi yêu cầu Ajax POST đến /api/wallet/pay/course/{courseId}?voucherCode=...)
[Controller: WalletApiController.java] (Hàm payCourse)
   ↓ 
[Service: WalletServiceImpl.java] (Hàm purchaseCourse)
   ↓ (Kiểm tra các ràng buộc: Không tự mua bài của mình, không mua khóa đã học)
[DAO: EnrollmentRepository & CourseRepository]
   ↓ (Gọi Service tính toán số tiền thực trả và phần chia doanh thu)
[Service: VoucherServiceImpl.java] (Hàm calculateVoucher)
   ↓ (Tính toán giá sau giảm giá, phân bổ tiền cho Giảng viên và Admin dựa trên creatorRole)
[Service: WalletServiceImpl.java] (Kiểm tra số dư ví học viên)
   ↓ (Khấu trừ ví học viên, cộng ví Giảng viên, cộng ví Admin)
[DAO: WalletRepository & WalletTransactionRepository]
   ↓ (Thực thi tạo Order, OrderItem, Enrollment, và 3 bản ghi giao dịch WalletTransaction)
[Database: Bảng orders, order_items, enrollments, wallets, wallet_transactions]
   ↓ (Đánh dấu voucher đã dùng trong ví voucher học viên)
[Service: VoucherServiceImpl.java] (Hàm confirmUseVoucher)
   ↓ (Trả về JSON phản hồi thành công và trang chuyển hướng)
[Controller: WalletApiController.java] → Trả về URL học tập /course/{courseId} cho Client.
```

#### **Ví dụ thực tế**:
Học viên Nguyễn Văn A (`userId = 2`) mua khóa học *"Java Core nâng cao"* (`courseId = 5`) do giảng viên Trần Bính giảng dạy có giá bán gốc là **`500.000đ`**. Anh A áp dụng mã giảm giá **`VOUCHER50K`** (giảm `50.000đ`) do **Giảng viên Trần Bính tự tạo** phát hành. Số dư ví anh A là `600.000đ`.
1.  **Xử lý tại Controller**:
    *   Hàm `payCourse` tiếp nhận request và gọi `walletService.purchaseCourse(2L, 5L, "VOUCHER50K")`.
2.  **Xử lý tại Service (Tính tiền)**:
    *   Học viên A không phải giảng viên khóa học và chưa sở hữu khóa học (Hợp lệ).
    *   Gọi `voucherService.calculateVoucher` tính toán:
        *   Giá thực trả: `actualPaid` = `500.000đ - 50.000đ` = **`450.000đ`**.
        *   Phân bổ tiền: Vì Voucher do Giảng viên tạo (`creatorRole = 'INSTRUCTOR'`), giảng viên gánh 100% giảm giá. Sàn (Admin) thu đủ 20% trên giá gốc:
            *   Sàn (Admin) nhận: `500.000đ * 20%` = **`100.000đ`**.
            *   Giảng viên nhận: `450.000đ - 100.000đ` = **`350.000đ`**.
3.  **Khấu trừ và phân bổ dòng tiền**:
    *   Khấu trừ ví học viên A: `600.000đ - 450.000đ` = `150.000đ`.
    *   Tạo `Order`, `OrderItem` và `Enrollment` mở quyền học cho A.
    *   Ghi nhận giao dịch nợ học viên A: `WalletTransaction` loại `PAYMENT` - `SUCCESS` trị giá `450.000đ`.
    *   Cộng tiền ví giảng viên Trần Bính: `balance = balance + 350.000đ` và tạo giao dịch doanh thu giảng viên.
    *   Cộng tiền ví Admin: `balance = balance + 100.000đ` và tạo giao dịch doanh thu hệ thống.
    *   Đánh dấu voucher đã dùng. Trả về JSON thành công điều hướng về trang học.

---

### 3. Danh sách file liên quan
*   **Controller**: `WalletApiController.java`
*   **Service**: `WalletService.java`, `WalletServiceImpl.java`, `VoucherService.java`, `VoucherServiceImpl.java`
*   **Repository (DAO)**: `WalletRepository.java`, `WalletTransactionRepository.java`, `OrderRepository.java`, `OrderItemRepository.java`, `EnrollmentRepository.java`, `CartItemRepository.java`, `UserRepository.java`
*   **Entity**: `Order`, `OrderItem`, `Enrollment`, `CartItem`, `Wallet`, `WalletTransaction`
*   **UI Template**: `course-detail.html`, `cart.html`

---

### 4. Code ví dụ thực tế & Giải thích chi tiết

#### **Hàm tính toán chia doanh thu khi có Voucher (`VoucherServiceImpl.java`)**:
```java
    @Override
    @Transactional(readOnly = true)
    public VoucherApplyResponse calculateVoucher(String code, Long courseId, Long studentId) {
        Voucher voucher = voucherRepository.findByCodeAndDeleteFlagFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại hoặc đã bị xóa!"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Khóa học không tồn tại!"));

        BigDecimal originalPrice = course.getCurrentPublishedVersion() != null
                ? course.getCurrentPublishedVersion().getPrice()
                : BigDecimal.ZERO;
        
        // ... Validate hạn dùng, limitUsage ...

        BigDecimal discountValue = voucher.getDiscountValue();
        BigDecimal actualPaid = originalPrice.subtract(discountValue).max(BigDecimal.ZERO);

        BigDecimal instructorShare;
        BigDecimal adminShare;

        // Phân chia gánh chịu chi phí giảm giá
        if ("ADMIN".equals(voucher.getCreatorRole())) {
            // ADMIN tạo -> Admin gánh 100% giảm giá. Giảng viên hưởng trọn 80% giá gốc.
            instructorShare = originalPrice.multiply(new BigDecimal("0.8"));
            adminShare = actualPaid.subtract(instructorShare).max(BigDecimal.ZERO);
        } else {
            // INSTRUCTOR tạo -> Giảng viên tự gánh 100% giảm giá. Sàn (Admin) thu đủ 20% giá gốc.
            adminShare = originalPrice.multiply(new BigDecimal("0.2"));
            instructorShare = actualPaid.subtract(adminShare).max(BigDecimal.ZERO);
        }

        return VoucherApplyResponse.builder()
                .success(true)
                .actualPaid(actualPaid)
                .instructorShare(instructorShare)
                .adminShare(adminShare)
                .build();
    }
```
*   **Giải thích code**:
    *   `actualPaid`: Giá thực tế học sinh phải trả sau khi lấy giá gốc trừ tiền giảm giá của voucher.
    *   `if ("ADMIN".equals(voucher.getCreatorRole()))`: Nếu do Admin tạo (Voucher khuyến mãi hệ thống), Giảng viên nhận đủ 80% của **giá gốc** khóa học (`originalPrice * 0.8`), số tiền giảm giá do Admin tự gánh chịu từ phần thu của mình.
    *   `else`: Nếu do Giảng viên tự tạo (Voucher khuyến mãi riêng của khóa học), Admin/Sàn vẫn thu trọn vẹn 20% tính trên **giá gốc** của khóa học (`originalPrice * 0.2`), số tiền giảm giá sẽ do Giảng viên tự gánh hoàn toàn.

#### **Trừ tiền ví và cộng phân bổ doanh thu (`WalletServiceImpl.java`)**:
```java
    @Override
    @Transactional
    public void purchaseCourse(Long userId, Long courseId, String voucherCode) {
        User student = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        
        // ... Kiểm tra ví học viên, kiểm tra giảng viên tự mua, kiểm tra đã mua ...
        BigDecimal originalPrice = course.getCurrentPublishedVersion().getPrice();
        BigDecimal priceToPay = originalPrice;
        BigDecimal instructorAmount = originalPrice.multiply(new BigDecimal("0.8"));
        BigDecimal adminAmount = originalPrice.multiply(new BigDecimal("0.2"));

        if (StringUtils.hasText(voucherCode)) {
            VoucherApplyResponse vResp = voucherService.calculateVoucher(voucherCode, courseId, userId);
            priceToPay = vResp.getActualPaid();
            instructorAmount = vResp.getInstructorShare();
            adminAmount = vResp.getAdminShare();
        }

        // 1. Trừ tiền ví học viên
        Wallet studentWallet = student.getWallet();
        studentWallet.setBalance(studentWallet.getBalance().subtract(priceToPay));
        walletRepository.save(studentWallet);

        // 2. Lưu hóa đơn và ghi nhận giao dịch của học viên
        // ... tạo Order, OrderItem, Enrollment ...

        // 3. Phân bổ doanh thu cho Giảng viên
        if (course.getInstructor() != null) {
            Wallet instWallet = course.getInstructor().getWallet();
            instWallet.setBalance(instWallet.getBalance().add(instructorAmount));
            walletRepository.save(instWallet);
            
            // Lưu WalletTransaction ghi nhận doanh thu của Giảng viên
            walletTransactionRepository.save(WalletTransaction.builder()
                    .wallet(instWallet)
                    .amount(instructorAmount)
                    .transactionType(ETransactionType.PAYMENT)
                    .status(ETransactionStatus.SUCCESS)
                    .description("Nhan doanh thu tu khoa hoc: " + course.getCurrentPublishedVersion().getTitle())
                    .build());
        }

        // 4. Phân bổ doanh thu cho Admin
        User admin = userRepository.findByEmail("admin@fcourse.vn").orElseThrow();
        Wallet adminWallet = admin.getWallet();
        adminWallet.setBalance(adminWallet.getBalance().add(adminAmount));
        walletRepository.save(adminWallet);
        // ... lưu giao dịch doanh thu cho ví Admin ...
    }
```
*   **Giải thích code**:
    *   `studentWallet.setBalance(...)`: Thực hiện trừ tiền ví học viên dựa vào giá thực trả (`priceToPay`).
    *   `instWallet.getBalance().add(instructorAmount)`: Thực hiện cộng dồn doanh thu được chia sẻ (`instructorAmount`) vào ví của Giảng viên và lưu lại DB.
    *   `userRepository.findByEmail("admin@fcourse.vn")`: Tìm tài khoản Admin hệ thống, thực hiện cộng số tiền chiết khấu thuộc về sàn (`adminAmount`) vào ví Admin.
    *   `walletTransactionRepository.save(...)`: Ghi nhận các lịch sử giao dịch tương ứng cho cả học viên, giảng viên và admin để đảm bảo tính minh bạch tài chính.

---

### 5. Phân tích Database (Thao tác bảng dữ liệu)
*   **`orders`** & **`order_items`**: Ghi nhận đơn hàng và giá mua thực tế tại thời điểm thanh toán.
*   **`enrollments`**: Mở quyền cho học viên truy cập vào giáo trình học.
*   **`wallets`** & **`wallet_transactions`**: Cập nhật số dư và lịch sử biến động tiền cho cả 3 đối tượng (Học viên, Giảng viên, Admin).

### 6. Phân tích UI & JS (Thymeleaf & Modal)
*   Giao diện trang chi tiết khóa học cung cấp ô nhập mã voucher và nút "Thanh toán bằng ví". JavaScript gọi API POST thanh toán, đón nhận JSON phản hồi và điều hướng học viên sang trang học tập nếu thành công.

### 7. Business Logic (Ý nghĩa nghiệp vụ)
*   **Phân bổ gánh chịu giảm giá dựa trên người tạo voucher (`creatorRole`)**:
    *   *Ý nghĩa*: Đảm bảo công bằng thương mại. Nếu Admin tạo Voucher (kích cầu toàn sàn), Admin tự gánh giảm giá, Giảng viên nhận đủ 80% giá gốc. Nếu Giảng viên tạo Voucher (khuyến mãi riêng), Giảng viên tự gánh giảm giá, Admin thu đủ 20% giá gốc.

### 8. Security (Bảo mật)
*   Toàn bộ quá trình tính tiền và phân bổ được xử lý cô lập tại Server-side dựa vào dữ liệu DB, ngăn chặn hacker thay đổi giá bán thô trên trình duyệt. Lớp `@Transactional` giúp tự động hoàn trả trạng thái ban đầu (rollback) nếu phát sinh lỗi trong bất kỳ bước lưu dữ liệu nào.

### 9. Đánh giá Chất lượng Code
*   *Điểm mạnh*: Nghiệp vụ phân chia dòng tiền cực kỳ chặt chẽ, công thức tính toán thông minh, giao dịch bọc Transaction an toàn tuyệt đối.
*   *Điểm yếu*: Email tài khoản Admin nhận tiền bị hardcode là `"admin@fcourse.vn"`. Nếu tài khoản này bị thay đổi, luồng mua khóa học sẽ bị lỗi nặng.
*   *Đề xuất*: Cấu hình động ví nhận tiền hệ thống thông qua cài đặt database.
