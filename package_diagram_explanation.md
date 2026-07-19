# Giải Thích Chi Tiết Sơ Đồ Gói (Package Diagram) - Hệ Thống My Learning Path

Tài liệu này giải thích chi tiết về cấu trúc các gói và quan hệ phụ thuộc trong hệ thống **My Learning Path** (dự án Spring Boot).

---

## I. Cấu Trúc Tổng Quan Và Cách Bố Trí (Layout & Structure)

Sơ đồ gói phân rã dự án thành **8 gói chính** thuộc namespace gốc `org.swp.my_learning_path` (đứng đầu bởi tệp chạy chính `MyLearningPathApplication.java`).

Để dễ theo dõi, sơ đồ gói được bố trí thành **3 cột lớn** theo luồng phụ thuộc một chiều từ phải qua trái và từ trên xuống dưới:

```
    [CỘT 1: LÕI DỮ LIỆU]         [CỘT 2: TRỤC XỬ LÝ CHÍNH]        [CỘT 3: TIỆN ÍCH]
 ┌──────────────────────┐         ┌──────────────────────┐         ┌─────────────────┐
 │                      │  ◄────  │      controller      │  ◄────  │  configuration  │
 │        entity        │  ◄────  │          │           │         │  (Gói cấu hình  │
 │    (Hộp đứng lớn     │         │          ▼           │         │   ở trạng thái  │
 │     làm lõi hệ       │  ◄────  │       service        │         │     cô lập)     │
 │       thống)         │         │          │           │         └─────────────────┘
 │                      │  ◄────  │          ▼           │
 ├──────────────────────┤         │       security       │
 │         dto          │  ◄────  │          │           │
 ├──────────────────────┤         │          ▼           │
 │       constant       │  ◄────  │      repository      │
 └──────────────────────┘         └──────────────────────┘
```

### Giải thích vai trò của các Cột:
1. **Cột 1 (Bên trái) - Lõi dữ liệu (Data Core)**:
   * **`entity`**: Được vẽ dạng **hình hộp đứng dài** (giống gói `bean` trong ví dụ mẫu) chứa các thực thể JPA ánh xạ trực tiếp xuống Database. Mọi lớp xử lý logic nghiệp vụ hay điều khiển API đều cần dữ liệu từ gói này.
   * **`dto`**: Các lớp trung gian chuyển tải dữ liệu giữa các lớp và Client để tránh lộ cấu trúc thực thể.
   * **`constant`**: Chứa các Enum định nghĩa trạng thái tĩnh cho hệ thống (ví dụ: vai trò người dùng, trạng thái giao dịch).
2. **Cột 2 (Ở giữa) - Trục xử lý (Processing Flow)**: Đi theo kiến trúc 3 lớp tiêu chuẩn của Spring Boot:
   * **`controller`** tiếp nhận HTTP Request $\rightarrow$ gọi sang **`service`** xử lý nghiệp vụ $\rightarrow$ gọi **`security`** xác thực $\rightarrow$ gọi **`repository`** để truy vấn DB.
3. **Cột 3 (Bên phải) - Cấu hình hệ thống (Configuration)**:
   * **`configuration`**: Chứa các cấu hình tổng quát độc lập của hệ thống (Auditing, Spring Security, AWS S3 Config). Nó chỉ khai báo các Bean cấu hình chạy lúc khởi động và không phụ thuộc trực tiếp vào các gói nghiệp vụ khác của mã nguồn tại thời điểm biên dịch.

---

## II. Phân Tích Chi Tiết Quan Hệ Phụ Thuộc Giữa Các Gói

Trong Java, mỗi lệnh `import` của một lớp thuộc gói này gọi tới lớp thuộc gói khác tạo ra một đường nét đứt biểu thị quan hệ phụ thuộc trong UML Package Diagram.

