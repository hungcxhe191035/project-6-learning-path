package org.swp.my_learning_path.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.Order;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDTO {
    // KPIs
    private BigDecimal totalRevenue;
    private long totalStudents;
    private long totalInstructors;
    private long totalCourses;
    private long pendingInstructorsCount;
    private long pendingCoursesCount;

    // Charts
    private List<ChartDataPointDTO> revenueChartData;
    private List<ChartDataPointDTO> categoryChartData;

    // Recent items and Rankings
    private List<InstructorApplication> pendingInstructors;
    private List<CourseVersion> pendingCourses;
    private List<TopCourseDTO> topCourses;
    private List<TopInstructorDTO> topInstructors;
    private List<Order> recentOrders;
}
