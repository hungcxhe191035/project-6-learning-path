package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "system_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SystemSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    Long settingId;

    @Column(name = "setting_key", unique = true, nullable = false, length = 100)
    String settingKey;

    @Column(name = "setting_value", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    String settingValue;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    String description;
}
