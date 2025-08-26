package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.request.CheckPwDTO;
import com.tekcit.festival.domain.user.dto.request.ResetPwDTO;
import com.tekcit.festival.domain.user.dto.request.UpdateUserRequestDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageCommonDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageHostDTO;
import com.tekcit.festival.domain.user.dto.response.MyPageUserDTO;
import com.tekcit.festival.domain.user.dto.response.UpdateUserResponseDTO;
import com.tekcit.festival.domain.user.service.MyPageService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myPage")
@RequiredArgsConstructor
@Tag(name = "마이 페이지 API", description = "회원 생성, 조회, 탈퇴")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping(value="/userInfo")
    @Operation(summary = "마이페이지 회원 정보 조회",
            description = "마이페이지 회원 정보 조회, MyPageUserDTO(USER), MyPageHostDTO(HOST), MyPageCommonDTO(ADMIN) Role에 따라 return 값이 달라집니다." +
                    "ex) GET /api/myPage/userInfo")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(oneOf = { MyPageUserDTO.class, MyPageHostDTO.class, MyPageCommonDTO.class })
    ))
    public ResponseEntity<SuccessResponse<Object>> myPageUserInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        Object myPageDto = myPageService.getUserInfo(userId);
        return ApiResponseUtil.success(myPageDto);
    }

    @PatchMapping(value="/updateUser")
    @Operation(summary = "마이페이지 회원 정보 수정",
            description = "마이페이지 회원 정보 수정, UpdateUserRequestDTO를 포함해야 합니다. ex) PATCH /api/myPage/updateUser")
    public ResponseEntity<SuccessResponse<UpdateUserResponseDTO>> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        UpdateUserResponseDTO updateUserDTO = myPageService.updateUser(updateUserRequestDTO, userId);
        return ApiResponseUtil.success(updateUserDTO);
    }

    @PostMapping(value="/checkPassword")
    @Operation(summary = "마이페이지 기존 비밀번호 일치 여부 확인",
            description = "마이페이지에서 기존 비밀번호 일치 여부를 확인할 수 있습니다. CheckPwDTO(기존 비밀번호)를 포함해야 합니다. ex) POST /api/myPage/checkPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 일치합니다.",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> checkPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody CheckPwDTO checkPwDTO){
        Long userId = Long.parseLong(principal);
        myPageService.checkPassword(userId, checkPwDTO);
        return ApiResponseUtil.success(null, "비밀번호가 일치합니다.");
    }

    @PatchMapping(value="/resetPassword")
    @Operation(summary = "마이페이지 비밀번호 재설정",
            description = "마이페이지에서 비밀번호를 변경할 수 있습니다. ResetPwDTO(새로운 비밀번호)를 포함해야 합니다. ex) PATCH /api/myPage/resetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 비밀번호 재설정",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
    })
    public ResponseEntity<SuccessResponse<Void>> resetPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody ResetPwDTO resetPwDTO){
        Long userId = Long.parseLong(principal);
        myPageService.resetPassword(userId, resetPwDTO);
        return ApiResponseUtil.success(null, "비밀번호를 수정했습니다.");
    }

}
