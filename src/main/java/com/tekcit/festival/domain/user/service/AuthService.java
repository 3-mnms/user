package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.config.security.CustomUserDetails;
import com.tekcit.festival.domain.user.dto.request.LoginRequestDTO;
import com.tekcit.festival.domain.user.dto.response.LoginResponseDTO;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.UserEventType;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.config.security.token.JwtTokenProvider;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.kafka.UserEventDTO;
import com.tekcit.festival.kafka.UserEventProducer;
import com.tekcit.festival.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
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
import com.tekcit.festival.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final UserEventProducer userEventProducer;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getLoginId(), loginRequestDTO.getLoginPw()));
        }catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        } catch (UsernameNotFoundException e) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        checkState(user);

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie cookie = cookieUtil.createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        UserEventDTO event = new UserEventDTO(
                user.getUserId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                UserEventType.LOGGED_IN,
                accessToken
        );

        userEventProducer.send(event);

        return LoginResponseDTO.fromUserAndToken(user, accessToken);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        //refreshToken cookie에서 가져옴
        String refreshToken = cookieUtil.resolveRefreshToken(request);

        // refreshToken이 아예 없는 경우 처리
        if (refreshToken == null)
            return;

        //cookie에서 refreshToken삭제
        ResponseCookie cookie = cookieUtil.deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String loginId = jwtTokenProvider.getLoginId(refreshToken);

        if (loginId == null)
            return;

        userRepository.findByLoginId(loginId).ifPresent(user -> {
            user.updateRefreshToken(null);
            userRepository.save(user);
        });
    }

    @Transactional
    public LoginResponseDTO reissue(HttpServletRequest request, HttpServletResponse response) {
        //refreshToken cookie에서 가져옴
        String refreshToken = cookieUtil.resolveRefreshToken(request);

        //refreshToken 유효한 토큰인지 확인
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
        }

        //refreshToken에서 loginId 정보 get
        String loginId = jwtTokenProvider.getLoginId(refreshToken);

        //loginId로 user조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkState(user);

        //cookie에서 가져온 refreshToken이 조회된 user의 refreshToken과 다르거나 null이면 error
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_NOT_MATCH);
        }

        //새로운 accessToken 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        return LoginResponseDTO.fromUserAndToken(user, newAccessToken);
    }

    public void checkState(User user){
        if (user.getRole().equals(UserRole.USER) && !user.getUserProfile().isActive()) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }

        if (user.getRole().equals(UserRole.HOST) && !user.getHostProfile().isActive()) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }

    }

}
