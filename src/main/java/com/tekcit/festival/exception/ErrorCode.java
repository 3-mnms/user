package com.tekcit.festival.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 예시 양식
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    USER_DEACTIVATED("U002", "정지된 계정입니다. 관리자 이메일로 문의하세요.", HttpStatus.FORBIDDEN),
    USER_EMAIL_NOT_VERIFIED("U003", "이메일이 인증되지 않았습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_LOGIN_ID("U004", "이미 존재하는 아이디입니다. ID: %s", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL_ID("U005", "이미 존재하는 이메일입니다. EMAIL: %s", HttpStatus.CONFLICT),
    DUPLICATE_KAKAO_ID("U006", "이미 존재하는 카카오 계정입니다. KAKAO_ID: %s", HttpStatus.CONFLICT),
    USER_EMAIL_NOT_MATCH("U007", "이메일이 일치하지 않습니다. EMAIL: %s", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND("U008", "주소가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_ALLOWED("U009", "허용되지 않는 행동입니다. 작성자만이 주소를 수정 또는 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    ADDRESS_DEFAULT_NOT_DELETED("U010", "기본 주소지는 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),

    //    AUTH 관련 에러입니다.
    AUTH_PASSWORD_NOT_EQUAL_ERROR("A001","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_REFRESH_TOKEN_EXPIRED("A002", "Refresh Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_TOKEN_EXPIRED("A003", "Access Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("A004", "올바르지 못한 Access Token 입니다.", HttpStatus.BAD_REQUEST),
    AUTH_REFRESH_TOKEN_NOT_MATCH("A005", "Refresh Token이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_NOT_ALLOWED("A006", "허용되지 않는 행동입니다.", HttpStatus.FORBIDDEN),
    AUTH_TOKEN_MISSING("A007", "토큰이 없습니다.", HttpStatus.BAD_REQUEST),

    //이메일 인증 에러
    EMAIL_VERIFICATION_NOT_FOUND("E001", "인증 요청이 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_VERIFICATION_EXPIRED("E002","인증 코드가 만료되었습니다.", HttpStatus.GONE),
    EMAIL_VERIFICATION_CODE_MISMATCH("E003","인증 코드가 일치하지 않습니다.",HttpStatus.BAD_REQUEST),

    //카카오 에러
    KAKAO_INVALID_FIELDS("K001", "%s", HttpStatus.BAD_REQUEST),
    KAKAO_INVALID_TICKET("K002", "%s", HttpStatus.BAD_REQUEST),
    KAKAO_UNLINK_FAILED("K003", "%s", HttpStatus.BAD_REQUEST);

    private final String code;        // A001, A002 등
    private final String message;     // 사용자에게 보여줄 메시지
    private final HttpStatus status;  //http status 코드

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}