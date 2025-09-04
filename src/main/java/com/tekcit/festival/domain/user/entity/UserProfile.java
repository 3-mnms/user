package com.tekcit.festival.domain.user.entity;

import com.tekcit.festival.domain.user.enums.GeocodeStatus;
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

    @Column(name = "latitude",  columnDefinition = "DECIMAL(10,7)")
    private Double latitude; //위도

    @Column(name = "longitude",  columnDefinition = "DECIMAL(10,7)")
    private Double longitude;//경도

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GeocodeStatus isGeocoded = GeocodeStatus.PENDING;//지오코드 여부(위도, 경도)

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


