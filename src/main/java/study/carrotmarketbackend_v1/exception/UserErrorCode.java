package study.carrotmarketbackend_v1.exception;

import lombok.Getter;

@Getter
public enum UserErrorCode {

    EMAIL_ALREADY_EXISTS(400, "이메일이 이미 존재합니다."),
    USERNAME_ALREADY_EXISTS(400, "사용자 이름이 이미 존재합니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다."),
    INVALID_PASSWORD(400, "비밀번호가 일치 하지 않습니다"),
    SAME_PASSWORD(400, "기존 비밀번호랑 일치 합니다.");

    private final int status;
    private final String message;

    UserErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
