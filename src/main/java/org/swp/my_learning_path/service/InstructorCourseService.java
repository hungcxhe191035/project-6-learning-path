package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.request.CreateCourseRequest;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.CourseVersionRepository;
import org.swp.my_learning_path.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class InstructorCourseService {

    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final UserRepository userRepository;
    // biến này dùng để truy vấn ảnh biìa
    private final org.swp.my_learning_path.repository.AppFileRepository appFileRepository;
    private final S3Service s3Service;

    @Transactional
    public Long createDraftCourse(CreateCourseRequest request) {
        //
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Long mockInstructorId = ((org.swp.my_learning_path.security.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();

        User instructor = userRepository.findById(mockInstructorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Giảng viên!"));

        // 1. Tạo khóa học gốc
        Course newCourse = Course.builder()
                .instructor(instructor)
                .build();
        newCourse = courseRepository.save(newCourse);

        // 2. Tạo phiên bản nháp số 1
        CourseVersion draftVersion = CourseVersion.builder()
                .course(newCourse)
                .title(request.getTitle())
                .versionNumber(1)
                .status(ECourseStatus.DRAFT) // Trạng thái nháp
                .build();
        courseVersionRepository.save(draftVersion);

        return newCourse.getCourseId();
    }
    @Transactional
    public void updateCourseInfo(Long courseId, org.swp.my_learning_path.dto.request.UpdateCourseInfoRequest request) {
        // TẠM THỜI: Vẫn dùng ID ảo để test
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Long mockInstructorId = ((org.swp.my_learning_path.security.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
        // 1. Kiểm tra khóa học có tồn tại không và có phải của Giảng viên này không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học!"));

        if (!course.getInstructor().getUserId().equals(mockInstructorId)) {
            throw new RuntimeException("Bạn không có quyền sửa khóa học này!");
        }

        // 2. Lấy ra bản mới nhất để sửa
        CourseVersion version = courseVersionRepository.findFirstByCourse_CourseIdOrderByCreatedAtDesc(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản của khóa học này!"));

        // 3. Đắp dữ liệu mới vào
        version.setTitle(request.getTitle());
        version.setSubtitle(request.getSubtitle());
        version.setDescription(request.getDescription());
        version.setPrice(request.getPrice());

        // 4. Nếu có gửi ID ảnh bìa lên thì tìm ảnh và gắn vào
        if (request.getThumbnailFileId() != null) {
            org.swp.my_learning_path.entity.AppFile thumbnail = appFileRepository.findById(request.getThumbnailFileId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file ảnh!"));
            version.setThumbnail(thumbnail);
        }

        courseVersionRepository.save(version);
    }

    // ================== CÁC HÀM MỚI (PHASE 7) ================== //

    @Transactional
    public void publishCourse(Long courseId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Long mockInstructorId = ((org.swp.my_learning_path.security.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học!"));

        if (!course.getInstructor().getUserId().equals(mockInstructorId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên khóa học này!");
        }

        CourseVersion version = courseVersionRepository.findFirstByCourse_CourseIdOrderByCreatedAtDesc(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản để xuất bản!"));

        // Validation sơ bộ: Phải có tiêu đề
        if (version.getTitle() == null || version.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng điền Tiêu đề khóa học trước khi xuất bản!");
        }

        // Bỏ qua bước chờ Admin duyệt, Duyệt thẳng luôn!
        version.setStatus(ECourseStatus.APPROVED);
        courseVersionRepository.save(version);
        
        // Gắn bản version này làm bản chính thức cho khóa học, đồng thời gỡ trạng thái khóa
        course.setCurrentPublishedVersion(version);
        course.setIsBlocked(false);
        course.setBlockReason(null);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteDraftCourse(Long courseId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Long mockInstructorId = ((org.swp.my_learning_path.security.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học!"));

        if (!course.getInstructor().getUserId().equals(mockInstructorId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên khóa học này!");
        }

        // Đánh dấu xóa (Soft Delete) cho Course
        course.setDeleteFlag(true);
        courseRepository.save(course);
        
        // Đánh dấu xóa luôn cả bản Nháp cho sạch sẽ
        courseVersionRepository.findByCourse_CourseIdAndStatus(courseId, ECourseStatus.DRAFT)
                .ifPresent(version -> {
                    version.setDeleteFlag(true);
                    courseVersionRepository.save(version);
                });
    }

    @Transactional
    public java.util.Map<String, Object> uploadCourseThumbnail(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        // Tải file lên S3
        String fileUrl = s3Service.uploadFile(file);

        // Trích xuất đuôi mở rộng
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        // Tạo bản ghi AppFile
        org.swp.my_learning_path.entity.AppFile appFile = org.swp.my_learning_path.entity.AppFile.builder()
                .fileName(originalFilename)
                .fileUrl(fileUrl)
                .fileType(org.swp.my_learning_path.constant.EFileType.IMAGE)
                .purpose(org.swp.my_learning_path.constant.EFilePurpose.COURSE_THUMBNAIL)
                .extension(extension)
                .build();
        appFile = appFileRepository.save(appFile);

        // Đóng gói kết quả trả về
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("fileId", appFile.getFileId());
        result.put("fileUrl", fileUrl);
        return result;
    }
}