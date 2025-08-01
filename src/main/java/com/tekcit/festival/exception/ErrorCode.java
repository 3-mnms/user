package com.tekcit.festival.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 예시 양식
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    DUPLICATE_LOGIN_ID("U002", "이미 존재하는 아이디입니다. ID: %s", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL_ID("U003", "이미 존재하는 이메일입니다. EMAIL: %s", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_ID("U004", "이미 존재하는 전화번호입니다. PHONE: %s", HttpStatus.CONFLICT),
    //    AUTH 관련 에러입니다.
    AUTH_PASSWORD_NOT_EQUAL_ERROR("U005","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_REFRESH_TOKEN_EXPIRED("U006", "Refresh Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_REFRESH_TOKEN_NOT_MATCH("U007", "Refresh Token이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;        // A001, A002 등
    private final String message;     // 사용자에게 보여줄 메시지
    private final HttpStatus status;  //http status 코드

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
