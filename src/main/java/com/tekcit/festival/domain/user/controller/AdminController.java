package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.response.AdminUserListDTO;
import com.tekcit.festival.domain.user.dto.response.BookingProfileDTO;
import com.tekcit.festival.domain.user.service.AdminService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<SuccessResponse<List<AdminUserListDTO>>> getAllUser(Authentication authentication){
        List<AdminUserListDTO> userListDTOS = adminService.getAllUser(authentication);
        return ApiResponseUtil.success(userListDTOS);
    }

    @GetMapping(value="/hostList")
    @Operation(summary = "주최자 전체 목록 조회",
            description = "주최자 전체 목록 조회(host), ex) GET /api/users/admin/userList")
    public ResponseEntity<SuccessResponse<BookingProfileDTO>> getAllHostList(Authentication authentication){

        return ApiResponseUtil.success(bookingProfile);
    }

    @PatchMapping(value="/updateHost")
    @Operation(summary = "주최자 정보 수정",
            description = "주최자 정보 수정, ex) POST /api/users/admin/updateHost")
    public ResponseEntity<SuccessResponse<BookingProfileDTO>> updateHost(Authentication authentication){

        return ApiResponseUtil.success(bookingProfile);
    }

}
