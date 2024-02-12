package com.sobolbetbackend.backendprojektbk1.controller.authorisation.tokens;

import com.sobolbetbackend.backendprojektbk1.controller.authorisation.AuthController;
import com.sobolbetbackend.backendprojektbk1.dto.AuthResponseDTO;
import com.sobolbetbackend.backendprojektbk1.dto.OnRefreshResponseDTO;
import com.sobolbetbackend.backendprojektbk1.service.otherServices.RefreshTokenRevocationService;
import com.sobolbetbackend.backendprojektbk1.util.security.JWT.JwtAuthenticationFilter;
import com.sobolbetbackend.backendprojektbk1.util.security.JWT.JwtGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt-refresh-token-logic")
public class JwtRefreshTokenLogicController {
    private static final Logger log = LoggerFactory.getLogger(JwtRefreshTokenLogicController.class);
    private final JwtGenerator jwtGenerator;
    private final RefreshTokenRevocationService refreshTokenRevocationService;
    @Autowired
    public JwtRefreshTokenLogicController(JwtGenerator jwtGenerator,
                                          RefreshTokenRevocationService refreshTokenRevocationService) {
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenRevocationService = refreshTokenRevocationService;
    }

    @PostMapping("/refresh-access-token-endpoint")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        try {
            System.out.println("Access token refreshed");
            Authentication updatedAuthentication = refreshLogic();
            // Генерация нового токена с обновленной аутентификацией
            String refreshedAccessToken = jwtGenerator.generateAccessToken(updatedAuthentication);

            System.out.println(SecurityContextHolder.getContext().getAuthentication());

            return new ResponseEntity<>(new OnRefreshResponseDTO(refreshedAccessToken), HttpStatus.OK);
        } catch (AuthenticationCredentialsNotFoundException e) {
            log.error("Exception occurred during validation", e);
            // Обработка исключения при невалидном токене
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Exception occurred during token refresh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        }
    }

    @PostMapping("/check-refresh-token-endpoint")
    public ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
        try {
            System.out.println("Refresh token checked");
            Authentication updatedAuthentication = refreshLogic();
            String refreshedAccessToken = jwtGenerator.generateAccessToken(updatedAuthentication);
            String refreshedRefreshToken = jwtGenerator.generateRefreshToken(updatedAuthentication);
            return new ResponseEntity<>(new AuthResponseDTO(refreshedAccessToken, refreshedRefreshToken), HttpStatus.OK);
        } catch (
                AuthenticationCredentialsNotFoundException e) {
            log.error("Exception occurred during validation", e);
            // Обработка исключения при невалидном токене
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Exception occurred during token refresh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        }
    }
    @PostMapping("/revoke-refresh-token-endpoint")
    public ResponseEntity<String> revokeRefreshToken(HttpServletRequest request){
        try{
            System.out.println("Refresh token revoked");
            refreshTokenRevocationService.revokeToken(JwtAuthenticationFilter
                    .getJwtRefreshTokenFromRequest(request));
            return ResponseEntity.ok().body("{\"message\": \"Token successfully revoked\"}");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");
        }
    }
    private Authentication refreshLogic(){
        Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) existingAuthentication.getPrincipal();

        Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                existingAuthentication.getCredentials(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);
        return updatedAuthentication;
    }
}
