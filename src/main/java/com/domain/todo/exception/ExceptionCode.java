package com.domain.todo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    //400
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "CONTENT001", "할일 입력은 필수입니다."),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "TITLE002", "1000자 이하로 작성해주세요."),

    //401
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH001", "로그인에 실패했습니다."),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH002", "로그인이 필요합니다."),

    //403
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH003", "접근 권한이 없습니다."),

    //404
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO001", "존재하지 않는 할일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH004", "사용자를 찾을 수 없습니다."),

    //409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER001", "이미 가입된 이메일입니다."),

    //500
    UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TODO002", "수정에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TODO003", "삭제에 실패했습니다."),
    USER_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH005", "회원가입에 실패했습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
