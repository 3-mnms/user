package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUser(User user);
    Optional<FcmToken> findByUserAndToken(User user, String token);

    // NotificationSchedule 페스티벌 ID로 FcmToken token 가져오는 통합 쿼리
    @Query("SELECT t.token FROM FcmToken t WHERE t.user IN (SELECT r.user FROM Reservation r WHERE r.festival = :festival)")
    List<String> findTokensByFestival(@Param("festival") Festival festival);
}
