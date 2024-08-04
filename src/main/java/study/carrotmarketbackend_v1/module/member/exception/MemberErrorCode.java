package study.carrotmarketbackend_v1.module.member.exception;

import lombok.Getter;

@Getter
public enum MemberErrorCode {

    EMAIL_ALREADY_EXISTS(400, "이메일이 이미 존재합니다."),
    USERNAME_ALREADY_EXISTS(400, "사용자 이름이 이미 존재합니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다.");

    private final int status;
    private final String message;

    MemberErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
