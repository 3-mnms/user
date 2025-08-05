package com.tekcit.festival.domain.user.entity;

import com.tekcit.festival.domain.user.enums.UserGender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userProfiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserProfile {
    @Id
    @Column(name = "u_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uId;

    @Column(name = "resident_num", nullable = false, length = 15)
    private String residentNum;

    @Column(name = "age", nullable = false, length = 5)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private UserGender gender;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "isActive", nullable = false)
    @Builder.Default
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


