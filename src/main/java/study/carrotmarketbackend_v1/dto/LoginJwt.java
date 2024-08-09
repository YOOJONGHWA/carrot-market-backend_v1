package study.carrotmarketbackend_v1.dto;

import lombok.*;


public class LoginJwt {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request {

        private String email;

        private String password;

    }
    @Getter
    @Setter
    @Builder
    public static class Response {
        private String message; // 추가

        public static Response fromJwt(String message) {

            return Response.builder()
                    .message(message)
                    .build();
        }
    }

}