package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.api.UserApiSpecification;
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
public class UserController implements UserApiSpecification {

    private final UserService userService;
    private final CookieUtil cookieUtil;

    @PostMapping(value="/signupUser")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupUser(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupUser = userService.signupUser(signupUserDTO);
        return ApiResponseUtil.success(signupUser);
    }

    @PostMapping(value="/signupHost")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupHost(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupHost = userService.signupHost(signupUserDTO);
        return ApiResponseUtil.success(signupHost);
    }

    @PostMapping(value="/signupAdmin")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupAdmin(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupAdmin = userService.signupAdmin(signupUserDTO);
        return ApiResponseUtil.success(signupAdmin);
    }

    @GetMapping(value="/checkLoginId")
    public ResponseEntity<SuccessResponse<Boolean>> checkLoginId(@RequestParam String loginId){
        boolean isLoginIdAvailable = userService.checkLoginId(loginId);
        return ApiResponseUtil.success(isLoginIdAvailable);
    }

    @GetMapping(value="/checkEmail")
    public ResponseEntity<SuccessResponse<Boolean>> checkEmail(@RequestParam String email){
        boolean isEmailAvailable = userService.checkEmail(email);
        return ApiResponseUtil.success(isEmailAvailable);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        userService.deleteUser(userId);
        ResponseCookie cookie = cookieUtil.deleteRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping(value="/findLoginId")
    public ResponseEntity<SuccessResponse<String>> findLoginId(@Valid @RequestBody FindLoginIdDTO findLoginIdDTO){
        String loginId = userService.findLoginId(findLoginIdDTO);
        return ApiResponseUtil.success(loginId);
    }

    @PostMapping(value="/findRegisteredEmail")
    public ResponseEntity<SuccessResponse<String>> findRegisteredEmail(@Valid @RequestBody FindPwEmailDTO findPwEmailDTO){
        String email = userService.findRegisteredEmail(findPwEmailDTO);
        return ApiResponseUtil.success(email);
    }

    @PatchMapping(value="/resetPasswordEmail")
    public ResponseEntity<SuccessResponse<Void>> resetPasswordEmail(@Valid @RequestBody FindPwResetDTO findPwResetDTO){
        userService.resetPasswordEmail(findPwResetDTO);
        return ApiResponseUtil.success();
    }
}