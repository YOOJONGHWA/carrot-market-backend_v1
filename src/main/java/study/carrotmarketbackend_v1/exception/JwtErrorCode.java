package study.carrotmarketbackend_v1.exception;

import lombok.Getter;

@Getter
public enum JwtErrorCode {
    JWT_EXPIRED("JWT 토큰 유효기간 만료", 401),
    JWT_PROCESSING_ERROR("JWT 토큰 처리 중 오류 발생", 500),
    REFRESH_JWT_PROCESSING_ERROR("JWT 리프레시 토큰 처리 중 오류 발생", 500),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다", 403),
    INVALID_SECRET_KEY("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", 500),
    INVALID_TOKEN_FORMAT("JWT 토큰 형식이 잘못되었습니다.", 400),
    MALFORMED_JWT("JWT 토큰 구조가 잘못되었습니다.", 400),
    INVALID_SIGNATURE("JWT 서명이 유효하지 않습니다.", 401),
    MISSING_TOKEN("JWT 토큰이 요청에 없습니다.", 401),
    TOKEN_REVOKED("JWT 토큰이 취소되었습니다.", 403),
    MISSING_COOKIE("JWT 쿠키가 요청에 없습니다.", 401),
    INVALID_COOKIE_FORMAT("JWT 쿠키 형식이 잘못되었습니다.", 400),
    COOKIE_EXPIRED("JWT 쿠키 유효기간 만료", 401),
    COOKIE_READ_WRITE_ERROR("JWT 쿠키를 읽거나 쓰는 중에 오류 발생", 500);

    private final String message;
    private final int status;

    JwtErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
