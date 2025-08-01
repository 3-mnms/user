package com.tekcit.festival.config.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.tekcit.festival.domain.user.entity.User;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_VALIDITY = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_VALIDITY = 15 * 24 * 60 * 60 * 1000L; // 15일

    // 액세스 토큰 생성
    public String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getLoginId())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getLoginId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // base64 인코딩된 key
                    .build()
                    .parseClaimsJws(token); // 여기서 서명 & 만료 검증됨
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims is empty.");
        }
        return false;
    }

    public String getLoginId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getLoginIdIfExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}

