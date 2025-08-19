package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.response.BookingProfileDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
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
import org.springframework.security.core.Authentication;
import com.tekcit.festival.exception.global.ErrorResponse;
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
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (잘못된 데이터, 필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupHost(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupHost = userService.signupHost(signupUserDTO);
        return ApiResponseUtil.success(signupHost);
    }

    @PostMapping(value="/signupAdmin")
    @Operation(summary = "회원 가입(운영 관리자)",
            description = "운영 관리자 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupAdmin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(운영 관리자)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse<UserResponseDTO>> signupAdmin(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupAdmin = userService.signupAdmin(signupUserDTO);
        return ApiResponseUtil.success(signupAdmin);
    }

    @PatchMapping(value="/{userId}/state")
    @Operation(summary = "회원 상태 변경 (활성화 / 비활성화)",
            description = "userId를 기준으로 회원의 활성 상태(active)를 true/false로 변경합니다. ex) PATCH /api/users/{userId}/state?active=false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 상태(active) 조정 완료"),
            @ApiResponse(responseCode = "403", description = "회원 상태(active) 조정 실패(운영 관리자는 불가능)"),
            @ApiResponse(responseCode = "404", description = "회원 상태(active) 조정 실패(해당 유저를 찾을 수 없거나 운영 관리자만 상태 관리를 할 수 있습니다.)")
    })
    public ResponseEntity<SuccessResponse<Void>> changeState(@Valid @PathVariable Long userId, @RequestParam boolean active, Authentication authentication){
        userService.changeState(userId, active, authentication);
        return ApiResponseUtil.success(null, "회원 상태 조정 완료");
    }

    @GetMapping(value="/checkLoginId")
    @Operation(summary = "로그인 아이디 중복 확인",
            description = "로그인 아이디 중복 확인, ex) GET /api/users/checkLoginId?loginId=test")
    public ResponseEntity<SuccessResponse<Boolean>> checkLoginId(@RequestParam String loginId){
        boolean isLoginIdAvailable = userService.checkLoginId(loginId);
        return ApiResponseUtil.success(isLoginIdAvailable);
    }

    @GetMapping(value="/checkEmail")
    @Operation(summary = "이메일 주소 중복 확인",
            description = "이메일 주소 중복 확인, ex) GET /api/users/checkEmail?email=test@test.com")
    public ResponseEntity<SuccessResponse<Boolean>> checkEmail(@RequestParam String email){
        boolean isEmailAvailable = userService.checkEmail(email);
        return ApiResponseUtil.success(isEmailAvailable);
    }

    @GetMapping(value="/booking-profile/{userId}")
    @Operation(summary = "예약자 정보",
            description = "예약자 정보, ex) GET /api/users/booking-profile/{userId}")
    public ResponseEntity<SuccessResponse<BookingProfileDTO>> bookingProfile(@Valid @PathVariable Long userId){
        BookingProfileDTO bookingProfile = userService.bookingProfile(userId);
        return ApiResponseUtil.success(bookingProfile);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal(expression = "user.userId") Long userId){
        userService.deleteUser(userId);
        ResponseCookie cookie = cookieUtil.deleteRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
