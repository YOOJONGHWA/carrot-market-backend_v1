package study.carrotmarketbackend_v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ChangePassword {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "현재 비밀번호는 필수 항목입니다.")
        private String oldPassword;

        @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
        private String newPassword;

    }

}
