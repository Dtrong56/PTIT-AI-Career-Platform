
package com.ptit.identity_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

        private final JwtEncoder jwtEncoder;
        private final long expirationSeconds;

        public JwtService(
                        JwtEncoder jwtEncoder,
                        @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {

                this.jwtEncoder = jwtEncoder;
                this.expirationSeconds = expirationSeconds;
        }

        public String generateToken(
                        String username,
                        String role,
                        boolean mustChangePassword,
                        String studentId) {

                Instant now = Instant.now();

                JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                                .issuer("ptit-identity-service")
                                .issuedAt(now)
                                .expiresAt(now.plusSeconds(expirationSeconds))
                                .subject(username)
                                .claim("role", role)
                                .claim("mustChangePassword", mustChangePassword);

                // Chỉ thêm studentId đối với tài khoản sinh viên
                if ("STUDENT".equals(role)
                                && studentId != null
                                && !studentId.isBlank()) {

                        claimsBuilder.claim("studentId", studentId);
                }

                JwtClaimsSet claims = claimsBuilder.build();

                JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                                .build();

                JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

                return jwtEncoder.encode(parameters).getTokenValue();
        }
}
