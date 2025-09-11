package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.api.AddressApiSpecification;
import com.tekcit.festival.domain.user.dto.request.AddressRequestDTO;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.service.AddressService;
import com.tekcit.festival.exception.global.SuccessResponse;
import com.tekcit.festival.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "주소 API", description = "주소 조회, 추가, 수정, 삭제, 기본 배송지 수정")
public class AddressController implements AddressApiSpecification {
    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SuccessResponse<AddressDTO>> addAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        AddressDTO addressDTO = addressService.addAddress(addressRequestDTO, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @PatchMapping(value="/updateAddress/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SuccessResponse<AddressDTO>> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        AddressDTO addressDTO = addressService.updateAddress(addressId, addressRequestDTO, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @PatchMapping(value="/changeDefault/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SuccessResponse<AddressDTO>> updateDefault(@PathVariable Long addressId, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        AddressDTO addressDTO = addressService.updateDefault(addressId, userId);
        return ApiResponseUtil.success(addressDTO);
    }

    @DeleteMapping(value="/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value="/allAddress")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SuccessResponse<List<AddressDTO>>> getAllAddresses(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        List<AddressDTO> addressDTOS = addressService.getAllAddresses(userId);
        return ApiResponseUtil.success(addressDTOS);
    }

    @GetMapping(value="/defaultAddress")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SuccessResponse<AddressDTO>> getDefaultAddress(@AuthenticationPrincipal String principal){
        Long userId = Long.parseLong(principal);
        AddressDTO addressDTOS = addressService.getDefaultAddress(userId);
        return ApiResponseUtil.success(addressDTOS);
    }

    @GetMapping(value="/{addressId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SuccessResponse<AddressDTO>> getAddress(@AuthenticationPrincipal String principal, @PathVariable Long addressId){
        Long userId = Long.parseLong(principal);
        AddressDTO addressDTO = addressService.getAddress(userId, addressId);
        return ApiResponseUtil.success(addressDTO);
    }

}
