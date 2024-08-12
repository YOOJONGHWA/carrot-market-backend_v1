package study.carrotmarketbackend_v1.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import study.carrotmarketbackend_v1.dto.ApiResponse;
import study.carrotmarketbackend_v1.dto.CustomUser;
import study.carrotmarketbackend_v1.entity.User;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Token을 헤더와 쿠키에서 추출
        String accessToken = Optional.ofNullable(request.getHeader("Authorization"))
                .map(header -> header.startsWith("Bearer ") ? header.substring(7) : null)
                .orElseGet(() -> getCookieValue(request, "naver"));

        // 2. Token이 없는 경우
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Token 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에 대한 응답
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ApiResponse<String> apiResponse = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "access token expired",
                    null
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
            return;
        }

        // 4. Token 카테고리 확인
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("Authorization")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<String> apiResponse = new ApiResponse<>(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "invalid access token",
                    null
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
            return;
        }

        // 5. Token에서 사용자 정보 추출
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = User.builder()
                .username(username)
                .role(role)
                .build();
        CustomUser customUser = new CustomUser(user);

        // 6. Authentication 객체 생성 및 설정
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 값을 추출하는 헬퍼 메소드
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
