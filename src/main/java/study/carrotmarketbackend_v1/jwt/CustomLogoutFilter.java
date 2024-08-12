package study.carrotmarketbackend_v1.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import study.carrotmarketbackend_v1.document.MongoRefreshToken;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, MongoRefreshTokenRepository mongoRefreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // Path and method verification
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api/auth/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 리프레시 토큰 확인
        Cookie refreshCookie = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refreshCookie = cookie;
                }
            }
        }

        // 리프레시 쿠키가 있는 경우 처리
        if (refreshCookie != null) {
            String refresh = refreshCookie.getValue();

            // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시된 카테고리 확인)
            String category = jwtUtil.getCategory(refresh);
            if (!category.equals("refresh")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // DB에서 리프레시 토큰 확인 및 삭제
            Optional<MongoRefreshToken> isExist = mongoRefreshTokenRepository.findByToken(refresh);
            if (isExist.isPresent()) {
                mongoRefreshTokenRepository.deleteByToken(refresh);
                log.info("DB에서 리프레시 토큰이 삭제되었습니다.");

                // 리프레시 쿠키 무효화
                Cookie invalidatedRefreshCookie = new Cookie("refresh", null);
                invalidatedRefreshCookie.setMaxAge(0);
                invalidatedRefreshCookie.setPath("/");
                response.addCookie(invalidatedRefreshCookie);
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("리프레시 쿠키가 무효화되었습니다.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

