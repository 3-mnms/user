package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.config.security.userdetails.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.request.FindLoginIdDTO;
import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.request.UserProfileDTO;
import com.tekcit.festival.domain.user.dto.response.AddressDTO;
import com.tekcit.festival.domain.user.dto.response.BookingProfileDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.domain.user.entity.*;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.enums.VerificationType;
import com.tekcit.festival.domain.user.repository.AddressRepository;
import com.tekcit.festival.domain.user.repository.EmailVerificationRepository;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.utils.CookieUtil;
import com.tekcit.festival.utils.ResidentUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final CookieUtil cookieUtil;

    @Transactional
    public UserResponseDTO signupUser(@Valid SignupUserDTO signupUserDTO){
        validateDuplicate(signupUserDTO);

        EmailVerification emailVerification =
                emailVerificationRepository.findByEmailAndType(signupUserDTO.getEmail(), VerificationType.SIGNUP)
                        .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if(!emailVerification.getIsVerified()){
            throw new BusinessException(ErrorCode.USER_EMAIL_NOT_VERIFIED);
        }

        User user = signupUserDTO.toUserEntity();
        user.setLoginPw(passwordEncoder.encode(user.getLoginPw()));
        user.setIsEmailVerified(true);

        UserProfileDTO userProfileDTO = signupUserDTO.getUserProfile();

        String rNum = userProfileDTO.getResidentNum();
        UserProfile userProfile = userProfileDTO.toEntity(ResidentUtil.calcAge(rNum), ResidentUtil.extractGender(rNum), ResidentUtil.calcBirth(rNum));

        Address address = userProfileDTO.toAddressEntity(userProfile);
        userProfile.getAddresses().add(address);

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO signupHost(SignupUserDTO signupUserDTO){
        validateDuplicate(signupUserDTO);
        User user = signupUserDTO.toHostEntity();
        user.setLoginPw(passwordEncoder.encode(user.getLoginPw()));

        HostProfile hostProfile = signupUserDTO.getHostProfile().toEntity();

        hostProfile.setUser(user);
        user.setHostProfile(hostProfile);

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO signupAdmin(SignupUserDTO signupUserDTO){
        validateDuplicate(signupUserDTO);
        User user = signupUserDTO.toAdminEntity();
        user.setLoginPw(passwordEncoder.encode(user.getLoginPw()));
        user.setOauthProvider(OAuthProvider.LOCAL);

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public void changeState(Long userId, boolean active, Authentication authentication){
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        User adminUser = currentUser.getUser();

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

    public BookingProfileDTO bookingProfile(Long userId) {
        User bookingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Address> addresses = addressRepository.findAllByUserProfile(profile);

        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address->AddressDTO.fromEntity(address))
                .toList();

        return BookingProfileDTO.fromEntity(bookingUser, profile, addressDTOS);
    }

    @Transactional
    public void deleteUser(Long userId){
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (deleteUser.getRole() != UserRole.USER) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        userRepository.delete(deleteUser);
    }

    public String findLoginId(FindLoginIdDTO findLoginIdDTO){
        String name  = findLoginIdDTO.getName();
        String email = findLoginIdDTO.getEmail();

        User findUser = userRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return findUser.getLoginId();
    }

    public boolean checkLoginId(String loginId){
        return !userRepository.existsByLoginId(loginId);
    }

    public boolean checkEmail(String email){
        return !userRepository.existsByEmail(email);
    }

    public void validateDuplicate(SignupUserDTO signupUserDTO){
        if(userRepository.existsByLoginId(signupUserDTO.getLoginId()))
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID, signupUserDTO.getLoginId());
        if(userRepository.existsByEmail(signupUserDTO.getEmail()))
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_ID, signupUserDTO.getEmail());
    }
}