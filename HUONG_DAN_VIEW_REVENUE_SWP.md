# HƯỚNG DẪN XÂY DỰNG TÍNH NĂNG XEM DOANH THU (VIEW REVENUE) - DỰ ÁN SWP

Tài liệu này hướng dẫn chi tiết từng bước (step-by-step) để bạn tự xây dựng tính năng xem doanh thu cho cả **Giảng viên (Instructor)** và **Quản trị viên (Admin)** theo đúng cấu trúc package của dự án **SWP (`org.swp.my_learning_path`)**.

---

## 📌 Ý TƯỞNG THIẾT KẾ & ĐỊNH NGHĨA NGHIỆP VỤ

1. **Doanh thu của Giảng viên (Instructor):** Là tổng tiền thu được từ các học viên mua các khóa học thuộc quyền sở hữu của Giảng viên đó.
   - *Công thức:* Tổng cột `price` trong bảng `order_items` của các khóa học (`Course`) có `instructor_id` bằng ID của Giảng viên đăng nhập, thuộc các đơn hàng (`orders`) có `payment_status = 'SUCCESS'`.
2. **Doanh thu của Admin:** Là tổng tiền tất cả các khóa học đã bán thành công trên toàn hệ thống.
   - *Công thức:* Tổng cột `total_amount` của tất cả các đơn hàng (`orders`) có `payment_status = 'SUCCESS'`.

---

## 🛠 CÁC BƯỚC THỰC HIỆN CHI TIẾT

### BƯỚC 1: TẠO CÁC REPOSITORY TRUY VẤN CƠ SỞ DỮ LIỆU
Bạn hãy tạo mới 2 file này trong package `org.swp.my_learning_path.repository`.

#### 1. Tạo file `OrderRepository.java`
```java
package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách các đơn hàng thành công để làm lịch sử giao dịch cho Admin
    List<Order> findByPaymentStatusOrderByCreatedAtDesc(ETransactionStatus status);

    // Tính tổng doanh thu toàn hệ thống cho Admin
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = :status")
    BigDecimal sumTotalRevenue(ETransactionStatus status);
}
```

#### 2. Tạo file `OrderItemRepository.java`
```java
package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 1. Tính tổng doanh thu của một Giảng viên cụ thể
    @Query("SELECT SUM(oi.price) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND o.paymentStatus = :status")
    BigDecimal sumRevenueByInstructor(@Param("instructorId") Long instructorId, 
                                      @Param("status") ETransactionStatus status);

    // 2. Lấy danh sách các giao dịch bán khóa học của một Giảng viên cụ thể
    @Query("SELECT oi FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND o.paymentStatus = :status " +
           "ORDER BY o.createdAt DESC")
    List<OrderItem> findSuccessfulSalesByInstructor(@Param("instructorId") Long instructorId, 
                                                    @Param("status") ETransactionStatus status);
}
```

---

### BƯỚC 2: TẠO REVENUE SERVICE XỬ LÝ LOGIC NGHIỆP VỤ
Tạo Service interface và Service implementation để đóng gói logic xử lý trong package `org.swp.my_learning_path.service`.

#### 1. Tạo file `RevenueService.java`
```java
package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface RevenueService {
    // Dành cho Giảng viên
    BigDecimal getInstructorTotalRevenue(Long instructorId);
    List<OrderItem> getInstructorSalesHistory(Long instructorId);

    // Dành cho Admin
    BigDecimal getAdminTotalRevenue();
    List<Order> getAdminSalesHistory();
}
```

#### 2. Tạo file `RevenueServiceImpl.java`
```java
package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.OrderItem;
import org.swp.my_learning_path.repository.OrderItemRepository;
import org.swp.my_learning_path.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public BigDecimal getInstructorTotalRevenue(Long instructorId) {
        BigDecimal revenue = orderItemRepository.sumRevenueByInstructor(instructorId, ETransactionStatus.SUCCESS);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public List<OrderItem> getInstructorSalesHistory(Long instructorId) {
        return orderItemRepository.findSuccessfulSalesByInstructor(instructorId, ETransactionStatus.SUCCESS);
    }

    @Override
    public BigDecimal getAdminTotalRevenue() {
        BigDecimal revenue = orderRepository.sumTotalRevenue(ETransactionStatus.SUCCESS);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public List<Order> getAdminSalesHistory() {
        return orderRepository.findByPaymentStatusOrderByCreatedAtDesc(ETransactionStatus.SUCCESS);
    }
}
```

---

### BƯỚC 3: TẠO REVENUE CONTROLLER ĐÓN NHẬN REQUEST
Tạo file `RevenueController.java` trong package `org.swp.my_learning_path.controller`.

