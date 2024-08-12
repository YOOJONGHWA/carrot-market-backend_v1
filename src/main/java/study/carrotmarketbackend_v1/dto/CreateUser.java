package study.carrotmarketbackend_v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import study.carrotmarketbackend_v1.entity.User;

public class CreateUser {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "이름은 필수 항목입니다.")
        private String username;

        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 문자와 숫자를 포함해야 합니다.")
        private String password;

        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10자리 또는 11자리여야 합니다.")
        private String phone;

        private AddressDTO address;

        private String role;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String username;

        private String email;

        private String password;

        private String phone;

        private AddressDTO address;

        private String role;

        public static Response fromEntity(User user) {
            // 엔티티를 DTO로 변환하는 변환 메서드 호출
            AddressDTO addressDTO = AddressDTO.fromEntity(user.getAddress());
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .address(addressDTO)
                    .role(user.getRole())
                    .build();
        }
    }
}
