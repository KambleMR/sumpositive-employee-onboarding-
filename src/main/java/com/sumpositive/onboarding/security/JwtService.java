
package com.sumpositive.onboarding.security;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.sumpositive.onboarding.enums.Role;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMs;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }

    // Generate JWT using email and actual user role
    public String generateToken(String email, Role role) {

        Instant now = Instant.now();

        Instant expiry = now.plusMillis(expirationMs);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("sumpositive-onboarding")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(email)
                .claim("roles", List.of("ROLE_" + role.name()))
                .build();

        JwsHeader header = JwsHeader.with(
                MacAlgorithm.HS256
        ).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
