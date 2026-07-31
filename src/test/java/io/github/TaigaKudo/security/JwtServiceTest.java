package io.github.TaigaKudo.security;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
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
    void JWTを生成して同じユーザーIDを取得できる() {
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
}