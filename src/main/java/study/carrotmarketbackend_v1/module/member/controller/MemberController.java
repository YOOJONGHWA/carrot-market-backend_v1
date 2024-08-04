package study.carrotmarketbackend_v1.module.member.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.carrotmarketbackend_v1.common.dto.ApiResponse;
import study.carrotmarketbackend_v1.module.member.dto.CreateMember;
import study.carrotmarketbackend_v1.module.member.dto.LoginJwt;
import study.carrotmarketbackend_v1.module.member.service.MemberService;

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

    @PostMapping("/login")
    public ResponseEntity<LoginJwt.LoginRequest> login(@Valid @RequestBody LoginJwt.LoginRequest request) {


        return ResponseEntity.ok(request);
    }

}
