package study.carrotmarketbackend_v1.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest
public class LoginFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MongoRefreshTokenRepository mongoRefreshTokenRepository;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private LoginFilter loginFilter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginFilter).build();
    }

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        // Mock authentication manager
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);

        // Mock JWT utility
        when(jwtUtil.createJwt(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(accessToken);
        when(jwtUtil.createCookie(anyString(), anyString())).thenReturn(null);

        // Mock request body
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.body").value(accessToken));

        // Verify interactions
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtUtil).createJwt(anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testUnsuccessfulAuthentication() throws Exception {
        String email = "test@example.com";
        String password = "password";

        // Mock authentication manager to throw exception
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Mock request body
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value("잘못된 이메일 또는 비밀번호입니다."));
    }
}
