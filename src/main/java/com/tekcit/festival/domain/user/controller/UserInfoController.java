package com.tekcit.festival.domain.user.controller;
import com.tekcit.festival.domain.user.dto.api.UserInfoApiSpecification;
import org.springframework.security.core.Authentication;

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
public class UserInfoController implements UserInfoApiSpecification {

    private final UserInfoService userInfoService;

    @GetMapping(value="/checkAge")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SuccessResponse<CheckAgeDTO>> checkUserAgeInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        CheckAgeDTO checkAgeDTO = userInfoService.checkUserAgeInfo(userId);
        return ApiResponseUtil.success(checkAgeDTO);
    }

    @GetMapping(value="/booking-profile/{userId}")
    public ResponseEntity<SuccessResponse<BookingProfileDTO>> bookingProfileInfo(@PathVariable Long userId){
        BookingProfileDTO bookingProfile = userInfoService.bookingProfileInfo(userId);
        return ApiResponseUtil.success(bookingProfile);
    }

    @PostMapping(value = "/reservationList")
    public ResponseEntity<SuccessResponse<List<ReservationUserDTO>>> getReservationUserInfo(@RequestBody List<Long> userIds){
        List<ReservationUserDTO> reservationUserDTOS = userInfoService.getReservationUserInfo(userIds);
        return ApiResponseUtil.success(reservationUserDTOS);
    }

    @PostMapping(value = "/statisticsList")
    public ResponseEntity<List<StatisticsDTO>> getStatisticsInfo(@RequestBody List<Long> userIds){
        List<StatisticsDTO> statisticsDTOS = userInfoService.getStatisticsInfo(userIds);
        return ResponseEntity.ok(statisticsDTOS);
    }

    @GetMapping(value = "/preReservation")
    public ResponseEntity<SuccessResponse<PreReservationDTO>> getPreReservationInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        PreReservationDTO preReservationDTO = userInfoService.getPreReservationInfo(userId);
        return ApiResponseUtil.success(preReservationDTO);
    }

    @GetMapping(value = "/transferee")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> transfereeInfo(@AuthenticationPrincipal String principal, @RequestParam String email){
        Long userId = Long.parseLong(principal);
        AssignmentDTO assignmentDTO = userInfoService.transfereeInfo(userId, email);
        return ApiResponseUtil.success(assignmentDTO);
    }

    @GetMapping(value = "/transferor")
    public ResponseEntity<SuccessResponse<AssignmentDTO>> transferorInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        AssignmentDTO assignmentDTO = userInfoService.transferorInfo(userId);
        return ApiResponseUtil.success(assignmentDTO);
    }

    @GetMapping(value = "/geocodeInfo")
    public ResponseEntity<SuccessResponse<GeoCodeInfoDTO>> geoCodeInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        GeoCodeInfoDTO geoCodeInfo = userInfoService.geoCodeInfo(userId);
        return ApiResponseUtil.success(geoCodeInfo);
    }

}
