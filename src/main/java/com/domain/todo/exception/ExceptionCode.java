package com.domain.todo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//private final 필드에 getter 자동생성, 코드 간결성을 위해
@Getter
public enum ExceptionCode {
    //400
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "CONTENT001", "할일 입력은 필수입니다."),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "TITLE002", "1000자 이하로 작성해주세요."),

    //404
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO001", "존재하지 않는 할일입니다."),

    //409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER001", "이미 가입된 이메일입니다."),

    //401
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH001", "로그인에 실패했습니다."),

    //500
    UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TODO002", "수정에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TODO003", "삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionCode(HttpStatus status, String code, String message) {
        //this.필드명 = 매개변수. 구분하기 위해 사용
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
