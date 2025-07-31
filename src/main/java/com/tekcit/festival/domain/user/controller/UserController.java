package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 생성, 조회, 탈퇴")
public class UserController {

    private final UserService userService;

    @PostMapping(value="/signupUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(일반 유저)",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (잘못된 데이터, 필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Phone, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDTO> signupUser(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupUser = userService.signupUser(signupUserDTO);
        return ResponseEntity.ok(signupUser);
    }

    @PostMapping(value="/signupHost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(축제 주최측)",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Phone, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDTO> signupHost(@Valid @RequestBody SignupUserDTO signupUserDTO){
        UserResponseDTO signupHost = userService.signupHost(signupUserDTO);
        return ResponseEntity.ok(signupHost);
    }



}
