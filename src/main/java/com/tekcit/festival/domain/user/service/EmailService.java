package com.tekcit.festival.domain.user.service;
import com.tekcit.festival.domain.user.dto.request.EmailSendDTO;
import com.tekcit.festival.domain.user.dto.request.EmailVerifyDTO;
import com.tekcit.festival.domain.user.dto.response.EmailResponseDTO;
import com.tekcit.festival.domain.user.entity.EmailVerification;
import com.tekcit.festival.domain.user.repository.EmailVerificationRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.EmailSendException;
import com.tekcit.festival.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private static final long EXPIRE_MINUTES = 5;

    public EmailResponseDTO sendVerificationCode(EmailSendDTO emailSendDTO){
        String code = createCode();

        saveOrUpdateEmail(code, emailSendDTO); // 이메일 주소, 타입 중복 확인 후 있을 경우 update, 없으면 새로 생성 후 save

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            String htmlContent = """
                    <div style="font-family: Arial, sans-serif;">
                        <p>인증 코드: <strong>%s</strong></p>
                        <br>
                        <p style="font-size: 14px; color: #777777;">
                            ※ 이 메일은 발신 전용입니다. 회신하지 말아주세요.<br>
                            문의사항은 관리자 이메일 주소로 문의해주세요.
                        </p>
                    </div>
                    """.formatted(code);
            helper.setFrom("noreply.tekcit@gmail.com");
            helper.setTo(emailSendDTO.getEmail());
            helper.setSubject("tekcit 이메일 인증 코드입니다");
            helper.setReplyTo("no-reply@invalid-domain.com");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            return buildResponse(true, "인증 코드가 전송되었습니다.");
        }
        catch (Exception e){
            throw new EmailSendException("이메일 인증 코드가 전송 실패했습니다.", e);
        }
    }

    public void saveOrUpdateEmail(String code, EmailSendDTO emailSendDTO){
        EmailVerification findExisting = emailVerificationRepository.findByEmailAndType(emailSendDTO.getEmail(), emailSendDTO.getType())
                .map(existing ->{
                    existing.setCode(code);
                    existing.setIsVerified(false);
                    existing.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
                    return existing;
                })
                .orElseGet(()-> {
                    EmailVerification emailVerification = emailSendDTO.toEmailEntity();
                    emailVerification.setCode(code);
                    emailVerification.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
                    return emailVerification;
                });

        emailVerificationRepository.save(findExisting);
    }

    public EmailResponseDTO verifyCode(EmailVerifyDTO emailVerifyDTO) {
        EmailVerification emailVerification =
                emailVerificationRepository.findByEmailAndType(emailVerifyDTO.getEmail(), emailVerifyDTO.getType())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_NOT_FOUND));

        if (emailVerification.isExpired()) {
            throw new BusinessException(ErrorCode.VERIFICATION_EXPIRED);
        }

        if (!emailVerification.getCode().equals(emailVerifyDTO.getCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        emailVerification.setIsVerified(true);
        emailVerificationRepository.save(emailVerification);

        return buildResponse(true, "이메일이 인증되었습니다.");
    }

    private String createCode() {
        return String.format("%06d", new SecureRandom().nextInt(1000000)); // 6자리 숫자
    }

    private EmailResponseDTO buildResponse(boolean success, String message){
        return EmailResponseDTO.builder()
                .success(success)
                .message(message)
                .build();
    }
}
