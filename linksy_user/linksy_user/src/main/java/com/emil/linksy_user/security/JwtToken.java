package com.emil.linksy_user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtToken {
    @Value("${jwt.code.access}") private String jwtSecretAccess;
    @Value("${jwt.code.refresh}") private String jwtSecretRefresh;
    private final long ACCESS_EXPIRATION_TIME = 1000 * 60 * 15;
    private final long REFRESH_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 21;
    private final long REFRESH_RENEWAL_THRESHOLD = 1000L * 60 * 60 * 24;

    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, jwtSecretAccess)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, jwtSecretRefresh)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretAccess).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecretAccess).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean needsRefreshRenewal(String refreshToken) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecretAccess).build().parseClaimsJws(refreshToken).getBody();
        Date expirationDate = claims.getExpiration();
        long timeRemaining = expirationDate.getTime() - System.currentTimeMillis();
        return timeRemaining <= REFRESH_RENEWAL_THRESHOLD;
    }
}