```java
package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    public String viewRevenue(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        String role = user.getRole().name(); // STUDENT, INSTRUCTOR, ADMIN

        model.addAttribute("user", user);

        if ("ADMIN".equals(role)) {
            model.addAttribute("totalRevenue", revenueService.getAdminTotalRevenue());
            model.addAttribute("salesHistory", revenueService.getAdminSalesHistory());
            model.addAttribute("isAdmin", true);
            return "pages/revenue";
            
        } else if ("INSTRUCTOR".equals(role)) {
            model.addAttribute("totalRevenue", revenueService.getInstructorTotalRevenue(user.getUserId()));
            model.addAttribute("salesHistory", revenueService.getInstructorSalesHistory(user.getUserId()));
            model.addAttribute("isAdmin", false);
            return "pages/revenue";
            
        } else {
            return "redirect:/";
        }
    }
}
```

---

### BƯỚC 4: TẠO GIAO DIỆN HIỂN THỊ (THEMELEAF HTML & BOOTSTRAP/CSS)
Tạo file HTML hiển thị doanh thu tại `src/main/resources/templates/pages/revenue.html` của dự án SWP.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Thống kê Doanh Thu - SWP Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Arial, sans-serif; }
        .card-revenue {
            background: linear-gradient(135deg, #1f4068 0%, #162447 100%);
            color: white; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); border: none;
        }
        .table-container {
            background: white; border-radius: 12px; padding: 25px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
        }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold text-dark" th:text="${isAdmin} ? 'Quản Lý Doanh Thu Hệ Thống' : 'Thống Kê Doanh Thu'">Báo Cáo Doanh Thu</h2>
        <a href="/" class="btn btn-outline-secondary">Về trang chủ</a>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="card card-revenue p-4 h-100 d-flex flex-column justify-content-between">
                <div>
                    <span class="text-uppercase tracking-wider opacity-75 small">Tổng Doanh Thu</span>
                    <h1 class="display-6 fw-bold mt-2" 
                        th:text="${#numbers.formatDecimal(totalRevenue, 0, 'COMMA', 0, 'POINT')} + ' đ'">0 đ</h1>
                </div>
                <p class="mb-0 mt-3"><small class="text-success fw-semibold">● Cập nhật thời gian thực</small></p>
            </div>
        </div>

        <div class="col-md-8">
            <div class="bg-white p-4 rounded-3 shadow-sm h-100">
                <h6 class="text-secondary mb-3">Biểu đồ doanh thu gần đây</h6>
                <canvas id="revenueChart" style="max-height: 180px;"></canvas>
            </div>
        </div>
    </div>

    <div class="table-container">
        <h5 class="mb-4 fw-bold text-secondary" 
            th:text="${isAdmin} ? 'Lịch Sử Đơn Hàng Hệ Thống' : 'Danh Sách Học Viên Mua Khóa Học'">Lịch sử giao dịch</h5>
        
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Mã GD</th>
                        <th>Khách hàng</th>
                        <th th:unless="${isAdmin}">Khóa học</th>
                        <th>Doanh thu nhận</th>
                        <th>Thời gian</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:if="${isAdmin}" th:each="order : ${salesHistory}">
                        <td th:text="'#' + ${order.orderId}">#01</td>
                        <td th:text="${order.user.fullName}">Nguyen Van A</td>
                        <td class="fw-bold text-primary" th:text="${#numbers.formatDecimal(order.totalAmount, 0, 'COMMA', 0, 'POINT')} + ' đ'">100k</td>
                        <td th:text="${#temporals.format(order.createdAt, 'dd/MM/yyyy HH:mm')}">01/01/2026</td>
                        <td><span class="badge bg-success">Thành công</span></td>
                    </tr>

                    <tr th:unless="${isAdmin}" th:each="item : ${salesHistory}">
                        <td th:text="'#' + ${item.orderItemId}">#01</td>
                        <td th:text="${item.order.user.fullName}">Nguyen Van A</td>
                        <td th:text="${item.course.currentPublishedVersion != null ? item.course.currentPublishedVersion.title : 'Khóa học'}">Tên khóa học</td>
                        <td class="fw-bold text-primary" th:text="${#numbers.formatDecimal(item.price, 0, 'COMMA', 0, 'POINT')} + ' đ'">100k</td>
                        <td th:text="${#temporals.format(item.createdAt, 'dd/MM/yyyy HH:mm')}">01/01/2026</td>
                        <td><span class="badge bg-success">Thành công</span></td>
                    </tr>

                    <tr th:if="${#lists.isEmpty(salesHistory)}">
                        <td colspan="6" class="text-center text-muted py-4">Chưa có giao dịch thành công nào.</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script th:inline="javascript">
    const ctx = document.getElementById('revenueChart').getContext('2d');
    const labels = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6'];
    const currentRevenue = [[${totalRevenue}]];
    const data = [400000, 900000, 1500000, 2200000, 1300000, currentRevenue];

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (VNĐ)',
                data: data,
                borderColor: '#1f4068',
                backgroundColor: 'rgba(31, 64, 104, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });
</script>
</body>
</html>
```
