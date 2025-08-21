package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.request.AddressRequestDTO;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.repository.AddressRepository;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {
    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public AddressDTO addAddress(AddressRequestDTO addressRequestDTO, Long userId){
        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Address address = addressRequestDTO.toAddressEntity(userProfile);
        userProfile.getAddresses().add(address);

        userProfileRepository.save(userProfile);
        return AddressDTO.fromEntity(address);
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO, Long userId){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!address.getUserProfile().getUId().equals(userProfile.getUId())) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_ALLOWED);
        }

        address.setAddress(addressRequestDTO.getAddress());
        address.setZipCode(addressRequestDTO.getZipCode());

        addressRepository.save(address);
        return AddressDTO.fromEntity(address);
    }

    @Transactional
    public AddressDTO updateDefault(Long addressId, Long userId){
        Address newAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!newAddress.getUserProfile().getUId().equals(userProfile.getUId())) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_ALLOWED);
        }

        addressRepository.findDefaultByUserId(userId)
                .ifPresent(Address::unsetDefault);

        newAddress.setAsDefault();

        return AddressDTO.fromEntity(newAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!address.getUserProfile().getUId().equals(userProfile.getUId())) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_ALLOWED);
        }

        if(address.isDefault())
            throw new BusinessException(ErrorCode.ADDRESS_DEFAULT_NOT_DELETED);

        addressRepository.delete(address);
    }

    public List<AddressDTO> getAddresses(Long userId){
        List<Address> addresses = addressRepository.findAllByUserId(userId);

        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address->AddressDTO.fromEntity(address))
                .toList();

        return addressDTOS;
    }
}
