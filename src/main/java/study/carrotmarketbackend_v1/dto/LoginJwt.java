package study.carrotmarketbackend_v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class LoginJwt {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request {

        @NotBlank(message = "올바르지 않은 양식입니다!")
        private String email;

        @NotBlank(message = "비밀번호를 입력하세요!")
        private String password;

    }
    @Getter
    @Setter
    @Builder
    public static class Response {
        private String accessToken;

        public static Response fromJwt(String accessToken) {

            return Response.builder()
                    .accessToken(accessToken)
                    .build();
        }
    }

}