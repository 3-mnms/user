package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.service.UserInfoService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 정보 조회 API", description = "회원 생성, 조회, 탈퇴")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping(value="/booking-profile/{userId}")
    @Operation(summary = "예매 시 사용자 정보",
            description = "예매 시 사용자 정보(email), ex) GET /api/users/booking-profile/{userId}")
    public ResponseEntity<SuccessResponse<BookingProfileDTO>> bookingProfileInfo(@Valid @PathVariable Long userId){
        BookingProfileDTO bookingProfile = userInfoService.bookingProfileInfo(userId);
        return ApiResponseUtil.success(bookingProfile);
    }

    @PostMapping(value = "/reservationList")
    @Operation(summary = "예매자 정보 조회",
            description = "예매자 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/reservationList")
    public ResponseEntity<SuccessResponse<List<ReservationUserDTO>>> getReservationUserInfo(@RequestBody List<Long> userIds){
        List<ReservationUserDTO> reservationUserDTOS = userInfoService.getReservationUserInfo(userIds);
        return ApiResponseUtil.success(reservationUserDTOS);
    }

    @PostMapping(value = "/statisticsList")
    @Operation(summary = "통계 정보 조회",
            description = "통계 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/statisticsList")
    public ResponseEntity<SuccessResponse<List<StatisticsDTO>>> getStatisticsInfo(@RequestBody List<Long> userIds){
        List<StatisticsDTO> statisticsDTOS = userInfoService.getStatisticsInfo(userIds);
        return ApiResponseUtil.success(statisticsDTOS);
    }

    @GetMapping(value = "/preReservation")
    @Operation(summary = "가예매자 정보 조회",
            description = "가예매자 정보 조회. ex) POST /api/users/preReservation")
    public ResponseEntity<SuccessResponse<PreReservationDTO>> getPreReservationInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        PreReservationDTO preReservationDTO = userInfoService.getPreReservationInfo(userDetails.getUser().getUserId());
        return ApiResponseUtil.success(preReservationDTO);
    }

    @GetMapping(value = "/assignment")
    @Operation(summary = "양도 시 사용자 정보 조회",
            description = "양도 시 사용자 정보 조회. ex) GET /api/users/assignment?email=test@test.com")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> getAssignmentUserInfo(@RequestParam String email){
        AssignmentDTO assignmentDTO = userInfoService.getAssignmentUserInfo(email);
        return ApiResponseUtil.success(assignmentDTO);
    }

}
