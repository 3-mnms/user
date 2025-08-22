package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.response.AdminHostListDTO;
import com.tekcit.festival.domain.user.dto.response.AdminUserListDTO;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.service.AdminService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "운영 관리자 api", description = "전체 회원 조회, 전체 주최자 조회")
public class AdminController {

    private final AdminService adminService;

    @GetMapping(value="/userList")
    @Operation(summary = "사용자 전체 목록 조회",
            description = "사용자 전체 목록 조회(user), ex) GET /api/admin/userList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AdminUserListDTO>>> getAllUser(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AdminUserListDTO> userListDTOS = adminService.getAllUser(userId);
        return ApiResponseUtil.success(userListDTOS);
    }

    @GetMapping(value="/hostList")
    @Operation(summary = "주최자 전체 목록 조회",
            description = "주최자 전체 목록 조회(host), ex) GET /api/admin/hostList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AdminHostListDTO>>> getAllHostList(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AdminHostListDTO> hostListDTOS = adminService.getAllHost(userId);
        return ApiResponseUtil.success(hostListDTOS);
    }

    @PatchMapping(value="/{userId}/state")
    @Operation(summary = "회원 상태 변경 (활성화 / 비활성화)",
            description = "운영관리자는 userId를 기준으로 회원의 활성 상태(active)를 true/false로 변경할 수 있습니다. ex) PATCH /api/admin/{userId}/state?active=false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 상태(active) 조정 완료"),
            @ApiResponse(responseCode = "403", description = "회원 상태(active) 조정 실패(운영 관리자는 불가능)"),
            @ApiResponse(responseCode = "404", description = "회원 상태(active) 조정 실패(해당 유저를 찾을 수 없거나 운영 관리자만 상태 관리를 할 수 있습니다.)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> changeState(@PathVariable Long userId, @RequestParam boolean active, @AuthenticationPrincipal String principal){
        Long adminId = Long.parseLong(principal);
        adminService.changeState(userId, active, adminId);
        return ApiResponseUtil.success(null, "회원 상태 조정 완료");
    }

    @DeleteMapping(value="/{userId}")
    @Operation(summary = "주최자 탈퇴(삭제)",
            description = "운영관리자가 주최자 탈퇴(host), ex) DELETE /api/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteHost(@AuthenticationPrincipal String principal, @PathVariable Long userId){
        Long adminId = Long.parseLong(principal);
        adminService.deleteHost(adminId, userId);

        return ApiResponseUtil.success(null, "주최측 탈퇴 완료");
    }

}
