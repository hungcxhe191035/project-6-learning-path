package org.swp.my_learning_path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.dto.request.AnswerQuestionRequest;
import org.swp.my_learning_path.dto.request.AskQuestionRequest;
import org.swp.my_learning_path.dto.response.CourseQuestionDTO;
import org.swp.my_learning_path.dto.response.QuestionAnswerDTO;
import org.swp.my_learning_path.dto.response.VoucherApplyResponse;
import org.swp.my_learning_path.entity.*;
import org.swp.my_learning_path.repository.*;
import org.swp.my_learning_path.service.CourseQnAService;
import org.swp.my_learning_path.service.VoucherService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QnAVoucherServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CourseQnAService qnaService;

    @Autowired
    private VoucherService voucherService;

    @Test
    @Transactional
    void testQnAAndVoucherFlow() {
        // ==========================================
        // 1. LẤY DỮ LIỆU THẬT TỪ DATABASE CỦA BẠN
        // ==========================================
        
        // Tìm Student đầu tiên trong DB
        User student = userRepository.findAll().stream()
                .filter(u -> u.getRole() == ERole.STUDENT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Student nào trong DB! Hãy đăng ký 1 student trước."));

        // Tìm Course đã xuất bản có giá tiền đầu tiên
        Course course = courseRepository.findAll().stream()
                .filter(c -> c.getInstructor() != null && c.getCurrentPublishedVersion() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Course nào đã publish trong DB! Hãy tạo 1 khóa học trước."));

        User instructor = course.getInstructor();
        CourseVersion courseVersion = course.getCurrentPublishedVersion();
        BigDecimal originalPrice = courseVersion.getPrice() != null ? courseVersion.getPrice() : BigDecimal.ZERO;

        // Tìm Section của Course này
        CourseSection section = courseSectionRepository.findAll().stream()
                .filter(s -> s.getCourseVersion().getCourseVersionId().equals(courseVersion.getCourseVersionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Section nào cho Course này!"));

        // Tìm Lesson thuộc Section này
        Lesson lesson = lessonRepository.findAll().stream()
                .filter(l -> l.getSection().getSectionId().equals(section.getSectionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Lesson nào cho Course này!"));

        // ==========================================
        // 2. TEST TÍNH NĂNG HỎI ĐÁP BÀI HỌC (Q&A)
        // ==========================================

        // Học viên hỏi bài
        AskQuestionRequest askRequest = AskQuestionRequest.builder()
                .courseId(course.getCourseId())
                .lessonId(lesson.getLessonId())
                .title("Thắc mắc bài học thực tế")
                .content("Chào thầy, em đang test câu hỏi này trên dữ liệu thật của khóa học " + courseVersion.getTitle())
                .build();

        CourseQuestionDTO questionDto = qnaService.askQuestion(student.getUserId(), askRequest);
        
        assertNotNull(questionDto);
        assertEquals("Thắc mắc bài học thực tế", questionDto.getTitle());
        assertEquals(EQuestionStatus.PENDING, questionDto.getStatus());

        // Giảng viên giải đáp
        AnswerQuestionRequest answerRequest = AnswerQuestionRequest.builder()
                .questionId(questionDto.getQuestionId())
                .content("Cảm ơn em đã đặt câu hỏi. Thầy đã nhận được phản hồi trên DB thật.")
                .build();

        QuestionAnswerDTO answerDto = qnaService.answerQuestion(instructor.getUserId(), answerRequest);

        assertNotNull(answerDto);
        assertEquals("Cảm ơn em đã đặt câu hỏi. Thầy đã nhận được phản hồi trên DB thật.", answerDto.getContent());

        // Lấy lại danh sách câu hỏi xem trạng thái đổi thành ANSWERED chưa
        var questionsList = qnaService.getQuestionsByLesson(lesson.getLessonId());
        assertFalse(questionsList.isEmpty());
        assertEquals(EQuestionStatus.ANSWERED, questionsList.get(0).getStatus());

        // ==========================================
        // 3. TEST VOUCHER VỚI CÔNG THỨC 80/20
        // ==========================================

        // Tạo tạm Voucher ADMIN giảm 10% giá gốc để test
        BigDecimal discountValue = originalPrice.multiply(new BigDecimal("0.1")); // Giảm 10%
        BigDecimal expectedActualPaid = originalPrice.subtract(discountValue).max(BigDecimal.ZERO);

        Voucher adminVoucher = Voucher.builder()
                .code("REAL_TEST_ADMIN")
                .discountValue(discountValue)
                .minOrderAmount(BigDecimal.ZERO)
                .creatorRole("ADMIN")
                .limitUsage(5)
                .usedCount(0)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        voucherRepository.save(adminVoucher);

        // Áp mã giảm giá
        VoucherApplyResponse result = voucherService.calculateVoucher(
                "REAL_TEST_ADMIN", course.getCourseId(), student.getUserId());

        assertTrue(result.isSuccess());
        assertEquals(0, discountValue.compareTo(result.getDiscountAmount()));
        assertEquals(0, expectedActualPaid.compareTo(result.getActualPaid()));

        // Công thức ADMIN: Thầy giáo nhận đủ 80% giá gốc
        BigDecimal expectedInstructorShare = originalPrice.multiply(new BigDecimal("0.8"));
        BigDecimal expectedAdminShare = expectedActualPaid.subtract(expectedInstructorShare);

        assertEquals(0, expectedInstructorShare.compareTo(result.getInstructorShare()));
        assertEquals(0, expectedAdminShare.compareTo(result.getAdminShare()));
    }
}
