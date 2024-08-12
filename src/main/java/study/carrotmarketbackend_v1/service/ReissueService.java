package study.carrotmarketbackend_v1.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.document.MongoRefreshToken;
import study.carrotmarketbackend_v1.document.MongoRefreshTokenRepository;
import study.carrotmarketbackend_v1.jwt.JWTUtil;

import java.util.Optional;

@Service
public class ReissueService {

    private final MongoRefreshTokenRepository mongoRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public ReissueService(MongoRefreshTokenRepository mongoRepository, JWTUtil jwtUtil) {
        this.mongoRepository = mongoRepository;
        this.jwtUtil = jwtUtil;
    }


    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Optional<MongoRefreshToken> mongoRefreshToke = mongoRepository.findByToken(refresh);
        if (mongoRefreshToke.isEmpty()) {

            //response body
            new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
            return;
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String userId = jwtUtil.getUserId(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("Authorization", username, role, userId, 10000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, userId, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        mongoRepository.deleteByToken(refresh);
        jwtUtil.addRefreshMongo(userId, username, newRefresh, 86400000L);

        //response
        response.setHeader("Authorization", newAccess);
        response.addCookie(jwtUtil.createCookie("refresh", newRefresh));

        new ResponseEntity<>(HttpStatus.OK);
    }

}
