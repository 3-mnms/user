package com.tekcit.festival.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final HeaderAuthenticationFilter headerAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/signupUser",
                                "/api/users/signupAdmin",
                                "/api/users/findLoginId",
                                "/api/users/findRegisteredEmail",
                                "/api/users/reservationList",
                                //"/api/users/fcm-token",
                                "/api/users/resetPasswordEmail",
                                "/api/users/login",
                                "/api/auth/kakao/signupUser"
                                ).anonymous()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/users/checkLoginId",
                                "/api/users/checkEmail",
                                "/api/users/reissue",
                                "/api/mail/**",
                                "/api/auth/kakao/**",
                                "/api/users/booking-profile/**",
                                "/api/users/statisticsList"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // 프론트 오리진들 추가 (dev/prod 맞춰서)
        c.setAllowedOrigins(List.of(
                "http://localhost:5173",// React dev
                "http://localhost:5174",
                "http://localhost:8080"    // (필요시) 같은 포트에서 테스트용
        ));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true); // ★ 쿠키 주고받기 허용
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}