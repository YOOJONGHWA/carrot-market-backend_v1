package study.carrotmarketbackend_v1.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.carrotmarketbackend_v1.dto.ApiResponse;
import study.carrotmarketbackend_v1.dto.CreateMember;
import study.carrotmarketbackend_v1.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/members")
public class MemberController {


    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<CreateMember.Response>> createMember(@Valid @RequestBody CreateMember.Request request) {
        log.info("request: {}", request);
        ApiResponse<CreateMember.Response> response = memberService.createMember(request);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<String> getUserDetails() {

        return ResponseEntity.ok("사용중입니당~");
    }
}
