package study.carrotmarketbackend_v1.status;

import lombok.Getter;

@Getter
public enum UserType {
    REGULAR,   // 일반 사용자
    OAUTH,      // OAuth 사용자
    BLACK_LIST;
}