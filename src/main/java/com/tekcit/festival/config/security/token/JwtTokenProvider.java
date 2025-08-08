package com.tekcit.festival.config.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.tekcit.festival.domain.user.entity.User;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.private-pem-path}")
    private org.springframework.core.io.Resource privatePemPath;

    @Value("${jwt.public-pem-path}")
    private org.springframework.core.io.Resource publicPemPath;

    @Value("${jwt.access-valid-ms:1800000}")
    private long accessValidMs;

    @Value("${jwt.refresh-valid-ms:1296000000}")
    private long refreshValidMs;

    @Value("${jwt.issuer:festival-user-service}")
    private String issuer;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            // 파일에서 PEM 내용 읽기
            String privatePem = new String(privatePemPath.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            String publicPem  = new String(publicPemPath.getInputStream().readAllBytes(),  java.nio.charset.StandardCharsets.UTF_8);

            this.privateKey = loadPrivateKeyFromPem(privatePem);
            this.publicKey  = loadPublicKeyFromPem(publicPem);
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
                .setSubject(user.getLoginId())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshValidMs);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getLoginId())
                .setIssuedAt(now)
                .setExpiration(expiration)
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

    public String getLoginId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(30)  // 시계 오차 30초 허용
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // loginId
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
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

