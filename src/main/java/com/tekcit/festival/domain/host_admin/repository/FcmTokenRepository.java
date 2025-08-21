package com.tekcit.festival.domain.host_admin.repository;

import com.tekcit.festival.domain.host_admin.entity.FcmToken;
import com.tekcit.festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // 특정 사용자의 FCM 토큰을 조회
    Optional<FcmToken> findByUser(User user);

    // 주어진 사용자 ID 목록에 해당하는 모든 FCM 토큰 문자열을 조회
    @Query("select t.token from FcmToken t where t.user.userId in :userIds")
    List<String> findTokensByUserIds(@Param("userIds") Collection<Long> userIds);
}