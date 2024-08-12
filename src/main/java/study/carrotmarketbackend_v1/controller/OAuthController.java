package study.carrotmarketbackend_v1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.carrotmarketbackend_v1.service.OAuthService;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callBack(HttpServletRequest request, HttpServletResponse response) {

        String callResponse = oAuthService.callBack(request,response);

        return ResponseEntity.ok(callResponse);
    }
}
