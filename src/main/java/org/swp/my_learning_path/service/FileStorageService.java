package org.swp.my_learning_path.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Lưu file CV PDF vào thư mục uploads/cv/.
     * @return tên file đã lưu (dạng UUID_originalName.pdf)
     */
    String storeCvFile(MultipartFile file);

    /**
     * Load file để trả về client (download).
     */
    Resource loadCvAsResource(String storedFileName);

    /**
     * Xóa file CV cũ khi người dùng nộp lại đơn.
     */
    void deleteCvFile(String storedFileName);
}
