package com.tekcit.festival.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 예시 양식
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    USER_DEACTIVATED("U002", "정지된 계정입니다. 관리자 이메일로 문의하세요.", HttpStatus.FORBIDDEN),
    USER_EMAIL_NOT_VERIFIED("U003", "이메일이 인증되지 않았습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_LOGIN_ID("U004", "이미 존재하는 아이디입니다. ID: %s", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL_ID("U005", "이미 존재하는 이메일입니다. EMAIL: %s", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_ID("U006", "이미 존재하는 전화번호입니다. PHONE: %s", HttpStatus.CONFLICT),
    //    AUTH 관련 에러입니다.
    AUTH_PASSWORD_NOT_EQUAL_ERROR("U007","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_REFRESH_TOKEN_EXPIRED("U008", "Refresh Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_REFRESH_TOKEN_NOT_MATCH("U009", "Refresh Token이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_NOT_ALLOWED("U0010", "허용되지 않는 행동입니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_NOT_FOUND("U0011", "인증 요청이 없습니다.", HttpStatus.NOT_FOUND),
    VERIFICATION_EXPIRED("U0012","인증 코드가 만료되었습니다.", HttpStatus.GONE),
    VERIFICATION_CODE_MISMATCH("U0013","인증 코드가 일치하지 않습니다.",HttpStatus.BAD_REQUEST);

    private final String code;        // A001, A002 등
    private final String message;     // 사용자에게 보여줄 메시지
    private final HttpStatus status;  //http status 코드

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
