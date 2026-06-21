package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.dto.response.AdminDashboardDTO;
import org.swp.my_learning_path.dto.response.ChartDataPointDTO;
import org.swp.my_learning_path.dto.response.TopCourseDTO;
import org.swp.my_learning_path.dto.response.TopInstructorDTO;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorApplicationRepository applicationRepository;

    @Override
    public AdminDashboardDTO getDashboardData(String period) {
        // 1. Xác định khoảng thời gian start - end
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

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
            // all time
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
        }

        // 2. Lấy dữ liệu KPIs
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue(ETransactionStatus.SUCCESS);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        long totalStudents = userRepository.countByRoleAndDeleteFlagFalse(ERole.STUDENT);
        long totalInstructors = userRepository.countByRoleAndDeleteFlagFalse(ERole.INSTRUCTOR);
        long totalCourses = courseRepository.count(); // Có thể đếm courses chưa delete nếu cần, hoặc count() thông thường
        
        long pendingInstructorsCount = applicationRepository.countByStatus(EApplicationStatus.PENDING);
        long pendingCoursesCount = courseVersionRepository.countByStatusAndCourseDeleteFlagFalse(ECourseStatus.PENDING_APPROVAL);

        // 3. Truy xuất đơn hàng thành công trong khoảng thời gian để dựng biểu đồ doanh thu
        List<Order> ordersInPeriod = orderRepository.findByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtAsc(
                ETransactionStatus.SUCCESS, start, end
        );

        List<ChartDataPointDTO> revenueChartData = generateRevenueChart(period, ordersInPeriod);

        // 4. Lấy dữ liệu tỷ lệ đăng ký theo Tag (Biểu đồ tròn)
        List<Object[]> rawTagData = enrollmentRepository.countEnrollmentsByTag();
        List<ChartDataPointDTO> categoryChartData = rawTagData.stream()
                .map(row -> ChartDataPointDTO.builder()
                        .label((String) row[0])
                        .value(BigDecimal.valueOf((Long) row[1]))
                        .build())
                .collect(Collectors.toList());

        // 5. Danh sách các hoạt động & Xếp hạng gần đây
        // Đơn giảng viên chờ duyệt mới nhất (Top 5)
        List<org.swp.my_learning_path.entity.InstructorApplication> pendingInstructors = applicationRepository
                .findByStatus(EApplicationStatus.PENDING, PageRequest.of(0, 5, Sort.by("createdAt").descending()))
                .getContent();

        // Phiên bản khóa học chờ duyệt mới nhất (Top 5)
        List<CourseVersion> pendingCourses = courseVersionRepository.findLatestPendingCourseVersions(
                ECourseStatus.PENDING_APPROVAL, PageRequest.of(0, 5)
        );

        // 10 Đơn hàng mới nhất trên hệ thống
        List<Order> recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc();

        // Top 5 khóa học bán chạy nhất trong khoảng thời gian
        List<Object[]> rawTopCourses = orderItemRepository.findTopSellingCoursesInPeriod(
                ETransactionStatus.SUCCESS, start, end, PageRequest.of(0, 5)
        );
        List<TopCourseDTO> topCourses = rawTopCourses.stream()
                .map(row -> {
                    Course course = (Course) row[0];
                    Long salesCount = (Long) row[1];
                    String title = course.getCurrentPublishedVersion() != null ? course.getCurrentPublishedVersion().getTitle() : "Khóa học không có tiêu đề";
                    BigDecimal price = course.getCurrentPublishedVersion() != null ? course.getCurrentPublishedVersion().getPrice() : BigDecimal.ZERO;
                    String instructorName = course.getInstructor() != null ? course.getInstructor().getFullName() : "N/A";
                    return TopCourseDTO.builder()
                            .courseId(course.getCourseId())
                            .title(title)
                            .instructorName(instructorName)
                            .price(price)
                            .salesCount(salesCount)
                            .averageRating(course.getAverageRating())
                            .build();
                })
                .collect(Collectors.toList());

        // Top 5 giảng viên tiêu biểu trong khoảng thời gian
        List<Object[]> rawTopInstructors = orderItemRepository.findTopInstructorsInPeriod(
                ETransactionStatus.SUCCESS, start, end, PageRequest.of(0, 5)
        );
        List<TopInstructorDTO> topInstructors = rawTopInstructors.stream()
                .map(row -> {
                    User instructor = (User) row[0];
                    BigDecimal revenue = (BigDecimal) row[1];
                    long courseCount = courseRepository.findByInstructor_UserIdAndDeleteFlagFalseOrderByCreatedAtDesc(instructor.getUserId()).size();
                    return TopInstructorDTO.builder()
                            .instructorId(instructor.getUserId())
                            .fullName(instructor.getFullName())
                            .email(instructor.getEmail())
                            .courseCount(courseCount)
                            .revenue(revenue)
                            .build();
                })
                .collect(Collectors.toList());

        // 6. Đóng gói kết quả trả về
        return AdminDashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalCourses(totalCourses)
                .pendingInstructorsCount(pendingInstructorsCount)
                .pendingCoursesCount(pendingCoursesCount)
                .revenueChartData(revenueChartData)
                .categoryChartData(categoryChartData)
                .pendingInstructors(pendingInstructors)
                .pendingCourses(pendingCourses)
                .topCourses(topCourses)
                .topInstructors(topInstructors)
                .recentOrders(recentOrders)
                .build();
    }

    private List<ChartDataPointDTO> generateRevenueChart(String period, List<Order> orders) {
        Map<String, BigDecimal> chartMap = new LinkedHashMap<>();

        if ("today".equalsIgnoreCase(period)) {
            // Nhóm theo giờ: 00:00 -> 23:00
            for (int i = 0; i < 24; i++) {
                chartMap.put(String.format("%02d:00", i), BigDecimal.ZERO);
            }
            for (Order order : orders) {
                if (order.getCreatedAt() != null) {
                    String key = String.format("%02d:00", order.getCreatedAt().getHour());
                    chartMap.put(key, chartMap.getOrDefault(key, BigDecimal.ZERO).add(order.getTotalAmount()));
                }
            }
        } else if ("7days".equalsIgnoreCase(period)) {
            // Nhóm theo ngày (7 ngày gần nhất)
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
        } else if ("30days".equalsIgnoreCase(period)) {
            // Nhóm theo ngày (30 ngày gần nhất)
            for (int i = 29; i >= 0; i--) {
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
        } else if ("thisyear".equalsIgnoreCase(period)) {
            // Nhóm theo tháng trong năm nay
            String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                               "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            for (String month : months) {
                chartMap.put(month, BigDecimal.ZERO);
            }
            for (Order order : orders) {
                if (order.getCreatedAt() != null) {
                    int monthIdx = order.getCreatedAt().getMonthValue() - 1;
                    String key = months[monthIdx];
                    chartMap.put(key, chartMap.get(key).add(order.getTotalAmount()));
                }
            }
        } else {
            // All-Time: nhóm theo Tháng/Năm xuất hiện
            for (Order order : orders) {
                if (order.getCreatedAt() != null) {
                    String key = order.getCreatedAt().getMonthValue() + "/" + order.getCreatedAt().getYear();
                    chartMap.put(key, chartMap.getOrDefault(key, BigDecimal.ZERO).add(order.getTotalAmount()));
                }
            }
        }

        List<ChartDataPointDTO> points = new ArrayList<>();
        chartMap.forEach((label, val) -> points.add(new ChartDataPointDTO(label, val)));
        return points;
    }
}
