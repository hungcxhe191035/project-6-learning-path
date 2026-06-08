package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    Long profileId;

    @Column(name = "bio", columnDefinition = "NVARCHAR(MAX)")
    String bio;

    @Column(name = "headline", columnDefinition = "NVARCHAR(255)")
    String headline;

    @Column(name = "facebook_url", length = 500)
    String facebookUrl;

    @Column(name = "youtube_url", length = 500)
    String youtubeUrl;

    @Column(name = "linkedin_url", length = 500)
    String linkedinUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    User user;
}
