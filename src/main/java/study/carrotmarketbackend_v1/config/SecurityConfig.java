package study.carrotmarketbackend_v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;
import study.carrotmarketbackend_v1.jwt.CustomLogoutFilter;
import study.carrotmarketbackend_v1.jwt.JWTFilter;
import study.carrotmarketbackend_v1.jwt.JWTUtil;
import study.carrotmarketbackend_v1.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, MongoRefreshTokenRepository mongoRefreshTokenRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
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

//        http
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setMaxAge(3600L);
//
//                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//                        configuration.setExposedHeaders(Collections.singletonList("access"));
//
//                        return configuration;
//                    }
//                }));

        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //http basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers( "/"
                                ,"/api/members/login"
                                ,"/api/members/signup"
                                ,"/api/members/reissue").permitAll()
                        .requestMatchers("/api/members/me").hasRole("USER")
                        .anyRequest().authenticated());

        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),mongoRefreshTokenRepository, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil,mongoRefreshTokenRepository), LogoutFilter.class);


        //세션 설정
        http.sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}