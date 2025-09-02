package com.tekcit.festival.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// --- HeaderAuthenticationFilter를 SecurityConfig 내부에 정의하여 관리합니다. ---
@Component
class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        final String userIdHeader = trimToNull(request.getHeader("X-User-Id"));
        final String rolesHdr     = trimToNull(request.getHeader("X-User-Role"));
        final String userNameHeader = trimToNull(request.getHeader("X-User-Name"));
        String userName = new String(
                java.util.Base64.getUrlDecoder().decode(userNameHeader),
                java.nio.charset.StandardCharsets.UTF_8
        );

        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = (current instanceof AnonymousAuthenticationToken);
        boolean canSetAuth = (current == null) || isAnonymous;

        if (canSetAuth && userIdHeader != null && rolesHdr != null) {
            final Long userId;
            try {
                userId = Long.valueOf(userIdHeader);
            } catch (NumberFormatException e) {
                logger.warn("[HeaderAuth] invalid X-User-Id: " + userIdHeader);
                chain.doFilter(request, response);
                return;
            }

            List<GrantedAuthority> authorities = Arrays.stream(rolesHdr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            logger.info("[HeaderAuth] userId=" + userId + ", rolesHeader=" + rolesHdr
                    + " -> authorities=" + authorities);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, authorities);
            auth.setDetails(new AuthDetails(request, userName));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // principal을 **Long**으로 세팅
            /*UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
             */
        }

        chain.doFilter(request, response);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return "OPTIONS".equals(method) || path.startsWith("/actuator");
    }
}