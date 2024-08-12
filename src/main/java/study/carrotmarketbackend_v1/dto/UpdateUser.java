package study.carrotmarketbackend_v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.carrotmarketbackend_v1.entity.User;

public class UpdateUser {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "이름은 필수 항목입니다.")
        private String username;

        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        private String email;

        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10자리 또는 11자리여야 합니다.")
        private String phone;

        private AddressDTO address;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response{

        private String username;

        private String email;

        private String phone;

        private AddressDTO address;

        public static Response fromEntity(User user) {

            AddressDTO address = AddressDTO.fromEntity(user.getAddress());
            return Response.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .address(address)
                    .build();

        }

    }

}
