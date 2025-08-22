package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;

    public List<AdminUserListDTO> getAllUser(User adminUser) {
        // 운영자 권한 확인
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        List<User> users = userRepository.findAllUserByRole(UserRole.USER);
        List<AdminUserListDTO> userListDTOS = users.stream()
                                                    .map(AdminUserListDTO::fromUserEntity)
                                                    .toList();
        return userListDTOS;
    }

    public List<AdminHostListDTO> getAllHost(User adminUser) {
        // 운영자 권한 확인
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        List<User> users = userRepository.findAllHostByRole(UserRole.HOST);

        List<AdminHostListDTO> hostListDTOS = users.stream()
                .map(AdminHostListDTO::fromUserEntity)
                .toList();
        return hostListDTOS;
    }

    @Transactional
    public void changeState(Long userId, boolean active, User adminUser){
        // 운영자 권한 확인
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        User changeUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (changeUser.getRole() == UserRole.USER) {
            if(active)
                changeUser.getUserProfile().activate();
            else
                changeUser.getUserProfile().deactivate();
        }
        else if(changeUser.getRole() == UserRole.HOST){
            if(active)
                changeUser.getHostProfile().activate();
            else
                changeUser.getHostProfile().deactivate();
        }
        else
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
    }

    @Transactional
    public void deleteHost(User adminUser, Long userId){
        // 운영자 권한 확인
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (deleteUser.getRole() != UserRole.HOST) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        userRepository.delete(deleteUser);
    }

}
