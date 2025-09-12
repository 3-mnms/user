package com.tekcit.festival.domain.user.controller.api;

import com.tekcit.festival.domain.user.dto.request.CheckPwDTO;
import com.tekcit.festival.domain.user.dto.request.ResetPwDTO;
import com.tekcit.festival.domain.user.dto.request.UpdateUserRequestDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageCommonDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageHostDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageUserDTO;
import com.tekcit.festival.domain.user.dto.response.UpdateUserResponseDTO;
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
import org.springframework.web.bind.annotation.RequestBody;

public interface MyPageApiSpecification {
    @Operation(summary = "마이페이지 회원 정보 조회",
            description = "마이페이지 회원 정보 조회, MyPageUserDTO(USER), MyPageHostDTO(HOST), MyPageCommonDTO(ADMIN) Role에 따라 return 값이 달라집니다." +
                    "ex) GET /api/myPage/userInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(oneOf = { MyPageUserDTO.class, MyPageHostDTO.class, MyPageCommonDTO.class })
            )),
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
    ResponseEntity<SuccessResponse<Object>> myPageUserInfo(@AuthenticationPrincipal String principal);

    @Operation(summary = "마이페이지 회원 정보 수정",
            description = "마이페이지 회원 정보 수정, UpdateUserRequestDTO를 포함해야 합니다. ex) PATCH /api/myPage/updateUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(필수 입력 사항 위반(이름, 전화번호, 주민번호))",
                    value = """
                                {
                                   "success": false,
                                   "code": "VALIDATION_ERROR",
                                   "message": "%s는 필수 입력사항 입니다."
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
    ResponseEntity<SuccessResponse<UpdateUserResponseDTO>> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO, @AuthenticationPrincipal String principal);

    @Operation(summary = "마이페이지 기존 비밀번호 일치 여부 확인",
            description = "마이페이지에서 기존 비밀번호 일치 여부를 확인할 수 있습니다. CheckPwDTO(기존 비밀번호)를 포함해야 합니다. ex) POST /api/myPage/checkPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "일치하지 않는 비밀번호 or 필수 입력 사항 위반", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "일치하지 않는 비밀번호 or 필수 입력 사항 위반(기존 비밀번호)",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_PASSWORD_NOT_EQUAL_ERROR or VALIDATION_ERROR",
                                   "message": "일치하지 않는 비밀번호입니다. or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "403", description = "카카오 계정은 비밀번호 없음", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "카카오 가입 사용자는 비밀번호가 존재하지 않음",
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
    ResponseEntity<SuccessResponse<Void>> checkPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody CheckPwDTO checkPwDTO);

    @Operation(summary = "마이페이지 비밀번호 재설정",
            description = "마이페이지에서 비밀번호를 변경할 수 있습니다. ResetPwDTO(새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/myPage/resetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항 위반(새로운 비밀번호)",
                    value = """
                                {
                                   "success": false,
                                   "code": "VALIDATION_ERROR",
                                   "message": "%s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "403", description = "카카오 계정은 비밀번호 없음", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "카카오 가입 사용자는 비밀번호가 존재하지 않음",
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
    ResponseEntity<SuccessResponse<Void>> resetPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody ResetPwDTO resetPwDTO);

    }
