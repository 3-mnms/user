package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.api.AdminApiSpecification;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.dto.response.AdminHostListDTO;
import com.tekcit.festival.domain.user.dto.response.AdminUserListDTO;
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
public class AdminController implements AdminApiSpecification {

    private final AdminService adminService;

    @GetMapping(value="/userList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AdminUserListDTO>>> getAllUser(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AdminUserListDTO> userListDTOS = adminService.getAllUser(userId);
        return ApiResponseUtil.success(userListDTOS);
    }

    @GetMapping(value="/hostList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AdminHostListDTO>>> getAllHostList(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AdminHostListDTO> hostListDTOS = adminService.getAllHost(userId);
        return ApiResponseUtil.success(hostListDTOS);
    }

    @PatchMapping(value="/{userId}/state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> changeState(@PathVariable Long userId, @RequestParam boolean active, @AuthenticationPrincipal String principal){
        Long adminId = Long.parseLong(principal);
        adminService.changeState(userId, active, adminId);
        return ApiResponseUtil.success(null, "회원 상태 조정 완료");
    }

    @DeleteMapping(value="/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteHost(@AuthenticationPrincipal String principal, @PathVariable Long userId){
        Long adminId = Long.parseLong(principal);
        adminService.deleteHost(adminId, userId);

        return ApiResponseUtil.success(null, "주최측 탈퇴 완료");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<AddressDTO>>> getAllAddresses(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AddressDTO> addressDTOS = adminService.getAllAddresses(userId);
        return ApiResponseUtil.success(addressDTOS);
    }
}
