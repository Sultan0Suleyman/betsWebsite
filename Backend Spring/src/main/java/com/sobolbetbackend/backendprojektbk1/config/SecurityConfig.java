package com.sobolbetbackend.backendprojektbk1.config;

import com.sobolbetbackend.backendprojektbk1.service.authenticationServices.UserEService;
import com.sobolbetbackend.backendprojektbk1.util.security.JWT.JwtAuthEntryPoint;
import com.sobolbetbackend.backendprojektbk1.util.security.JWT.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthEntryPoint authEntryPoint;
    private final UserEService userEService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, UserEService userEService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authEntryPoint = authEntryPoint;
        this.userEService = userEService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception->exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST,"/players").permitAll()
                        .requestMatchers(HttpMethod.POST,"/login-endpoint").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/jwt-refresh-token-logic/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/csrf/token").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/list/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/linemaker/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/linemaker/create-match").permitAll()
                        .requestMatchers(HttpMethod.POST,"/linemaker/set-odds").permitAll()
                        .requestMatchers(HttpMethod.POST,"linemaker/publish-match/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"linemaker/unpublish-match/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH,"/linemaker/match-status").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/linemaker/delete-match/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/player/data/**").permitAll()
                        .requestMatchers("/payment/**").permitAll()
                        .requestMatchers("/bet/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS","PUT","PATCH","DELETE"));
        configuration.addAllowedHeader("*"); // Разрешить все заголовки
        configuration.setExposedHeaders(List.of("*")); // Добавьте эту строку
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/players", configuration);
        source.registerCorsConfiguration("/login-endpoint", configuration);
        source.registerCorsConfiguration("/jwt-refresh-token-logic/**", configuration);
        source.registerCorsConfiguration("/csrf/token", configuration);
        source.registerCorsConfiguration("/list/**", configuration);
        source.registerCorsConfiguration("/player/data/**", configuration);
        source.registerCorsConfiguration("/payment/**", configuration);
        source.registerCorsConfiguration("/bet/**", configuration);
        source.registerCorsConfiguration("/admin/**",configuration);
        source.registerCorsConfiguration("/linemaker/**", configuration);
        return source;
    }
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
