package com.tekcit.festival.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 생성, 조회, 탈퇴")
public class HostController {
}
