package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.FestivalDto;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.mapper.FestivalMapper;
import com.tekcit.festival.domain.user.service.FestivalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "공연 관리 API", description = "주최자 및 관리자의 공연 등록/수정/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class FestivalManageController {

    private final FestivalService festivalService;
    private final FestivalMapper festivalMapper;

    //HOST
    @Operation(summary = "공연 등록 (호스트)", description = "주최자가 새로운 공연을 등록합니다.")
    @PostMapping("/host")
    public ResponseEntity<Map<String, Object>> createFestival(@RequestBody FestivalDto request) {
        Festival created = festivalService.createFestival(festivalMapper.toEntity(request));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "🎉 공연 등록 성공",
                "data", festivalMapper.toDto(created)
        ));
    }

    @Operation(summary = "공연 수정 (호스트)", description = "주최자가 본인이 등록한 공연 정보를 수정합니다.")
    @PutMapping("/host/{festivalId}")
    public ResponseEntity<Map<String, Object>> updateFestival(
            @PathVariable Long festivalId,
            @RequestBody FestivalDto request
    ) {
        Festival updated = festivalService.updateFestival(festivalId, festivalMapper.toEntity(request));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✏️ 공연 수정 성공",
                "data", festivalMapper.toDto(updated)
        ));
    }

    @Operation(summary = "공연 삭제 (호스트)", description = "주최자가 본인이 등록한 공연을 삭제합니다.")
    @DeleteMapping("/host/{festivalId}")
    public ResponseEntity<Map<String, Object>> deleteFestival(
            @PathVariable Long festivalId,
            @RequestParam Long hostId // 👉 추후 JWT 기반 인증에서 추출 예정
    ) {
        festivalService.deleteFestivalByHost(festivalId, hostId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "🗑️ 공연 삭제 성공"
        ));
    }

    @Operation(summary = "내 공연 목록 조회 (호스트)", description = "주최자가 본인이 등록한 공연 목록을 조회합니다.")
    @GetMapping("/host")
    public ResponseEntity<Map<String, Object>> getMyFestivals(@RequestParam Long hostId) {
        List<Festival> list = festivalService.getFestivalsByHost(hostId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "📄 내 공연 목록 조회 성공",
                "data", festivalMapper.toDtoList(list)
        ));
    }

    //ADMIN
    @Operation(summary = "전체 공연 목록 조회 (관리자)", description = "운영자가 전체 공연 목록을 조회합니다.")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAllFestivals() {
        List<Festival> list = festivalService.getAllFestivals();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "📋 전체 공연 목록 조회 성공",
                "data", festivalMapper.toDtoList(list)
        ));
    }

    @Operation(summary = "공연 삭제 (관리자)", description = "운영자가 특정 공연을 삭제합니다.")
    @DeleteMapping("/admin/{festivalId}")
    public ResponseEntity<Map<String, Object>> adminDeleteFestival(@PathVariable Long festivalId) {
        festivalService.adminDeleteFestival(festivalId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "🗑️ 공연 삭제 성공 (관리자)"
        ));
    }
}
