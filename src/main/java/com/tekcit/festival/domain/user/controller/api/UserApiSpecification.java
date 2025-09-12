package com.tekcit.festival.domain.user.controller.api;

import com.tekcit.festival.domain.user.dto.request.FindLoginIdDTO;
import com.tekcit.festival.domain.user.dto.request.FindPwEmailDTO;
import com.tekcit.festival.domain.user.dto.request.FindPwResetDTO;
import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApiSpecification {
    @Operation(summary = "회원 가입(일반 유저)",
            description = "일반 유저 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "이메일 인증 안 됨 or 필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(이메일 인증 안 됨 or 필수 입력 사항 위반(로그인 아이디, 로그인 비밀번호, 이름, 전화번호, 이메일, 주민번호, 주소, 우편번호))",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_EMAIL_NOT_VERIFIED or VALIDATION_ERROR",
                                   "message": "이메일이 인증되지 않았습니다. or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "이메일 인증 요청 조회를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "EMAIL_VERIFICATION_NOT_FOUND",
                                   "message": "인증 요청이 없습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패 (중복된 login ID, Email로 인한 conflict)",
                    value = """
                                {
                                   "success": false,
                                   "code": "DUPLICATE_LOGIN_ID or DUPLICATE_EMAIL_ID",
                                   "message": "이미 존재하는 아이디입니다. ID: %s, 이미 존재하는 이메일입니다. EMAIL: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<UserResponseDTO>> signupUser(@Valid @RequestBody SignupUserDTO signupUserDTO);

    @Operation(summary = "회원 가입(축제 주최측)",
            description = "축제 주최측 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupHost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(필수 입력 사항 위반(로그인 아이디, 로그인 비밀번호, 이름, 전화번호, 이메일, 사업체명))",
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
            @ApiResponse(responseCode = "409", description = "회원 가입 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패 (중복된 login ID, Email로 인한 conflict)",
                    value = """
                                {
                                   "success": false,
                                   "code": "DUPLICATE_LOGIN_ID or DUPLICATE_EMAIL_ID",
                                   "message": "이미 존재하는 아이디입니다. ID: %s, 이미 존재하는 이메일입니다. EMAIL: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<UserResponseDTO>> signupHost(@Valid @RequestBody SignupUserDTO signupUserDTO);

    @Operation(summary = "회원 가입(운영 관리자)",
            description = "운영 관리자 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/users/signupAdmin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(필수 입력 사항 위반(로그인 아이디, 로그인 비밀번호, 이름, 전화번호, 이메일))",
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
            @ApiResponse(responseCode = "409", description = "회원 가입 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패 (중복된 login ID, Email로 인한 conflict)",
                    value = """
                                {
                                   "success": false,
                                   "code": "DUPLICATE_LOGIN_ID or DUPLICATE_EMAIL_ID",
                                   "message": "이미 존재하는 아이디입니다. ID: %s, 이미 존재하는 이메일입니다. EMAIL: %s"
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<UserResponseDTO>> signupAdmin(@Valid @RequestBody SignupUserDTO signupUserDTO);

    @Operation(summary = "로그인 아이디 중복 확인",
            description = "로그인 아이디 중복 확인, ex) GET /api/users/checkLoginId?loginId=test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 아이디 중복 체크(true면 중복 아님, false면 중복)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
        }
    )
    ResponseEntity<SuccessResponse<Boolean>> checkLoginId(@RequestParam String loginId);

    @Operation(summary = "이메일 주소 중복 확인",
            description = "이메일 주소 중복 확인, ex) GET /api/users/checkEmail?email=test@test.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 주소 중복 체크(true면 중복 아님, false면 중복)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    ResponseEntity<SuccessResponse<Boolean>> checkEmail(@RequestParam String email);

    @Operation(summary = "일반 회원 탈퇴",
            description = "일반 회원 탈퇴, ex) DELETE /api/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "kakao unlink 오류 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "kakao unlink 오류",
                    value = """
                                {
                                   "success": false,
                                   "code": "KAKAO_UNLINK_FAILED",
                                   "message": "errorMessage"
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "403", description = "사용자 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "일반 사용자만 삭제(탈퇴) 가능합니다.",
                    value = """
                                {
                                   "success": false,
                                   "code": "AUTH_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다"
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
    ResponseEntity<Void> deleteUser(@AuthenticationPrincipal String principal);

    @Operation(summary = "아이디 찾기",
            description = "로그인 아이디 찾기, FindLoginIdDTO를 포함해야 합니다. ex) POST /api/users/findLoginId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항 위반(이름, 이메일))",
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
    ResponseEntity<SuccessResponse<String>> findLoginId(@Valid @RequestBody FindLoginIdDTO findLoginIdDTO);

    @Operation(summary = "비밀번호 찾기",
            description = "로그인 비밀번호 찾기 1단계, FindLoginPwDTO(로그인아이디, 이름)을 포함해야 합니다. ex) POST /api/users/findRegisteredEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항 위반(로그인아이디, 이름))",
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
    ResponseEntity<SuccessResponse<String>> findRegisteredEmail(@Valid @RequestBody FindPwEmailDTO findPwEmailDTO);

    @Operation(summary = "비밀번호 재설정",
            description = "로그인 비밀번호 찾기 2단계, FindPwResetDTO(로그인아이디, 이메일, 새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/users/resetPasswordEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "이메일 인증 안 됨 or 이메일 일치하지 않음 or 필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "회원 가입 실패(이메일 인증 안 됨 or 이메일 일치하지 않음 or 필수 입력 사항 위반(로그인 아이디, 이메일, 새로운 비밀번호))",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_EMAIL_NOT_VERIFIED or USER_EMAIL_NOT_MATCH or VALIDATION_ERROR",
                                   "message": "이메일이 인증되지 않았습니다. or 이메일이 일치하지 않습니다. EMAIL: %s or %s는 필수 입력사항 입니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "이메일 인증 요청 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "이메일 인증 요청 조회를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "EMAIL_VERIFICATION_NOT_FOUND",
                                   "message": "인증 요청이 없습니다."
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
    ResponseEntity<SuccessResponse<Void>> resetPasswordEmail(@Valid @RequestBody FindPwResetDTO findPwResetDTO);

    }
