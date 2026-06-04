package org.swp.my_learning_path.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private static final String UPLOAD_DIR = "uploads/cv";

    private final Path uploadPath;

    public FileStorageServiceImpl() {
        this.uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
            log.info("CV upload directory ready: {}", this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ CV: " + UPLOAD_DIR, e);
        }
    }

    @Override
    public String storeCvFile(MultipartFile file) {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "cv.pdf"
        );

        // Đảm bảo tên file không có ký tự nguy hiểm
        if (originalName.contains("..")) {
            throw new RuntimeException("Tên file không hợp lệ: " + originalName);
        }

        // Sanitize tên file để an toàn trên Windows/Linux File System (bỏ dấu tiếng Việt, ký tự ?, khoảng trắng, v.v.)
        String safeOriginalName = sanitizeFilename(originalName);

        // Tạo tên file unique với UUID prefix
        String storedName = UUID.randomUUID() + "_" + safeOriginalName;
        Path targetPath = this.uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored CV file: {}", storedName);
            return storedName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file CV: " + originalName, e);
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        // Loại bỏ dấu tiếng Việt
        String normalized = java.text.Normalizer.normalize(filename, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Chỉ giữ lại chữ cái ASCII, số, dấu chấm, dấu gạch dưới, dấu gạch ngang
        String safeName = normalized.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        // Rút gọn nhiều dấu gạch dưới liên tiếp
        safeName = safeName.replaceAll("__+", "_");

        return safeName;
    }

    @Override
    public Resource loadCvAsResource(String storedFileName) {
        try {
            Path filePath = this.uploadPath.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Không tìm thấy hoặc không thể đọc file: " + storedFileName);
            }
        } catch (InvalidPathException e) {
            throw new RuntimeException("Tên file chứa ký tự không hợp lệ trên hệ thống: " + storedFileName, e);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Đường dẫn file không hợp lệ: " + storedFileName, e);
        }
    }

    @Override
    public void deleteCvFile(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) return;
        try {
            Path filePath = this.uploadPath.resolve(storedFileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted old CV file: {}", storedFileName);
            }
        } catch (InvalidPathException e) {
            log.warn("Tên file cũ chứa ký tự không hợp lệ trên hệ thống, bỏ qua việc xóa: {}", storedFileName, e);
        } catch (IOException e) {
            log.warn("Không thể xóa file CV cũ: {}", storedFileName, e);
        }
    }
}
