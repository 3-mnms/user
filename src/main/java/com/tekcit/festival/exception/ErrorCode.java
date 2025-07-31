package com.tekcit.festival.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 예시 양식
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND_BY_EMAIL("U002", "해당 사용자를 찾을 수 없습니다. Email: %s", HttpStatus.NOT_FOUND),
    DUPLICATE_LOGIN_ID("U003", "이미 존재하는 아이디입니다. ID: %s", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL_ID("U004", "이미 존재하는 이메일입니다. EMAIL: %s", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_ID("U005", "이미 존재하는 전화번호입니다. PHONE: %s", HttpStatus.CONFLICT),
    //    AUTH 관련 에러입니다.
    AUTH_PASSWORD_NOT_EQUAL_ERROR("A002","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
