package study.carrotmarketbackend_v1.module.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import study.carrotmarketbackend_v1.common.dto.ApiResponse;
import study.carrotmarketbackend_v1.module.address.entity.Address;
import study.carrotmarketbackend_v1.module.member.dto.CreateMember;
import study.carrotmarketbackend_v1.module.member.entity.Member;
import study.carrotmarketbackend_v1.module.member.exception.MemberErrorCode;
import study.carrotmarketbackend_v1.module.member.exception.MemberException;
import study.carrotmarketbackend_v1.module.member.repository.MemberRepository;
import study.carrotmarketbackend_v1.module.member.status.MemberStatusEnum;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ApiResponse<CreateMember.Response> createMember(CreateMember.Request request) {

        validateCreateMemberRequest(request);

        // DTO를 엔티티로 변환
        Address address = new Address(
                request.getAddress().getCity(),
                request.getAddress().getStreet(),
                request.getAddress().getZipcode()
        );

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(address)
                .role("ROLE_MEMBER")
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
