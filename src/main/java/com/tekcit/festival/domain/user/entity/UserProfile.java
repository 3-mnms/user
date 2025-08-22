package com.tekcit.festival.domain.user.entity;

import com.tekcit.festival.domain.user.enums.UserGender;
import com.tekcit.festival.utils.ResidentUtil;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
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

    public void updateResidentInfo(String residentNum) {
        this.residentNum = residentNum;
        this.age = ResidentUtil.calcAge(residentNum);
        this.birth = ResidentUtil.calcBirth(residentNum);
        this.gender = ResidentUtil.extractGender(residentNum);
    }
}


