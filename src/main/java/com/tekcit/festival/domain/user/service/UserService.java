package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.request.*;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.entity.*;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.enums.VerificationType;
import com.tekcit.festival.domain.user.repository.*;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.utils.ResidentUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final KakaoOAuthService kakaoOAuthService;

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

        Address address = userProfileDTO.toAddressEntity(user, userProfile);
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

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public void deleteUser(Long userId){
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (deleteUser.getRole() != UserRole.USER) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED);
        }

        if (deleteUser.getOauthProvider() == OAuthProvider.KAKAO) {
            try {
                kakaoOAuthService.unlinkByAdmin(deleteUser.getOauthProviderId());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.KAKAO_UNLINK_FAILED, e.getMessage());
            }
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

    public String findRegisteredEmail(FindPwEmailDTO findPwEmailDTO){
        String loginId = findPwEmailDTO.getLoginId();
        String name  = findPwEmailDTO.getName();

        User findUser = userRepository.findByLoginIdAndName(loginId, name)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return findUser.getEmail();
    }

    @Transactional
    public void resetPasswordEmail(FindPwResetDTO findPwResetDTO){
        User findUser = userRepository.findByLoginId(findPwResetDTO.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!findUser.getEmail().equals(findPwResetDTO.getEmail()))
            throw new BusinessException(ErrorCode.USER_EMAIL_NOT_MATCH);

        EmailVerification emailVerification =
                emailVerificationRepository.findByEmailAndType(findPwResetDTO.getEmail(), VerificationType.PASSWORD_FIND)
                        .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if(!emailVerification.getIsVerified()){
            throw new BusinessException(ErrorCode.USER_EMAIL_NOT_VERIFIED);
        }

        findUser.setLoginPw(passwordEncoder.encode(findPwResetDTO.getLoginPw()));

        emailVerification.setIsVerified(false);
        emailVerificationRepository.save(emailVerification);

        userRepository.save(findUser);
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