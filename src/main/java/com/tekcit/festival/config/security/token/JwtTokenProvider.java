package com.tekcit.festival.config.security.token;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Serializer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.tekcit.festival.domain.user.entity.User;
import io.jsonwebtoken.jackson.io.JacksonSerializer; // jjwt-jackson

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.private-pem-path}")
    private org.springframework.core.io.Resource privatePemPath;

    @Value("${jwt.public-pem-path}")
    private org.springframework.core.io.Resource publicPemPath;

    @Value("${jwt.access-valid-ms}")
    private long accessValidMs;

    @Value("${jwt.refresh-valid-ms}")
    private long refreshValidMs;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${signup.ticket.valid-ms}") // 기본 10분
    private long signupTicketValidMs;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Serializer<Map<String, ?>> jsonSerializer;

    @PostConstruct
    public void init() {
        try {
            // 파일에서 PEM 내용 읽기
            String privatePem = new String(privatePemPath.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            String publicPem  = new String(publicPemPath.getInputStream().readAllBytes(),  java.nio.charset.StandardCharsets.UTF_8);

            this.privateKey = loadPrivateKeyFromPem(privatePem);
            this.publicKey  = loadPublicKeyFromPem(publicPem);

            ObjectMapper om = new ObjectMapper();
            om.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            this.jsonSerializer = new JacksonSerializer<>(om);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read PEM files", e);
        }
    }

    // 액세스 토큰 생성
    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime()+accessValidMs);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(user.getUserId()))
                .claim("role", user.getRole().name())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .serializeToJsonWith(jsonSerializer) // ★ 여기!
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshValidMs);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .serializeToJsonWith(jsonSerializer) // ★ 여기!
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String createSignupTicket(String kakaoId, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + signupTicketValidMs);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject("kakao-signup")
                .claim("kakaoId", kakaoId)                    // kakaoId
                .claim("email", email)                    // email
                .setIssuedAt(now)
                .setExpiration(exp)
                .serializeToJsonWith(jsonSerializer)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(30)  // 시계 오차 30초 허용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token.");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token.");
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims is empty.");
        }
        return false;
    }

    public Long getUserId(String token) {
        try {
            String sub = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(30)  // 시계 오차 30초 허용
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // userId=> String
            return Long.parseLong(sub);
        } catch (ExpiredJwtException e) {
            return Long.parseLong(e.getClaims().getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .setAllowedClockSkewSeconds(30)  // 시계 오차 30초 허용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public record SignupTicketClaims(String kakaoId, String email) {}

    public SignupTicketClaims parseSignupTicket(String token) {
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(30)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            if (!"kakao-signup".equals(c.getSubject())) {
                throw new BusinessException(ErrorCode.KAKAO_INVALID_TICKET, "잘못된 가입 티켓(subject mismatch) 입니다.");
            }

            String kid = c.get("kakaoId", String.class);
            String email = c.get("email", String.class);

            if (kid == null || kid.isBlank() || email == null || email.isBlank()) {
                throw new BusinessException(ErrorCode.KAKAO_INVALID_TICKET, "카카오 id 또는 카카오 이메일이 올바르지 못합니다.");
            }
            return new SignupTicketClaims(kid, email);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.KAKAO_INVALID_TICKET, "가입 티켓이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) { // 서명 오류, 위조, 포맷 오류 등
            throw new BusinessException(ErrorCode.KAKAO_INVALID_TICKET, "가입 티켓이 유효하지 않습니다.");
        }
    }

    // ===== PEM 로더들
    private static PrivateKey loadPrivateKeyFromPem(String pem) {
        try {
            String content = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    private static PublicKey loadPublicKeyFromPem(String pem) {
        try {
            String content = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(content);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }
}

