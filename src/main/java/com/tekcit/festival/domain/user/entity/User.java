package com.tekcit.festival.domain.user.entity;

import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_login_id", columnNames = {"login_id"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_users_provider_pid", columnNames = {"oauth_provider", "oauth_provider_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "login_pw")
    private String loginPw;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = false;

    //'USER', 'HOST', 'ADMIN'
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", length = 20, nullable = false)
    private OAuthProvider oauthProvider; // LOCAL, KAKAO

    @Column(name = "oauth_provider_id", length = 100)
    private String oauthProviderId; // 예: 카카오 id(문자열)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private HostProfile hostProfile;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @PrePersist
    @PreUpdate
    private void validateProviderSpecificFields() {
        if (oauthProvider == null) {
            throw new IllegalStateException("oauthProvider는 필수입니다.");
        }
        switch (oauthProvider) {
            case LOCAL -> {
                if ((loginId == null || loginId.isBlank()) || (loginPw == null || loginPw.isBlank()))
                    throw new BusinessException(ErrorCode.KAKAO_INVALID_FIELDS, "LOCAL 계정은 loginId/loginPw가 필수입니다.");
                if (oauthProviderId != null)
                    throw new BusinessException(ErrorCode.KAKAO_INVALID_FIELDS, "LOCAL 계정은 oauthProviderId가 null이어야 합니다.");
            }
            case KAKAO -> {
                if (loginId != null || loginPw != null)
                    throw new BusinessException(ErrorCode.KAKAO_INVALID_FIELDS, "KAKAO 계정은 loginId/loginPw가 null이어야 합니다.");
                if ((oauthProviderId == null || oauthProviderId.isBlank()))
                    throw new BusinessException(ErrorCode.KAKAO_INVALID_FIELDS, "KAKAO 계정은 oauthProviderId가 필수입니다.");
            }
            default -> throw new BusinessException(ErrorCode.KAKAO_INVALID_FIELDS, "지원하지 않는 oauthProvider: " + oauthProvider);
        }
    }
}
