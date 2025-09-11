package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.api.EmailApiSpecification;
import com.tekcit.festival.domain.user.dto.request.EmailSendDTO;
import com.tekcit.festival.domain.user.dto.request.EmailVerifyDTO;
import com.tekcit.festival.domain.user.dto.response.EmailResponseDTO;
import com.tekcit.festival.domain.user.service.EmailService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Tag(name = "이메일 인증 API", description = "이메일 인증 코드 전송, 검증")
public class EmailController implements EmailApiSpecification {

    private final EmailService emailService;

    @PostMapping("/sendCode")
    public ResponseEntity<SuccessResponse<EmailResponseDTO>> sendCode(@Valid @RequestBody EmailSendDTO emailSendDTO) {
       EmailResponseDTO sendCode = emailService.sendVerificationCode(emailSendDTO);
       return ApiResponseUtil.success(sendCode);
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<SuccessResponse<EmailResponseDTO>> verifyCode(@Valid @RequestBody EmailVerifyDTO emailVerifyDTO) {
        EmailResponseDTO verifyCode = emailService.verifyCode(emailVerifyDTO);
        return ApiResponseUtil.success(verifyCode);
    }
}
