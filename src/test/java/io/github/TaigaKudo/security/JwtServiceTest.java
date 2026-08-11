package io.github.TaigaKudo.security;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String testKey =
                "0123456789abcdef0123456789abcdef";

        String base64Key = Base64.getEncoder()
                .encodeToString(
                        testKey.getBytes(StandardCharsets.UTF_8)
                );

        JwtProperties properties = new JwtProperties(
                base64Key,
                Duration.ofMinutes(15)
        );

        jwtService = new JwtService(properties);
    }
    @Test
    @DisplayName("JWTを生成して同じユーザーIDを取得できる")
    void generateAndValidate_ReturnsSameUserId() {
        // 準備
        Long userId = 10L;

        // 実行
        String token =
                jwtService.generateAccessToken(userId);

        Long extractedUserId =
                jwtService.validateAndExtractUserId(token);

        // 検証
        assertFalse(token.isBlank());
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("nullまたは空のトークンは例外を投げる")
    void validate_ThrowsOnNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateAndExtractUserId(null));
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateAndExtractUserId(""));
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateAndExtractUserId("   "));
    }

    @Test
    @DisplayName("期限切れトークンは例外を投げる")
    void validate_ThrowsOnExpiredToken() {
        // 同じ鍵を使って期限切れのトークンを作成
        String testKey = "0123456789abcdef0123456789abcdef";
        String base64Key = Base64.getEncoder().encodeToString(testKey.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

        String expired = io.jsonwebtoken.Jwts.builder()
                .subject("10")
                .issuedAt(Date.from(Instant.now().minus(Duration.ofHours(2))))
                .expiration(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .signWith(key, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.validateAndExtractUserId(expired));
    }

    @Test
    @DisplayName("署名が無効なトークンは例外を投げる")
    void validate_ThrowsOnInvalidSignature() {
        // 別の鍵で署名したトークンを作成
        String otherKey = "fedcba9876543210fedcba9876543210";
        String otherBase64 = Base64.getEncoder().encodeToString(otherKey.getBytes(StandardCharsets.UTF_8));
        byte[] otherBytes = Base64.getDecoder().decode(otherBase64);
        javax.crypto.SecretKey wrongKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(otherBytes);

        String token = io.jsonwebtoken.Jwts.builder()
                .subject("10")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
                .signWith(wrongKey, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();

        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.validateAndExtractUserId(token));
    }

    @Test
    @DisplayName("サブジェクトが数値でないトークンは例外を投げる")
    void validate_ThrowsOnNonNumericSubject() {
        // 同じ鍵でサブジェクトが数値でないトークンを作成
        String testKey = "0123456789abcdef0123456789abcdef";
        String base64Key = Base64.getEncoder().encodeToString(testKey.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

        String token = io.jsonwebtoken.Jwts.builder()
                .subject("not-a-number")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
                .signWith(key, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> jwtService.validateAndExtractUserId(token));
        assertTrue(ex.getMessage().contains("JWT subject must be valid user ID"));
    }
}