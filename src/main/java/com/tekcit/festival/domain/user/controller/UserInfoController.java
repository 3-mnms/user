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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 정보 조회 API", description = "예매, 통계 가예매자, 양수자, 양도자 정보 조회")
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
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
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

    @GetMapping(value = "/transferee")
    @Operation(summary = "양도 시 이메일을 통한 양수자 정보 조회",
            description = "양도 시 이메일을 통한 양수자 정보 조회. ex) GET /api/users/transferee?email=test@test.com")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> transfereeInfo(@RequestParam String email){
        AssignmentDTO assignmentDTO = userInfoService.transfereeInfo(email);
        return ApiResponseUtil.success(assignmentDTO);
    }

    @GetMapping(value = "/transferor")
    @Operation(summary = "양도 시 현재 양도자 정보 조회",
            description = "양도 시 현재 양도자 정보 조회. ex) GET /api/users/transferor?email=test@test.com")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> transferorInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        AssignmentDTO assignmentDTO = userInfoService.transferorInfo(userDetails.getUser().getUserId());
        return ApiResponseUtil.success(assignmentDTO);
    }

}
