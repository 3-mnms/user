package com.tekcit.festival.domain.user.dto.api;

import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserInfoApiSpecification {
    @Operation(summary = "사용자 나이 확인",
            description = "사용자 나이 확인(age), ex) GET /api/users/checkAge")
    @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
            summary = "user를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<CheckAgeDTO>> checkUserAgeInfo(@AuthenticationPrincipal String principal);

    @Operation(summary = "예매 시 사용자 정보",
            description = "예매 시 사용자 정보(email, 이름), ex) GET /api/users/booking-profile/{userId}")
    @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
            summary = "user를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<BookingProfileDTO>> bookingProfileInfo(@Valid @PathVariable Long userId);

    @Operation(summary = "예매자 정보 조회",
            description = "예매자 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/reservationList")
    ResponseEntity<SuccessResponse<List<ReservationUserDTO>>> getReservationUserInfo(@RequestBody List<Long> userIds);

    @Operation(summary = "통계 정보 조회",
            description = "통계 정보 조회, 예매자 userId가 리스트로 주어져야 합니다. ex) POST /api/users/statisticsList")
    ResponseEntity<List<StatisticsDTO>> getStatisticsInfo(@RequestBody List<Long> userIds);

    @Operation(summary = "가예매자 정보 조회",
            description = "가예매자 정보 조회. ex) POST /api/users/preReservation")
    @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
            summary = "user를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<PreReservationDTO>> getPreReservationInfo(@AuthenticationPrincipal String principal);


    @Operation(summary = "양도 시 이메일을 통한 양수자 정보 조회",
            description = "양도 시 이메일을 통한 양수자 정보 조회. ex) GET /api/users/transferee?email=test@test.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "본인에게는 양도 불가능", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "본인에게는 양도 불가능",
                    value = """
                                {
                                   "success": false,
                                   "code": "SELF_TRANSFER_FORBIDDEN",
                                   "message": "본인에게는 양도를 할 수 없습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<AssignmentDTO>> transfereeInfo(@AuthenticationPrincipal String principal, @RequestParam String email);

    @Operation(summary = "양도 시 현재 양도자 정보 조회",
            description = "양도 시 현재 양도자 정보 조회. ex) GET /api/users/transferor?email=test@test.com")
    @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
            summary = "user를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<AssignmentDTO>> transferorInfo(@AuthenticationPrincipal String principal);

    @Operation(summary = "사용자 위도 경도 정보 조회",
            description = "사용자 위도 경도 정보 조회. ex) GET /api/users/geocodeInfo")
    @ApiResponse(responseCode = "404", description = "사용자 조회 실패 or 주소 조회 실패", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
            summary = "user를 찾을 수 없습니다. or 기본 주소를 찾을 수 없습니다.",
            value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND or ADDRESS_DEFAULT_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s or 기본 주소가 존재하지 않습니다."
                                 }
                            """
    )
    )
    )
    ResponseEntity<SuccessResponse<GeoCodeInfoDTO>> geoCodeInfo(@AuthenticationPrincipal String principal);

    }
