package org.swp.my_learning_path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.InstructorApplicationRepository;
import org.swp.my_learning_path.repository.UserRepository;
import org.swp.my_learning_path.service.FileStorageService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorApplicationRepository applicationRepository;

    @MockBean
    private FileStorageService fileStorageService;

    private String testFileName;

    @BeforeEach
    void setUp() throws Exception {
        testFileName = UUID.randomUUID() + "_test_cv.pdf";
        Mockito.when(fileStorageService.loadCvAsResource(testFileName))
                .thenReturn(new ByteArrayResource("Dummy PDF Content".getBytes()) {
                    @Override
                    public String getFilename() {
                        return "test_cv.pdf";
                    }
                });
    }

    @Test
    @WithUserDetails("admin@fcourse.vn")
    void testDownloadCvAsAdmin_ShouldSucceed() throws Exception {
        // Admin should be able to download any CV, even if they don't own it.
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "admin@fcourse.vn", roles = {"ADMIN"})
    void testDownloadCvWithMockUserAdmin_ShouldSucceed() throws Exception {
        // Test with a principal that is a standard UserDetails instead of CustomUserDetails
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("hocvien01@gmail.com")
    void testDownloadCvAsStudentOwnFile_ShouldSucceed() throws Exception {
        // Create an application for student 'hocvien01@gmail.com'
        User student = userRepository.findByEmail("hocvien01@gmail.com")
                .orElseThrow(() -> new AssertionError("Student not found"));

        InstructorApplication application = InstructorApplication.builder()
                .user(student)
                .headline("My Headline")
                .bio("My Bio")
                .motivation("My Motivation")
                .cvFileName("test_cv.pdf")
                .cvFilePath(testFileName)
                .status(EApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);

        // Student should be able to download their own CV
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("hocvien02@gmail.com")
    void testDownloadCvAsStudentOtherFile_ShouldBeForbidden() throws Exception {
        // Create application for hocvien01@gmail.com, but attempt to access as hocvien02@gmail.com
        User student1 = userRepository.findByEmail("hocvien01@gmail.com")
                .orElseThrow(() -> new AssertionError("Student 1 not found"));

        InstructorApplication application = InstructorApplication.builder()
                .user(student1)
                .headline("My Headline")
                .bio("My Bio")
                .motivation("My Motivation")
                .cvFileName("test_cv.pdf")
                .cvFilePath(testFileName)
                .status(EApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);

        // Student 2 should not be able to download Student 1's CV
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("giangvien.java@fcourse.vn")
    void testDownloadCvAsInstructorOwnFile_ShouldSucceed() throws Exception {
        // Create an application for instructor 'giangvien.java@fcourse.vn'
        User instructor = userRepository.findByEmail("giangvien.java@fcourse.vn")
                .orElseThrow(() -> new AssertionError("Instructor not found"));

        InstructorApplication application = InstructorApplication.builder()
                .user(instructor)
                .headline("My Headline")
                .bio("My Bio")
                .motivation("My Motivation")
                .cvFileName("test_cv.pdf")
                .cvFilePath(testFileName)
                .status(EApplicationStatus.APPROVED)
                .build();
        applicationRepository.save(application);

        // Instructor should be able to download their own CV
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("giangvien.java@fcourse.vn")
    void testDownloadCvAsInstructorOtherFile_ShouldBeForbidden() throws Exception {
        // Create application for hocvien01@gmail.com, but attempt to access as giangvien.java@fcourse.vn
        User student = userRepository.findByEmail("hocvien01@gmail.com")
                .orElseThrow(() -> new AssertionError("Student not found"));

        InstructorApplication application = InstructorApplication.builder()
                .user(student)
                .headline("My Headline")
                .bio("My Bio")
                .motivation("My Motivation")
                .cvFileName("test_cv.pdf")
                .cvFilePath(testFileName)
                .status(EApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);

        // Instructor should not be able to download Student's CV
        mockMvc.perform(get("/files/cv/" + testFileName))
                .andExpect(status().isForbidden());
    }
}
