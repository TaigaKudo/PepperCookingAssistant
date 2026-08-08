package io.github.TaigaKudo.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.refresh-token")
public record RefreshTokenProperties(Duration expiration, boolean cookieSecure) {

}
