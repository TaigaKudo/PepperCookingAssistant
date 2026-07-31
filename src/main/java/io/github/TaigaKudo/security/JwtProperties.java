package io.github.TaigaKudo.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secretKey, Duration accessTokenExpiration) {

}
