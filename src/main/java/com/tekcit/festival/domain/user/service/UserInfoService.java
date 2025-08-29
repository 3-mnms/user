package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public CheckAgeDTO checkUserAgeInfo(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return CheckAgeDTO.fromEntity(userProfile);
    }

    public BookingProfileDTO bookingProfileInfo(Long userId) {
        User bookingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return BookingProfileDTO.fromEntity(bookingUser);
    }

    public List<ReservationUserDTO> getReservationUserInfo(List<Long> userIds){
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(ReservationUserDTO::fromUserEntity)
                .collect(Collectors.toList());
    }

    public List<StatisticsDTO> getStatisticsInfo(List<Long> userIds){
        List<UserProfile> userProfiles = userProfileRepository.findAllByUserIds(userIds);

        return userProfiles.stream()
                .map(StatisticsDTO::fromUserProfileEntity)
                .collect(Collectors.toList());
    }

    public PreReservationDTO getPreReservationInfo(Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return PreReservationDTO.fromUserEntity(findUser);
    }

    public AssignmentDTO transfereeInfo(Long userId, String email){
        User transferor = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(transferor.getEmail().equals(email))
            throw new BusinessException(ErrorCode.SELF_TRANSFER_FORBIDDEN);

        User transferee = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AssignmentDTO.fromUserEntity(transferee);
    }

    public AssignmentDTO transferorInfo(Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AssignmentDTO.fromUserEntity(findUser);
    }

}
