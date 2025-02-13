package study.carrotmarketbackend_v1.status;

import lombok.Getter;

@Getter
public enum UserStatusEnum implements BaseEnum {

    OK(200, "성공"),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "서버 에러 "),
    USER_NOT_FOUND(402, "해당 유저가 존재 하지 안습니다."),;

    public final int statusCode;
    public final String message;

    UserStatusEnum(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
