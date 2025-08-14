package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.request.LoginRequestDTO;
import com.tekcit.festival.domain.user.dto.response.LoginResponseDTO;
import com.tekcit.festival.domain.user.service.AuthService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.tekcit.festival.exception.global.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "로그인 API", description = "회원 로그인, 로그아웃, 토큰 재발급")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인",
            description = "로그인 기능, LoginRequestDTO를 포함해야 합니다. ex) POST /api/users/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "로그인 실패 (잘못된 비밀번호)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "로그인 실패 (사용자를 찾을 수 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO loginResult = authService.login(request, response);
        return ApiResponseUtil.success(loginResult);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃",
            description = "로그아웃 기능 ex) POST /api/users/logout")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    public ResponseEntity<SuccessResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ApiResponseUtil.success(null, "로그아웃 성공");
    }

    @PostMapping("/reissue")
    @Operation(summary = "accessToken 재발급",
            description = "accessToken 재발급 기능 ex) POST /api/users/reissue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 refresh 토큰(만료되었거나 일치하지 않음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "로그인 실패 (사용자를 찾을 수 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> reissue(HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDTO newToken = authService.reissue(request, response);
        return ApiResponseUtil.success(newToken);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<String>> getMyInfo(Authentication authentication) {
            // SecurityContext에서 로그인한 사용자 정보 확인
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ApiResponseUtil.success("Hello, " + userDetails.getUsername());
    }

}
