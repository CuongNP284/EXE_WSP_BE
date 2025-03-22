package com.wsp.workshophy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(1009, "Post not existed", HttpStatus.NOT_FOUND),
    POST_NOT_AVAILABLE(1010, "Post is not available", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1011, "Email is invalid", HttpStatus.BAD_REQUEST),
    ORGANIZER_PROFILE_NOT_FOUND(1012, "Organizer profile not found", HttpStatus.NOT_FOUND),
    ORGANIZER_PROFILE_ALREADY_EXISTS(1013, "Organizer profile already exists for this user", HttpStatus.BAD_REQUEST),
    USER_NOT_ORGANIZER(1014, "User does not have ORGANIZER role", HttpStatus.BAD_REQUEST),
    WORKSHOP_CATEGORY_NOT_FOUND(1015, "Workshop category not found", HttpStatus.NOT_FOUND),;
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
