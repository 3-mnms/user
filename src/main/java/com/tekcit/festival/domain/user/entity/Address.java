package com.tekcit.festival.domain.user.entity;

import com.tekcit.festival.domain.user.enums.GeocodeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "latitude",  columnDefinition = "DECIMAL(10,7)")
    private Double latitude; //위도

    @Column(name = "longitude",  columnDefinition = "DECIMAL(10,7)")
    private Double longitude;//경도

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GeocodeStatus isGeocoded = GeocodeStatus.PENDING;//지오코드 여부(위도, 경도)

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private UserProfile userProfile;

    public void setAsDefault() {
        this.isDefault = true;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }
}
