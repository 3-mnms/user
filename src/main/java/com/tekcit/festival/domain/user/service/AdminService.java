package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.entity.User;
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

    public List<AdminUserListDTO> getAllUser(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        User adminUser = currentUser.getUser();
        // 운영자 권한 확인
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        List<User> users = userRepository.findAllByRole(UserRole.USER);

        List<AdminUserListDTO> userListDTOS = users.stream()
                                                    .map(AdminUserListDTO::fromUserEntity)
                                                    .toList();
        return userListDTOS;
    }

}
