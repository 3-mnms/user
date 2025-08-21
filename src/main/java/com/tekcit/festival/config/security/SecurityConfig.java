package com.tekcit.festival.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/mail/**",
                                "/api/auth/kakao/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 게이트웨이에서 전달받은 헤더를 처리하는 필터 추가
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
}