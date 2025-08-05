package com.tekcit.festival.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hostProfiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HostProfile {
    @Id
    @Column(name = "h_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hId;

    @Column(name = "b_name", nullable = false)
    private String businessName;

    @Column(name = "genre")
    private String genre;

    @Column(name = "isActive", nullable = false)
    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
