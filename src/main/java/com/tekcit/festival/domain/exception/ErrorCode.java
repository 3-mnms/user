package com.tekcit.festival.domain.exception.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 예시 오류코드 (추후 도메인별로 추가하세요)
    USER_NOT_FOUND("U001", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    INVALID_REQUEST("C001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String code;        // A001, U001 등
    private final String message;     // 사용자에게 보여줄 메시지
    private final HttpStatus status;  // HTTP 상태 코드

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
