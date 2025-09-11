package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.api.MyPageApiSpecification;
import com.tekcit.festival.domain.user.dto.request.CheckPwDTO;
import com.tekcit.festival.domain.user.dto.request.ResetPwDTO;
import com.tekcit.festival.domain.user.dto.request.UpdateUserRequestDTO;
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
public class MyPageController implements MyPageApiSpecification {
    private final MyPageService myPageService;

    @GetMapping(value="/userInfo")
    public ResponseEntity<SuccessResponse<Object>> myPageUserInfo(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        Object myPageDto = myPageService.getUserInfo(userId);
        return ApiResponseUtil.success(myPageDto);
    }

    @PatchMapping(value="/updateUser")
    public ResponseEntity<SuccessResponse<UpdateUserResponseDTO>> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        UpdateUserResponseDTO updateUserDTO = myPageService.updateUser(updateUserRequestDTO, userId);
        return ApiResponseUtil.success(updateUserDTO);
    }

    @PostMapping(value="/checkPassword")
    public ResponseEntity<SuccessResponse<Void>> checkPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody CheckPwDTO checkPwDTO){
        Long userId = Long.parseLong(principal);
        myPageService.checkPassword(userId, checkPwDTO);
        return ApiResponseUtil.success(null, "비밀번호가 일치합니다.");
    }

    @PatchMapping(value="/resetPassword")
    public ResponseEntity<SuccessResponse<Void>> resetPassword(@AuthenticationPrincipal String principal, @Valid @RequestBody ResetPwDTO resetPwDTO){
        Long userId = Long.parseLong(principal);
        myPageService.resetPassword(userId, resetPwDTO);
        return ApiResponseUtil.success(null, "비밀번호를 수정했습니다.");
    }

}
