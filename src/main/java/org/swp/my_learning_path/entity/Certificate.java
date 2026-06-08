package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(
        name = "certificates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CERTIFICATE_CODE",
                        columnNames = "certificate_code"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class Certificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    Long certificateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    Enrollment enrollment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    AppFile file;

    @Column(name = "certificate_code",
            nullable = false,
            unique = true,
            length = 100)
    String certificateCode;
}
