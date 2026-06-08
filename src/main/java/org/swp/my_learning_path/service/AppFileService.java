package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.AppFile;
import org.swp.my_learning_path.repository.AppFileRepository;

@Service
@RequiredArgsConstructor
public class AppFileService {

    private final AppFileRepository appFileRepository;

    @Transactional
    public AppFile saveFileInfo(String fileUrl, org.swp.my_learning_path.constant.EFileType fileType, String originalName) {
        AppFile appFile = AppFile.builder()
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileName(originalName)
                .build();
        return appFileRepository.save(appFile);
    }
}
