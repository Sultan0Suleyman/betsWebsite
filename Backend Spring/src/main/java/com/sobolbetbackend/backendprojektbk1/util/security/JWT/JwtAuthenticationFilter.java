package com.sobolbetbackend.backendprojektbk1.util.security.JWT;

import com.sobolbetbackend.backendprojektbk1.service.otherServices.RefreshTokenRevocationService;
import com.sobolbetbackend.backendprojektbk1.service.authenticationServices.UserEService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final UserEService userEService;
    private final JwtGenerator jwtGenerator;
    private final RefreshTokenRevocationService refreshTokenRevocationService;
    @Autowired
    public JwtAuthenticationFilter(UserEService userEService, JwtGenerator jwtGenerator,
                                   RefreshTokenRevocationService refreshTokenRevocationService) {
        this.userEService = userEService;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenRevocationService = refreshTokenRevocationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, AuthenticationCredentialsNotFoundException {
        String token = getJwtAccessTokenFromRequest(request);
        if(StringUtils.hasText(token) && jwtGenerator.validateAccessToken(token)){
            filterLogic(request,token);
        }else{
            String refreshToken = getJwtRefreshTokenFromRequest(request);
            if (refreshTokenRevocationService.isTokenRevoked(refreshToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (StringUtils.hasText(refreshToken) && jwtGenerator.validateRefreshToken(refreshToken)) {
                filterLogic(request,refreshToken);
                refreshTokenRevocationService.revokeToken(refreshToken);
            }
        }

        filterChain.doFilter(request,response);
    }
    private void filterLogic(HttpServletRequest request, String token){
        String username = jwtGenerator.getUsernameFromJwt(token);

        UserDetails userDetails = userEService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    private String getJwtAccessTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
    public static String getJwtRefreshTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("RefreshToken");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
