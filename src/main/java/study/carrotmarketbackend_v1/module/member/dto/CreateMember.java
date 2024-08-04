package study.carrotmarketbackend_v1.module.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import study.carrotmarketbackend_v1.common.dto.ApiResponse;
import study.carrotmarketbackend_v1.module.address.dto.AddressDTO;
import study.carrotmarketbackend_v1.module.address.entity.Address;
import study.carrotmarketbackend_v1.module.member.entity.Member;
import study.carrotmarketbackend_v1.module.member.status.MemberStatusEnum;

public class CreateMember {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "이름은 필수 항목입니다.")
        private String name;

        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 문자와 숫자를 포함해야 합니다.")
        private String password;

        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10자리 또는 11자리여야 합니다.")
        private String phone;

        private AddressDTO address;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String name;

        private String email;

        private String password;

        private String phone;

        private AddressDTO address;

        public static Response fromEntity(Member member) {
            // 엔티티를 DTO로 변환하는 변환 메서드 호출
            AddressDTO addressDTO = AddressDTO.fromEntity(member.getAddress());
            return Response.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .address(addressDTO)
                    .build();
        }
    }
}
