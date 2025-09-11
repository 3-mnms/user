package com.tekcit.festival.domain.user.dto.api;

import com.tekcit.festival.domain.user.dto.request.AddressRequestDTO;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
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

import java.util.List;

public interface AddressApiSpecification {
    @Operation(summary = "회원 주소 정보 추가",
            description = "회원 주소 정보 추가, AddressRequestDTO를 포함해야 합니다. ex) POST /api/addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항(주소, 우편번호, 이름, 전화번호) 위반",
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
    ResponseEntity<SuccessResponse<AddressDTO>> addAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal String principal);


    @Operation(summary = "회원 주소 정보 수정",
            description = "회원 주소 정보 수정, AddressRequestDTO를 포함해야 합니다. ex) PATCH /api/addresses/updateAddress/{addressId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 입력 사항 위반 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "필수 입력 사항(주소, 우편번호, 이름, 전화번호) 위반",
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
            @ApiResponse(responseCode = "403", description = "허용되지 않는 행동 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "로그인 한 user의 userId, 수정하려는 주소지 userId 정보 일치하지 않을 경우",
                    value = """
                                {
                                   "success": false,
                                   "code": "ADDRESS_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다. 작성자만이 주소를 수정 또는 삭제할 수 있습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패, 주소 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음 또는 수정하려는 주소를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND or ADDRESS_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s or 주소가 존재하지 않습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<AddressDTO>> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal String principal);

    @Operation(summary = "회원 주소 기본 배송지 수정",
            description = "회원 주소 기본 배송지 수정, ex) PATCH /api/addresses/changeDefault/{addressId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "허용되지 않는 행동 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "로그인 한 user의 userId, 수정하려는 주소지 userId 정보 일치하지 않을 경우",
                    value = """
                                {
                                   "success": false,
                                   "code": "ADDRESS_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다. 작성자만이 주소를 수정 또는 삭제할 수 있습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패, 주소 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음 또는 수정하려는 주소를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND or ADDRESS_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s or 주소가 존재하지 않습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<AddressDTO>> updateDefault(@PathVariable Long addressId, @AuthenticationPrincipal String principal);

    @Operation(summary = "회원 주소 삭제",
            description = "회원 주소 삭제, ex) DELETE /api/addresses/{addressId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "기본 배송지 삭제 불가 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "기본 배송지 삭제 불가",
                    value = """
                                {
                                   "success": false,
                                   "code": "ADDRESS_DEFAULT_NOT_DELETED",
                                   "message": "기본 주소지는 삭제할 수 없습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "403", description = "허용되지 않는 행동 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "로그인 한 user의 userId, 삭제하려는 주소지 userId 정보 일치하지 않을 경우",
                    value = """
                                {
                                   "success": false,
                                   "code": "ADDRESS_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다. 작성자만이 주소를 수정 또는 삭제할 수 있습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패, 주소 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음 또는 삭제하려는 주소를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND or ADDRESS_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s or 주소가 존재하지 않습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal String principal);

    @Operation(summary = "회원 주소 정보 전체 조회",
            description = "회원 주소 정보 전체 조회 ex) GET /api/addresses/allAddress")
    @ApiResponses(value = {
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
    ResponseEntity<SuccessResponse<List<AddressDTO>>> getAllAddresses(@AuthenticationPrincipal String principal);


    @Operation(summary = "회원 주소 기본 배송지 조회",
            description = "회원 주소 기본 배송지 정보 조회 ex) GET /api/addresses/defaultAddress")
    @ApiResponses(value = {
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
    ResponseEntity<SuccessResponse<AddressDTO>> getDefaultAddress(@AuthenticationPrincipal String principal);

    @Operation(summary = "회원 주소 정보 한 개 조회",
            description = "회원 주소 정보 한 개 조회 ex) GET /api/addresses/{addressId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "허용되지 않는 행동 ", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "로그인 한 user의 userId, 조회하려는 주소지 userId 정보 일치하지 않을 경우",
                    value = """
                                {
                                   "success": false,
                                   "code": "ADDRESS_NOT_ALLOWED",
                                   "message": "허용되지 않는 행동입니다. 작성자만이 주소를 수정 또는 삭제할 수 있습니다."
                                 }
                            """
            )
            )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 조회 실패, 주소 조회 실패", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                    summary = "user를 찾을 수 없음 또는 조회하려는 주소를 찾을 수 없음",
                    value = """
                                {
                                   "success": false,
                                   "code": "USER_NOT_FOUND or ADDRESS_NOT_FOUND",
                                   "message": "해당 사용자를 찾을 수 없습니다. ID: %s or 주소가 존재하지 않습니다."
                                 }
                            """
            )
            )
            )
    }
    )
    ResponseEntity<SuccessResponse<AddressDTO>> getAddress(@AuthenticationPrincipal String principal, @PathVariable Long addressId);

    }
