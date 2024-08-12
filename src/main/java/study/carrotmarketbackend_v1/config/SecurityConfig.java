package study.carrotmarketbackend_v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;
import study.carrotmarketbackend_v1.jwt.*;
import study.carrotmarketbackend_v1.repository.UserRepository;
import study.carrotmarketbackend_v1.service.CustomOAuth2UserService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    @Autowired
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, MongoRefreshTokenRepository mongoRefreshTokenRepository, CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
            configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
            return configuration;
        }));

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 로그인 폼 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        // URL 접근 제어 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/",
                        "/api/auth/login",
                        "/api/auth/signup",
                        "/api/auth/reissue",
                        "/api/oauth/callback").permitAll()
//                .requestMatchers("/oauth2/authorization/naver").denyAll() // 이 경로에 대한 접근 차단
                .requestMatchers("/api/auth/me",
                        "api/auth/profile",
                        "api/auth/update",
                        "api/auth/change-password").hasRole("USER")
                .anyRequest().authenticated()
        );

        // 필터 설정
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), mongoRefreshTokenRepository ,jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, mongoRefreshTokenRepository), LogoutFilter.class);


        // 세션 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}
