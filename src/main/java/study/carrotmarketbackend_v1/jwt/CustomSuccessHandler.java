package study.carrotmarketbackend_v1.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import study.carrotmarketbackend_v1.document.MongoRefreshToken;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;
import study.carrotmarketbackend_v1.dto.CustomUser;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;

    public CustomSuccessHandler(JWTUtil jwtUtil, MongoRefreshTokenRepository mongoRefreshTokenRepository) {

        this.jwtUtil = jwtUtil;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomUser customUserDetails = (CustomUser) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String userId = String.valueOf(customUserDetails.getUserId());

        // 토큰 생성
        String access = jwtUtil.createJwt("Authorization", username, role, userId, 10000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, userId,86400000L);

        log.info("Generated NAVER Access Token: {}", access);
        log.info("Generated NAVER Refresh Token: {}", refresh);

        // 기존 리프레시 토큰을 사용자 이름으로 찾아 삭제
        Optional<MongoRefreshToken> existingToken = mongoRefreshTokenRepository.findByMemberId(userId);
        if (existingToken.isPresent()) {
            mongoRefreshTokenRepository.deleteByMemberId(userId);
            log.info("Deleted existing refresh token for user: {}", username);
        }

        // 새로운 리프레시 토큰 저장
        jwtUtil.addRefreshMongo(userId, username, refresh, 86400000L);
        log.info("Saved new refresh token for user: {}", username);

        response.addCookie(jwtUtil.createCookie("naver", access));
        response.addCookie(jwtUtil.createCookie("refresh", refresh));
        response.sendRedirect("http://localhost:3000/callback");

    }

}
