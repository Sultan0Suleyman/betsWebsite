package com.sobolbetbackend.backendprojektbk1.util.security.JWT;

import com.sobolbetbackend.backendprojektbk1.util.security.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtGenerator {
    public String generateAccessToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_ACCESS_TOKEN_EXPIRATION);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(SecurityConstants.getCurrentSecretKey())
                .compact();
    }
    public String generateRefreshToken(Authentication authentication){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_REFRESH_TOKEN_EXPIRATION);
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(SecurityConstants.getCurrentSecretKey())
                .compact();
    }
    public String getUsernameFromJwt(String token){
        Claims claims = Jwts.parser()
                .verifyWith(SecurityConstants.getCurrentSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
    public boolean validateAccessToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(SecurityConstants.getCurrentSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch(Exception e){
            return false;
        }
    }
    public boolean validateRefreshToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(SecurityConstants.getCurrentSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch(Exception e){
            throw new AuthenticationCredentialsNotFoundException("JWT_REFRESH was expired or incorrect");
        }
    }

}
