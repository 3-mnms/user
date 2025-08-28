package com.tekcit.festival.domain.host_admin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;


@Entity
@Table(
        name = "notification_schedules",
        uniqueConstraints = {
                // 동일 회차·동일 발송시각 중복 방지
                @UniqueConstraint(
                        name = "uk_fid_start_send",
                        columnNames = {"fid", "start_at", "send_time"}
                )
        },
        indexes = {
                @Index(name = "idx_notice_fid_time", columnList = "fid, send_time"),
                @Index(name = "idx_notice_start_at", columnList = "start_at")
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(name = "fid", length = 20, nullable = false)
    private String fid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false, length = 20)
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    private String title;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "내용은 50자 이내여야 합니다.")
    private String body;

    @Column(name = "send_time", nullable = true)
    private LocalDateTime sendTime;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent = false;

    @Column(name = "fname", nullable = false)
    private String fname;

    @PrePersist
    public void prePersist() {
        if (this.sendTime != null) {
            this.sendTime = this.sendTime.withSecond(0).withNano(0);
        }
        if (this.startAt != null) {
            this.startAt = this.startAt.withSecond(0).withNano(0);
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.sendTime != null) {
            this.sendTime = this.sendTime.withSecond(0).withNano(0);
        }
        if (this.startAt != null) {
            this.startAt = this.startAt.withSecond(0).withNano(0);
        }
    }
}