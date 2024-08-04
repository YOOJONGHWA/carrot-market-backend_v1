package study.carrotmarketbackend_v1.module.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


public class LoginJwt {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class LoginRequest {

        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        private String password;

    }

}
