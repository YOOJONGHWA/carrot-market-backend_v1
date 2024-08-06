package study.carrotmarketbackend_v1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.dto.ApiResponse;
import study.carrotmarketbackend_v1.dto.CreateMember;
import study.carrotmarketbackend_v1.entity.Address;
import study.carrotmarketbackend_v1.entity.Member;
import study.carrotmarketbackend_v1.exception.MemberErrorCode;
import study.carrotmarketbackend_v1.exception.MemberException;
import study.carrotmarketbackend_v1.repository.MemberRepository;
import study.carrotmarketbackend_v1.status.MemberStatusEnum;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public ApiResponse<CreateMember.Response> createMember(CreateMember.Request request) {

        validateCreateMemberRequest(request);

        // DTO를 엔티티로 변환
        Address address = new Address(
                request.getAddress().getCity(),
                request.getAddress().getStreet(),
                request.getAddress().getZipcode()
        );

        /*
        * 역할 저장 형식: 현재 memberRole 변수를 USER 또는 ADMIN으로 설정하고 있지만, S
        * pring Security는 권한 문자열에 ROLE_ 접두어를 자동으로 추가합니다.
        * 따라서 저장된 역할이 USER일 때 Spring Security에서는 이를 ROLE_USER로 변환하여 인식합니다.
        *
        * */
        String memberRole = "ROLE_USER";
        if(request.getEmail().equals("admin@admin.com")) {
            memberRole = "ROLE_ADMIN";
        }
        
        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(address)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(memberRole)
                .build();
        memberRepository.save(member);

        CreateMember.Response response = CreateMember.Response.fromEntity(member);
        return ApiResponse.<CreateMember.Response>builder()
                .status(MemberStatusEnum.OK.getStatusCode())
                .message(MemberStatusEnum.OK.getMessage())
                .body(response)
                .build();
    }

    private void validateCreateMemberRequest(CreateMember.Request request) {

        // 이메일 중복 검사
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자 이름 중복 검사
        if (memberRepository.findByName(request.getName()).isPresent()) {
            throw new MemberException(MemberErrorCode.USERNAME_ALREADY_EXISTS);
        }

    }
    
}
