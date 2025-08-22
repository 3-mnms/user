package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.*;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.service.UserService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import com.tekcit.festival.utils.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 생성, 조회, 탈퇴")
public class UserController {

    private final UserService userService;
    private final CookieUtil cookieUtil;

    @PostMapping(value="/signupUser")
    @Operation(summary = "회원 가입(일반 유저)",
            description = "일반 유저 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(일반 유저)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupUser(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupUser = userService.signupUser(signupUserDTO);
        return ApiResponseUtil.success(signupUser);
    }

    @PostMapping(value="/signupHost")
    @Operation(summary = "회원 가입(축제 주최측)",
            description = "축제 주최측 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupHost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(축제 주최측)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupHost(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupHost = userService.signupHost(signupUserDTO);
        return ApiResponseUtil.success(signupHost);
    }

    @PostMapping(value="/signupAdmin")
    @Operation(summary = "회원 가입(운영 관리자)",
            description = "운영 관리자 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupAdmin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(운영 관리자)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupAdmin(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupAdmin = userService.signupAdmin(signupUserDTO);
        return ApiResponseUtil.success(signupAdmin);
    }

    @GetMapping(value="/checkLoginId")
    @Operation(summary = "로그인 아이디 중복 확인",
            description = "로그인 아이디 중복 확인, ex) GET /api/users/checkLoginId?loginId=test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 아이디 중복 체크(true면 중복 아님, false면 중복)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    public ResponseEntity<SuccessResponse<Boolean>> checkLoginId(@RequestParam String loginId){
        boolean isLoginIdAvailable = userService.checkLoginId(loginId);
        return ApiResponseUtil.success(isLoginIdAvailable);
    }

    @GetMapping(value="/checkEmail")
    @Operation(summary = "이메일 주소 중복 확인",
            description = "이메일 주소 중복 확인, ex) GET /api/users/checkEmail?email=test@test.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 주소 중복 체크(true면 중복 아님, false면 중복)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    public ResponseEntity<SuccessResponse<Boolean>> checkEmail(@RequestParam String email){
        boolean isEmailAvailable = userService.checkEmail(email);
        return ApiResponseUtil.success(isEmailAvailable);
    }

    @DeleteMapping
    @Operation(summary = "일반 회원 탈퇴",
            description = "일반 회원 탈퇴, ex) DELETE /api/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal(expression = "user.userId") Long userId){
        userService.deleteUser(userId);
        ResponseCookie cookie = cookieUtil.deleteRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping(value="/findLoginId")
    @Operation(summary = "아이디 찾기",
            description = "로그인 아이디 찾기, FindLoginIdDTO를 포함해야 합니다. ex) POST /api/users/findLoginId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<String>> findLoginId(@Valid @RequestBody FindLoginIdDTO findLoginIdDTO){
        String loginId = userService.findLoginId(findLoginIdDTO);
        return ApiResponseUtil.success(loginId);
    }

    @PostMapping(value="/findRegisteredEmail")
    @Operation(summary = "비밀번호 찾기",
            description = "로그인 비밀번호 찾기 1단계, FindLoginPwDTO(로그인아이디, 이름)을 포함해야 합니다. ex) POST /api/users/findRegisteredEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공 이메일 주소 return",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<String>> findRegisteredEmail(@Valid @RequestBody FindPwEmailDTO findPwEmailDTO){
        String email = userService.findRegisteredEmail(findPwEmailDTO);
        return ApiResponseUtil.success(email);
    }

    @PatchMapping(value="/resetPasswordEmail")
    @Operation(summary = "비밀번호 재설정",
            description = "로그인 비밀번호 찾기 2단계, FindPwResetDTO(로그인아이디, 이메일, 새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/users/resetPasswordEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증번호 검증 후 새로운 비밀번호 재설정",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> resetPasswordEmail(@Valid @RequestBody FindPwResetDTO findPwResetDTO){
        userService.resetPasswordEmail(findPwResetDTO);
        return ApiResponseUtil.success();
    }
}