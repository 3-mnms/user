package com.tekcit.festival.domain.user.dto.api;

import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.dto.response.AdminHostListDTO;
import com.tekcit.festival.domain.user.dto.response.AdminUserListDTO;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AdminApiSpecification {
    @Operation(summary = "사용자 전체 목록 조회",
            description = "사용자 전체 목록 조회(user), ex) GET /api/admin/userList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "ROLE: ADMIN 만 전체 일반 사용자 목록 조회 가능(HOST, USER 위반)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다."
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
    ResponseEntity<SuccessResponse<List<AdminUserListDTO>>> getAllUser(@AuthenticationPrincipal String principal);

    @Operation(summary = "주최자 전체 목록 조회",
            description = "주최자 전체 목록 조회(host), ex) GET /api/admin/hostList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "ROLE: ADMIN 만 전체 축제 주최측 목록 조회 가능(HOST, USER 위반)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다."
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
    ResponseEntity<SuccessResponse<List<AdminHostListDTO>>> getAllHostList(@AuthenticationPrincipal String principal);

    @Operation(summary = "회원 상태 변경 (활성화 / 비활성화)",
            description = "운영관리자는 userId를 기준으로 회원의 활성 상태(active)를 true/false로 변경할 수 있습니다. ex) PATCH /api/admin/{userId}/state?active=false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "ROLE: ADMIN 만 회원 상태 변경 가능(HOST, USER 위반), 운영관리자(ROLE:ADMIN)은 상태를 조정할 수 없습니다.",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "조정하려는 user를 찾을 수 없거나 관리자를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<Void>> changeState(@PathVariable Long userId, @RequestParam boolean active, @AuthenticationPrincipal String principal);

    @Operation(summary = "주최자 탈퇴(삭제)",
            description = "운영관리자가 주최자 탈퇴(host), ex) DELETE /api/admin/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "ROLE: ADMIN 만 사용자 삭제 가능(HOST, USER 위반), 운영관리자(ROLE:ADMIN)은 삭제할 수 없습니다.",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "삭제하려는 user를 찾을 수 없거나 관리자를 찾을 수 없습니다.",
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
    ResponseEntity<SuccessResponse<Void>> deleteHost(@AuthenticationPrincipal String principal, @PathVariable Long userId);

    @Operation(summary = "전체 회원 주소 정보 조회",
            description = "회원 주소 정보 조회 ex) GET /api/admin/addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "ROLE: ADMIN 만 전체 주소 조회 가능(HOST, USER 위반)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다."
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
    ResponseEntity<SuccessResponse<List<AddressDTO>>> getAllAddresses(@AuthenticationPrincipal String principal);

    }
