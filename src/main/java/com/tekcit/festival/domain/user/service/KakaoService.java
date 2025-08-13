package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.config.security.CustomUserDetails;
import com.tekcit.festival.config.security.token.JwtTokenProvider;
import com.tekcit.festival.domain.user.dto.request.KakaoSignupDTO;
import com.tekcit.festival.domain.user.dto.request.LoginRequestDTO;
import com.tekcit.festival.domain.user.dto.request.UserProfileDTO;
import com.tekcit.festival.domain.user.dto.response.KakaoMeResponse;
import com.tekcit.festival.domain.user.dto.response.LoginResponseDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.utils.CookieUtil;
import com.tekcit.festival.utils.ResidentUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {
    private final AuthenticationManager authenticationManager;
    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    public KakaoCallbackResult handleCallback(String code) {
        String accessToken = kakaoOAuthService.exchangeCodeForAccessToken(code);
        KakaoMeResponse me = kakaoOAuthService.fetchMe(accessToken);

        KakaoMeResponse.KakaoAccount account = Optional.ofNullable(me)
                .map(KakaoMeResponse::getKakaoAccount)
                .orElseThrow(()-> new IllegalStateException("카카오 계정 정보가 비었습니다."));

        String email = account.getEmail();
        Boolean needs = account.getEmailNeedsAgreement();
        Boolean valid = account.getIsEmailValid();
        Boolean verified = account.getIsEmailVerified();

        if (email == null || Boolean.TRUE.equals(needs) || !Boolean.TRUE.equals(valid) || !Boolean.TRUE.equals(verified)) {
            throw new IllegalStateException(
                    "카카오 이메일 사용 불가: email=" + email +
                            ", needsAgreement=" + needs +
                            ", isValid=" + valid +
                            ", isVerified=" + verified
            );
        }

        String kakaoId = String.valueOf(me.getId());

        boolean exists = userRepository
                .findByOauthProviderAndOauthProviderId(OAuthProvider.KAKAO, kakaoId)
                .isPresent();

        if (exists)
            return new KakaoCallbackResult(false, null, kakaoId); //기존 가입자

        String signupTicket = jwtTokenProvider.createSignupTicket(kakaoId, email); //신규 가입자
        return new KakaoCallbackResult(true, signupTicket, kakaoId);
    }

    @Transactional
    public UserResponseDTO signupUser(KakaoSignupDTO kakaoSignupDTO, String signupTicket) {
        if (signupTicket == null)
            throw new BusinessException(ErrorCode.KAKAO_INVALID_TICKET, "카카오 가입 토큰이 없습니다. 다시 인증해주세요.");

        JwtTokenProvider.SignupTicketClaims claims = jwtTokenProvider.parseSignupTicket(signupTicket);
        String kakaoId = claims.kakaoId();
        String kakaoEmail = claims.email();

        userRepository.findByOauthProviderAndOauthProviderId(OAuthProvider.KAKAO, kakaoId)
                .ifPresent(u-> {throw new BusinessException(ErrorCode.DUPLICATE_KAKAO_ID, kakaoId);});

        if(userRepository.existsByEmail(kakaoEmail))
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_ID, kakaoEmail);

        User kakaoUser = kakaoSignupDTO.toUserEntity(kakaoId, kakaoEmail);

        UserProfileDTO userProfileDTO = kakaoSignupDTO.getUserProfile();

        String rNum = userProfileDTO.getResidentNum();
        UserProfile userProfile = userProfileDTO.toEntity(ResidentUtil.calcAge(rNum), ResidentUtil.extractGender(rNum), ResidentUtil.calcBirth(rNum));

        Address address = userProfileDTO.toAddressEntity(userProfile);
        userProfile.getAddresses().add(address);

        userProfile.setUser(kakaoUser);
        kakaoUser.setUserProfile(userProfile);

        userRepository.save(kakaoUser);
        return UserResponseDTO.fromEntity(kakaoUser);
    }

    @Transactional
    public void login(String kakaoId, HttpServletResponse response) {
        User user = userRepository.findByOauthProviderAndOauthProviderId(OAuthProvider.KAKAO, kakaoId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole().equals(UserRole.USER) && !user.getUserProfile().isActive()) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }

        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie cookie = cookieUtil.createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public record KakaoCallbackResult(boolean isNew, String signupTicket, String kakaoId) {}

}
