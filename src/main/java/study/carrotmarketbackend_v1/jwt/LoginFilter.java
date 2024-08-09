package study.carrotmarketbackend_v1.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import study.carrotmarketbackend_v1.document.MongoRefreshToken;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;
import study.carrotmarketbackend_v1.dto.ApiResponse;
import study.carrotmarketbackend_v1.dto.LoginJwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, MongoRefreshTokenRepository mongoRefreshTokenRepository, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/members/login"); // 로그인 URL을 설정합니다.
    }

    private static final Map<Class<? extends AuthenticationException>, String> errorMessages = new HashMap<>();

    static {
        errorMessages.put(BadCredentialsException.class, "잘못된 이메일 또는 비밀번호입니다.");
        errorMessages.put(AccountExpiredException.class, "계정이 만료되었습니다.");
        errorMessages.put(DisabledException.class, "계정이 비활성화되었습니다.");
        errorMessages.put(LockedException.class, "계정이 잠겼습니다.");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!"POST".equals(request.getMethod())) {
            return null; // POST 요청이 아니면 처리하지 않음
        }
        LoginJwt.Request loginRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginJwt.Request.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        // 유저 정보
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        GrantedAuthority auth = authorities.iterator().next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        log.info("Generated Access Token: {}", access);
        log.info("Generated Refresh Token: {}", refresh);

        // 기존 리프레시 토큰을 사용자 이름으로 찾아 삭제
        Optional<MongoRefreshToken> existingToken = mongoRefreshTokenRepository.findByUsername(username);
        if (existingToken.isPresent()) {
            mongoRefreshTokenRepository.deleteByUsername(username);
        }

        // 새로운 리프레시 토큰 저장
        jwtUtil.addRefreshMongo(username, refresh, 86400000L);

        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("access", access);
        response.addCookie(jwtUtil.createCookie("refresh", refresh));

        // ApiResponse 객체 생성
        ApiResponse<String> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), // 상태 코드
                "로그인 성공", // 성공 메시지
                access // 응답 본문에 토큰 포함
        );

        // 응답을 JSON으로 변환하여 클라이언트에게 전송
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);

    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 오류 메시지 설정
        String errorMessage = errorMessages.getOrDefault(failed.getClass(), "인증 실패");

        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // ApiResponse 객체 생성
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(), // 상태 코드
                errorMessage, // 오류 메시지
                null // 데이터는 없음
        );

        // 응답을 JSON으로 변환하여 클라이언트에게 전송
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
    }

}
