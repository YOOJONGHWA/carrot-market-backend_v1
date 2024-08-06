package study.carrotmarketbackend_v1.status;

import lombok.Getter;

@Getter
public enum LoginResponseStatus {
    LOGIN_SUCCESS(200, "로그인 성공, 토큰이 정상 발급되었습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "잘못된 이메일 또는 비밀번호입니다."),
    ACCOUNT_LOCKED(403, "계정이 잠겼습니다."),
    NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    PASSWORD_RESET_REQUIRED(410, "비밀번호 재설정이 필요합니다.");

    private final int statusCode;
    private final String message;

    LoginResponseStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
