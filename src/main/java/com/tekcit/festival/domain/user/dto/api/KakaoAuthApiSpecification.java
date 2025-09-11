package com.tekcit.festival.domain.user.dto.api;

import com.tekcit.festival.domain.user.dto.request.KakaoSignupDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface KakaoAuthApiSpecification {

    void redirectToKakao(@RequestParam(value = "force", defaultValue = "false") boolean forceLogin, HttpServletResponse response) throws IOException;

    void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException;

    @Operation(summary = "회원 가입(일반 유저), 카카오 회원가입",
            description = "일반 유저 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/auth/kakao/signupUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "가입 토큰 없거나 만료 or 필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(가입 토큰 없음 or 필수 입력 사항 위반(이름, 전화번호, 주민번호, 주소, 우편번호))",
                    value = """
                                {
                                   "success": false,
                                   "code": "KAKAO_INVALID_TICKET or VALIDATION_ERROR",
                                   "message": "카카오 가입 토큰이 없거나 만료(10분). 다시 인증해주세요. or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패 (중복된 kakao ID, Email로 인한 conflict)",
                    value = """
                                {
                                   "success": false,
                                   "code": "DUPLICATE_KAKAO_ID or DUPLICATE_EMAIL_ID",
                                   "message": "이미 존재하는 카카오 계정입니다. KAKAO_ID: %s, 이미 존재하는 이메일입니다. EMAIL: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<UserResponseDTO>> signupUser(@Valid @RequestBody KakaoSignupDTO kakaoSignupDTO,
                                                                       @CookieValue(value = "kakao_signup", required = false) String ticket,
                                                                       HttpServletResponse res);


    }
