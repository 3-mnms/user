package com.tekcit.festival.domain.user.controller.api;

import com.tekcit.festival.domain.user.dto.request.EmailSendDTO;
import com.tekcit.festival.domain.user.dto.request.EmailVerifyDTO;
import com.tekcit.festival.domain.user.dto.response.EmailResponseDTO;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailApiSpecification {
    @Operation(summary = "이메일 인증 코드 전송",
            description = "이메일 인증 코드 전송, 인증 코드 5분 이후 만료, emailSendDTO를 포함해야 합니다. ex) POST /api/mail/sendCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항 위반(이메일, 검증타입)",
                    value = """
                                {
                                   "success": false,
                                   "code": "VALIDATION_ERROR",
                                   "message": "%s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            )
    })
    ResponseEntity<SuccessResponse<EmailResponseDTO>> sendCode(@Valid @RequestBody EmailSendDTO emailSendDTO);


    @Operation(summary = "이메일 인증 코드 검증",
            description = "이메일 인증 코드 검증, emailVerifyDTO를 포함해야 합니다. ex) POST /api/mail/verify")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "인증코드 인증 실패 or 필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "인증코드 인증 실패(불일치) or 필수 입력 사항 위반(이메일, 인증 코드, 검증타입)",
                    value = """
                                {
                                   "success": false,
                                   "code": "EMAIL_VERIFICATION_CODE_MISMATCH or VALIDATION_ERROR",
                                   "message": "인증 코드가 일치하지 않습니다. or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "이메일 인증 요청 조회를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "EMAIL_VERIFICATION_NOT_FOUND",
                                   "message": "인증 요청이 없습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "410", description = "이메일 인증 요청 시간 만료", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "이메일 인증 요청 시간 만료",
                    value = """
                                {
                                   "success": false,
                                   "code": "EMAIL_VERIFICATION_EXPIRED",
                                   "message": "인증 코드가 만료되었습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<EmailResponseDTO>> verifyCode(@Valid @RequestBody EmailVerifyDTO emailVerifyDTO);

}
