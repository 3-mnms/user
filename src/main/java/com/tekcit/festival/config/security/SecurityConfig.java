package com.tekcit.festival.config.security;

import com.tekcit.festival.config.security.userdetails.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                                "/api/users/signupHost",
                                "/api/users/signupAdmin",
                                "/api/users/login",
                                "/api/users/reissue",
                                "/api/users/checkLoginId",
                                "/api/users/checkEmail",
                                "/api/users/token/parse",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/mail/**",
                                "/api/auth/kakao/**",
                                "/api/users/findLoginId",
                                "/api/users/findRegisteredEmail",
                                "/api/users/resetPasswordWithEmail",
                                "/api/users/reservationList",
                                "/api/users/fcm-token" // FCM 토큰 발급 API는 인증 없이 접근 가능하도록 수정
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter gatewayHeaderAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {

                final String userIdHeader = request.getHeader("X-User-Id");
                final String userRoleHeader = request.getHeader("X-User-Role");
                final String userNameHeader = request.getHeader("X-User-Name");

                // 헤더가 없거나, 인증이 이미 설정된 경우 필터를 건너뛰기
                if (userIdHeader == null || userRoleHeader == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // userId가 숫자인지 안전하게 검증하고 파싱합니다.
                try {
                    Long userId = Long.parseLong(userIdHeader);
                    Set<GrantedAuthority> authorities = Arrays.stream(userRoleHeader.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(String::toUpperCase)
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

                    // Principal에 Long 타입 대신 String으로 userId를 저장
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            String.valueOf(userId), null, authorities);

                    // user-id를 principal에 담아 컨트롤러에서 사용할 수 있도록 설정
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (NumberFormatException e) {
                    // userId가 유효한 숫자가 아닐 경우 경고를 기록하고 인증을 설정하지 않고 통과
                    log.warn("[HeaderAuth] 유효하지 않은 X-User-Id 헤더: " + userIdHeader);
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // 프론트 오리진들 추가 (dev/prod 맞춰서)
        c.setAllowedOrigins(List.of(
                "http://localhost:5173",   // React dev
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