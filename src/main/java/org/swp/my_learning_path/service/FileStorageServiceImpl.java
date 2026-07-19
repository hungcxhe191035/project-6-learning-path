package org.swp.my_learning_path.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

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

        // Tạo tên file unique với UUID prefix làm key trên S3
        String storedName = UUID.randomUUID().toString() + "_" + safeOriginalName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedName)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Stored CV file on S3: {}", storedName);
            return storedName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file CV lên S3: " + originalName, e);
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
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .build();
            byte[] bytes = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return storedFileName;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Không thể tải file CV từ S3: " + storedFileName, e);
        }
    }

    @Override
    public void deleteCvFile(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) return;
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted CV file from S3: {}", storedFileName);
        } catch (Exception e) {
            log.error("Lỗi khi xóa file CV từ S3: {}", storedFileName, e);
        }
    }
}
