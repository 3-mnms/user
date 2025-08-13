package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.KakaoSignupDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.domain.user.service.KakaoService;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.exception.global.ErrorResponse;
import com.tekcit.festival.utils.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Tag(name = "카카오 회원가입, 로그인 API", description = "카카오 회원가입, 로그인, 로그아웃, 토큰 재발급")
public class KakaoAuthController {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.authorize-uri}")
    private String authorizeUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.userinfo-uri}")
    private String userInfoUri;

    @Value("${kakao.scope}")
    private String scope;

    @Value("${app.frontend.signup-url}") private String frontendSignupUrl;

    @Value("${app.frontend.login-url}")  private String frontendLoginUrl;

    private final KakaoService kakaoService;
    private final CookieUtil cookieUtil;

    /** 카카오 인가 시작 (force=true면 로그인 화면 강제 노출) */
    @GetMapping("/authorize")
    public void redirectToKakao(@RequestParam(value = "force", defaultValue = "false") boolean forceLogin, HttpServletResponse response) throws IOException {

        UriComponentsBuilder u = UriComponentsBuilder.fromHttpUrl(authorizeUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope);

        if (forceLogin){
            u.queryParam("prompt", "login"); // 무조건 카카오 로그인창
        }
        response.sendRedirect(u.toUriString()); // 브라우저를 카카오로 리다이렉트
    }

    /** 카카오 콜백: 신규면 티켓 쿠키 SET 후 가입 폼으로, 기존이면 쿠키 삭제 후 로그인으로 */
    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException{
        KakaoService.KakaoCallbackResult result = kakaoService.handleCallback(code);
        if (result.isNew()) {
            response.addHeader("Set-Cookie", cookieUtil.createKakaoSignupCookie(result.signupTicket()).toString());
            response.sendRedirect(frontendSignupUrl + "?provider=kakao");
            return;
        }
        else {
            kakaoService.login(result.kakaoId(), response);
            response.addHeader("Set-Cookie", cookieUtil.deleteKakaoSignupCookie().toString());
            response.sendRedirect(frontendLoginUrl);
        }
    }

    @PostMapping(value="/signupUser")
    @Operation(summary = "회원 가입(일반 유저), 카카오 회원가입",
            description = "일반 유저 회원 가입, SignupUserDTO를 포함해야 합니다. ex) POST /api/auth/kakao/signupUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공(일반 유저)",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (잘못된 데이터, 필수 필드 누락)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원 가입 실패 (중복된 ID, Email로 인한 conflict)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDTO> signupUser(@Valid @RequestBody KakaoSignupDTO kakaoSignupDTO,
                                                      @CookieValue(value = "kakao_signup", required = false) String ticket,
                                                      HttpServletResponse res) {
        try {
            UserResponseDTO signupUser = kakaoService.signupUser(kakaoSignupDTO, ticket);
            res.addHeader("Set-Cookie", cookieUtil.deleteKakaoSignupCookie().toString());
            return ResponseEntity.ok(signupUser);

        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.KAKAO_INVALID_TICKET) {
                res.addHeader("Set-Cookie", cookieUtil.deleteKakaoSignupCookie().toString());
            }
            throw ex;
        }
    }
}

