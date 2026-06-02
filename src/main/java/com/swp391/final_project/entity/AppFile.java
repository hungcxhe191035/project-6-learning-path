package com.swp391.final_project.entity;

import com.swp391.final_project.constant.EFilePurpose;
import com.swp391.final_project.constant.EFileType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class AppFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    Long fileId;

    @Column(name = "file_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", length = 50)
    EFileType fileType;
    // IMAGE, PDF, DOCX,...

    @Column(name = "extension", length = 20)
    String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 50)
    EFilePurpose purpose;
    // CERTIFICATE, AVATAR, HOMEWORK,...
}
