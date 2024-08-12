package study.carrotmarketbackend_v1.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    public String callBack(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("naver")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            return "Refresh token not found";
        }

        response.setHeader("Authorization", token);

        Cookie naverCookie = new Cookie("naver", null);
        naverCookie.setMaxAge(0);
        naverCookie.setPath("/");
        response.addCookie(naverCookie);

        return "Access token set and naver cookie removed";
    }
}
