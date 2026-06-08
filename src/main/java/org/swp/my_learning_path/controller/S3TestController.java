package org.swp.my_learning_path.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.service.S3Service;
// Hiển thị trang web test
@RestController
@RequestMapping("/api/s3")
public class S3TestController {

    @Autowired
    private S3Service s3Service;
    // Xử lý khi bấm nút Upload
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return s3Service.uploadFile(file);
        } catch (Exception e) {
            return "Lỗi upload: " + e.getMessage();
        }
    }
}