package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.response.AdminHostListDTO;
import com.tekcit.festival.domain.user.dto.response.AdminUserListDTO;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.service.AdminService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/admin")
@RequiredArgsConstructor
@Tag(name = "운영 관리자 api", description = "전체 회원 조회, 전체 주최자 조회")
public class AdminController {

    private final AdminService adminService;

    @GetMapping(value="/userList")
    @Operation(summary = "사용자 전체 목록 조회",
            description = "사용자 전체 목록 조회(user), ex) GET /api/users/admin/userList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AdminUserListDTO>>> getAllUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        User adminUser = userDetails.getUser();
        List<AdminUserListDTO> userListDTOS = adminService.getAllUser(adminUser);
        return ApiResponseUtil.success(userListDTOS);
    }

    @GetMapping(value="/hostList")
    @Operation(summary = "주최자 전체 목록 조회",
            description = "주최자 전체 목록 조회(host), ex) GET /api/users/admin/hostList")
    public ResponseEntity<SuccessResponse<List<AdminHostListDTO>>> getAllHostList(@AuthenticationPrincipal CustomUserDetails userDetails){
        User adminUser = userDetails.getUser();
        List<AdminHostListDTO> hostListDTOS = adminService.getAllHost(adminUser);
        return ApiResponseUtil.success(hostListDTOS);
    }

    @DeleteMapping(value="/{userId}")
    @Operation(summary = "주최자 탈퇴(삭제)",
            description = "운영관리자가 주최자 탈퇴(host), ex) DELETE /api/users/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteHost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userId){
        User adminUser = userDetails.getUser();
        adminService.deleteHost(adminUser, userId);

        return ResponseEntity.noContent().build();
    }

}
