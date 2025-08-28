package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.EmailVerification;
import com.tekcit.festival.domain.user.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndType(String email, VerificationType type);
}