### 1. Phụ thuộc của nhóm Controller (Tầng Biên / API)
Gói `controller` nằm ở vị trí cao nhất trên trục xử lý, phụ thuộc vào hầu hết các gói khác:
*   **`controller` $\rightarrow$ `service`**: Controller gọi tầng Service để xử lý logic. Ví dụ: `AdminController` import và sử dụng các giao diện `AdminService`, `TagService`.
*   **`controller` $\rightarrow$ `dto`**: Controller tiếp nhận Request DTO và trả về Response DTO cho người dùng. Ví dụ: `AdminController` import `CreateUserRequest`, `TagRequest`.
*   **`controller` $\rightarrow$ `security`**: Dùng để lấy thông tin phiên đăng nhập của người dùng hiện tại thông qua lớp `CustomUserDetails`.
*   **`controller` $\rightarrow$ `entity`**: Trả trực tiếp các thực thể JPA trong một số trường hợp hiển thị đơn giản. Ví dụ: `AdminController` import `User`, `Tag`.
*   **`controller` $\rightarrow$ `constant`**: Kiểm tra trạng thái dữ liệu tĩnh bằng các Enum. Ví dụ: `AdminController` import `ERole`, `EAccountStatus`.
*   **Mối quan hệ "Lối tắt" (`controller` $\rightarrow$ `repository`)**:
    *   **Hiện trạng**: Trên sơ đồ, mối quan hệ này được vẽ bằng **nét liền** biểu thị sự bất thường kiến trúc (Bypass Layer).
    *   **Lý do**: Lớp `InstructorUIController` import trực tiếp `CourseRepository`, `CourseSectionRepository`, `LessonRepository` để tải nhanh dữ liệu tĩnh hiển thị lên UI mà không gọi qua các lớp Service tương ứng.

### 2. Phụ thuộc của nhóm Service (Tầng Nghiệp vụ)
Gói `service` là trung tâm tính toán nghiệp vụ, phụ thuộc vào:
*   **`service` $\rightarrow$ `repository`**: Service gọi các Repository thực hiện truy vấn DB. Ví dụ: `AdminServiceImpl` import `UserRepository` để tìm kiếm tài khoản.
*   **`service` $\rightarrow$ `entity`**: Nhận dữ liệu thực thể từ repository để tính toán hoặc cập nhật trạng thái thực thể. Ví dụ: `InstructorCourseService` xử lý thực thể `Course`.
*   **`service` $\rightarrow$ `dto`**: Trích xuất dữ liệu thô từ thực thể chuyển sang định dạng DTO gửi ngược lại cho Controller. Ví dụ: `AdminDashboardServiceImpl` sử dụng `AdminDashboardDTO`.
*   **`service` $\rightarrow$ `constant`**: Thiết lập hoặc lọc trạng thái thông qua các Enum. Ví dụ: `EmailService` thiết lập trạng thái thư gửi qua `EEmailStatus`.
*   **`service` $\rightarrow$ `security`**: Kiểm tra phân quyền nghiệp vụ nội bộ. Ví dụ: `BlogServiceImpl` import `CustomUserDetails` để kiểm tra tác giả Blog có quyền sửa bài viết hay không.

### 3. Phụ thuộc của nhóm Security (Tầng Bảo mật)
Gói `security` phụ thuộc vào:
*   **`security` $\rightarrow$ `repository`**: `CustomUserDetailsService` import `UserRepository` để truy xuất tài khoản từ DB bằng email.
*   **`security` $\rightarrow$ `entity`**: Trực tiếp sử dụng thực thể `User` để đóng gói dữ liệu đăng nhập vào đối tượng kế thừa `UserDetails` là `CustomUserDetails`.

### 4. Phụ thuộc của nhóm Repository (Tầng Lưu trữ)
Gói `repository` phụ thuộc vào:
*   **`repository` $\rightarrow$ `entity`**: Mỗi Interface Repository định nghĩa kiểu thực thể quản lý. Ví dụ: `UserRepository` kế thừa `JpaRepository<User, Long>`.
*   **`repository` $\rightarrow$ `constant`**: Thực hiện các câu lệnh truy vấn lọc theo trạng thái lưu dưới dạng enum. Ví dụ: `UserRepository` import `EAccountStatus` để tìm các tài khoản đang hoạt động.

### 5. Phụ thuộc của nhóm DTO (Tầng Trung chuyển dữ liệu)
Gói `dto` phụ thuộc vào:
*   **`dto` $\rightarrow$ `entity`**: Chứa thông tin các lớp thực thể liên kết làm thành phần thuộc tính. Ví dụ: `AdminDashboardDTO` import thực thể `Order`, `InstructorApplication`.
*   **`dto` $\rightarrow$ `constant`**: Định nghĩa thuộc tính trạng thái cho các DTO gửi/nhận. Ví dụ: `UserResponse` import `ERole`, `EAccountStatus`.

### 6. Phụ thuộc của nhóm Entity (Tầng Cốt lõi)
*   **`entity` $\rightarrow$ `constant`**: Các trường trong các bảng database được ánh xạ trực tiếp thành các Enum để kiểm soát chặt chẽ giá trị đầu vào của cột (ví dụ: vai trò của `User` là `ERole`).
