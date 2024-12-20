package com.emil.linksy_cloud.security;

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
    @Value("${jwt.expiration.access}") private long accessExpirationTime;
    @Value("${jwt.expiration.refresh}") private long refreshExpirationTime;
    @Value("${jwt.threshold.refresh-renewal}") private long refreshRenewalThreshold;

    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecretAccess)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecretRefresh)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretAccess).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretRefresh).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Long extractUserId(String token,TokenType tokenType) {
        String signingKey = "";
        if (tokenType == TokenType.ACCESS){
            signingKey = jwtSecretAccess;
        }else if (tokenType == TokenType.REFRESH){
            signingKey = jwtSecretRefresh;
        }
        Claims claims = Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    public boolean needsRefreshRenewal(String refreshToken) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecretRefresh).build().parseClaimsJws(refreshToken).getBody();
        Date expirationDate = claims.getExpiration();
        long timeRemaining = expirationDate.getTime() - System.currentTimeMillis();
        return timeRemaining <= refreshRenewalThreshold;
    }
}
