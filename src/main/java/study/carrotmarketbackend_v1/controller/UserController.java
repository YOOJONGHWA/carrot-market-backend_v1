package study.carrotmarketbackend_v1.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import study.carrotmarketbackend_v1.dto.ApiResponse;
import study.carrotmarketbackend_v1.dto.ChangePassword;
import study.carrotmarketbackend_v1.dto.CreateUser;
import study.carrotmarketbackend_v1.dto.UpdateUser;
import study.carrotmarketbackend_v1.entity.User;
import study.carrotmarketbackend_v1.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService memberService) {
        this.userService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<CreateUser.Response>> createMember(@Valid @RequestBody CreateUser.Request request) {

        ApiResponse<CreateUser.Response> response = userService.createMember(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<User>> getUserDetails(Authentication authentication) {

        ApiResponse<User> response = userService.getUserDetails(authentication);
        return  ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UpdateUser.Response>> updateUser(@Valid @RequestBody UpdateUser.Request request, Authentication authentication) {

        ApiResponse<UpdateUser.Response> response = userService.updateUser(request,authentication);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePassword.Request request, Authentication authentication) {

        ApiResponse<Void> response = userService.changePassword(request,authentication);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getUserDetails() {
        log.info("해당 정보 : {} ", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return ResponseEntity.ok("사용중입니당~");
    }
}
