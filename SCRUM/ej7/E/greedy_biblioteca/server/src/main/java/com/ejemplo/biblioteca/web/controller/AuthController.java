package com.ejemplo.biblioteca.web.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);
    private static final String ISSUER = "http://localhost:8080";
    private static final String DEFAULT_SCOPE = "books.read books.write loans.read loans.write";

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Instant now = Instant.now();
        Instant expiresAt = now.plus(ACCESS_TOKEN_TTL);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("scope", DEFAULT_SCOPE)
                .build();

        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        TokenResponse response = new TokenResponse(
                tokenValue,
                "Bearer",
                ACCESS_TOKEN_TTL.getSeconds(),
                DEFAULT_SCOPE
        );

        return ResponseEntity.ok(response.toMap());
    }

    public record LoginRequest(String username, String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn, String scope) {
        public Map<String, Object> toMap() {
            return Map.of(
                    "access_token", accessToken,
                    "token_type", tokenType,
                    "expires_in", expiresIn,
                    "scope", scope
            );
        }
    }
}
