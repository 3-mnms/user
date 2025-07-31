package com.tekcit.festival.domain.user.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

//테스트용 임의 엔티티
@Entity
@Table(name = "reservations")
@Setter
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;
}
