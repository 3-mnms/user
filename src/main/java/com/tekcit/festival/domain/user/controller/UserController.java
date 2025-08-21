package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
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
import com.tekcit.festival.exception.global.ErrorResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "예매 시 사용자 정보",
            description = "예매 시 사용자 정보(email), ex) GET /api/users/booking-profile/{userId}")
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

    @PatchMapping(value="/resetPasswordWithEmail")
    @Operation(summary = "비밀번호 재설정",
            description = "로그인 비밀번호 찾기 2단계, FindPwResetDTO(로그인아이디, 이메일, 새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/users/resetPasswordWithEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증번호 검증 후 새로운 비밀번호 재설정",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> resetPasswordWithEmail(@Valid @RequestBody FindPwResetDTO findPwResetDTO){
        userService.resetPasswordWithEmail(findPwResetDTO);
        return ApiResponseUtil.success();
    }

    @GetMapping(value="/myPage/userInfo")
    @Operation(summary = "마이페이지 회원 정보 조회",
            description = "마이페이지 회원 정보 조회, MyPageUserDTO(USER), MyPageHostDTO(HOST), MyPageCommonDTO(ADMIN) Role에 따라 return 값이 달라집니다." +
                    "ex) GET /api/users/myPage/userInfo")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(oneOf = { MyPageUserDTO.class, MyPageHostDTO.class, MyPageCommonDTO.class })
    ))
    public ResponseEntity<SuccessResponse<Object>> myPageUserInfo(@AuthenticationPrincipal(expression = "user.userId") Long userId){
        Object myPageDto = userService.getUserInfo(userId);
        return ApiResponseUtil.success(myPageDto);
    }

    @PatchMapping(value="/updateUser")
    @Operation(summary = "마이페이지 회원 정보 수정",
            description = "마이페이지 회원 정보 수정, UpdateUserRequestDTO를 포함해야 합니다. ex) PATCH /api/users/updateUser")
    public ResponseEntity<SuccessResponse<UpdateUserResponseDTO>> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO, @AuthenticationPrincipal(expression = "user.userId") Long userId){
        UpdateUserResponseDTO updateUserDTO = userService.updateUser(updateUserRequestDTO, userId);
        return ApiResponseUtil.success(updateUserDTO);
    }

    @PostMapping(value="/checkPassword")
    @Operation(summary = "마이페이지 기존 비밀번호 일치 여부 확인",
            description = "마이페이지에서 기존 비밀번호 일치 여부를 확인할 수 있습니다. CheckPwDTO(기존 비밀번호)를 포함해야 합니다. ex) POST /api/users/checkPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 일치합니다.",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> checkPassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CheckPwDTO checkPwDTO){
        userService.checkPassword(userDetails.getUser().getUserId(), checkPwDTO);
        return ApiResponseUtil.success(null, "비밀번호가 일치합니다.");
    }

    @PatchMapping(value="/resetPassword")
    @Operation(summary = "마이페이지 비밀번호 재설정",
            description = "마이페이지에서 비밀번호를 변경할 수 있습니다. ResetPwDTO(새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/users/resetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 비밀번호 재설정",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> resetPassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ResetPwDTO resetPwDTO){
        userService.resetPassword(userDetails.getUser().getUserId(), resetPwDTO);
        return ApiResponseUtil.success(null, "비밀번호를 수정했습니다.");
    }

    @PostMapping(value = "/reservationList")
    @Operation(summary = "예매자 정보 조회",
            description = "예매자 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/reservationList")
    public ResponseEntity<SuccessResponse<List<ReservationUserDTO>>> getReservationUserInfo(@RequestBody List<Long> userIds){
        List<ReservationUserDTO> reservationUserDTOS = userService.getReservationUserInfo(userIds);
        return ApiResponseUtil.success(reservationUserDTOS);
    }

    @PostMapping(value = "/statistics")
    @Operation(summary = "통계 정보 조회",
            description = "통계 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/statistics")
    public ResponseEntity<SuccessResponse<List<StatisticsDTO>>> getStatisticsInfo(@RequestBody List<Long> userIds){
        List<StatisticsDTO> statisticsDTOS = userService.getStatisticsInfo(userIds);
        return ApiResponseUtil.success(statisticsDTOS);
    }

    @GetMapping(value = "/preReservation")
    @Operation(summary = "가예매자 정보 조회",
            description = "가예매자 정보 조회. ex) POST /api/users/preReservation")
    public ResponseEntity<SuccessResponse<PreReservationDTO>> getPreReservation(@AuthenticationPrincipal CustomUserDetails userDetails){
        PreReservationDTO preReservationDTO = userService.getPreReservation(userDetails.getUser().getUserId());
        return ApiResponseUtil.success(preReservationDTO);
    }

    @GetMapping(value = "/assignment")
    @Operation(summary = "가예매자 정보 조회",
            description = "가예매자 정보 조회. ex) GET /api/users/assignment?email=test@test.com")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> getAssignmentUserInfo(@RequestParam String email){
        AssignmentDTO assignmentDTO = userService.getAssignmentUserInfo(email);
        return ApiResponseUtil.success(assignmentDTO);
    }



}