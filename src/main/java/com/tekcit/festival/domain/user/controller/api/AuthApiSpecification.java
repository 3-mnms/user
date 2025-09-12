package com.tekcit.festival.domain.user.controller.api;

import com.tekcit.festival.domain.user.dto.request.LoginRequestDTO;
import com.tekcit.festival.domain.user.dto.response.AccessTokenInfoDTO;
import com.tekcit.festival.domain.user.dto.response.LoginResponseDTO;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApiSpecification {
    @Operation(summary = "로그인",
            description = "로그인 기능, LoginRequestDTO를 포함해야 합니다. ex) POST /api/users/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "일치하지 않는 비밀번호 or 필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "일치하지 않는 비밀번호 or 필수 입력 사항 위반(아이디, 비밀번호)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_PASSWORD_NOT_EQUAL_ERROR or VALIDATION_ERROR",
                                   "message": "일치하지 않는 비밀번호입니다. or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response);

    @Operation(summary = "로그아웃",
            description = "로그아웃 기능 ex) POST /api/users/logout")
    ResponseEntity<SuccessResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response);


    @Operation(summary = "accessToken 재발급",
            description = "accessToken 재발급 기능 ex) POST /api/users/reissue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "유효하지 않은 RefreshToken", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "유효하지 않은 RefreshToken(만료 또는 cookie의 refreshToken과 db의 refreshToken 불일치)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_REFRESH_TOKEN_EXPIRED or AUTH_REFRESH_TOKEN_NOT_MATCH",
                                   "message": "Refresh Token이 만료되었습니다. or Refresh Token이 일치하지 않습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<LoginResponseDTO>> reissue(HttpServletRequest request, HttpServletResponse response);


    @Operation(
            summary = "Access Token 파싱",
            description = "Authorization: Bearer {token} 헤더로 전달된 Access Token을 검증하고, 포함된 클레임 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유효하지 않은 AccessToken", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "유효하지 않은 AccessToken(토큰이 없거나 올바르지 못한 accessToken)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_TOKEN_MISSING or AUTH_TOKEN_INVALID",
                                   "message": "토큰이 없습니다. or 올바르지 못한 Access Token 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 AccessToken", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "유효하지 않은 AccessToken(만료)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_ACCESS_TOKEN_EXPIRED",
                                   "message": "Access Token이 만료되었습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<AccessTokenInfoDTO>> parseToken(HttpServletRequest request);

    }
