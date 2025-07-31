package com.tekcit.festival.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//테스트용 임의 엔티티
@Entity
@Table(name = "festivals")
@Getter
@Setter
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 예시 컬럼
}
