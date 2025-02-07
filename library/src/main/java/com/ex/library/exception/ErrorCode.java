package com.ex.library.exception;

public enum ErrorCode {
    INVALID_VALUE_INPUT(1400,"invalid value input!"),
    SERVER_IN_MAINTENANCE(1000, "Server in maintenance!"),
    USER_NOT_EXISTED(1001, "user not exist!"),
    USER_EXISTED(1002, "user existed!"),
    EMAIL_EXISTED(1003, "email existed!"),
    WRONG_PASSWORD(1004, "Wrong password"),
    UNAUTHENTICATED(403, "User is unauthenticated"),
    SEND_MAIL_ERROR(1006, "Cannot send mail this time, please try again later"),
    USER_INACTIVE(1007, "User is inactive. Activate the user first!"),
    CODE_NOT_FOUND(1008, "Code not found!"),
    DUPLICATE_DATA(1009,"Your data is duplicated!"),
    WEAK_PASSWORD(1010, "Your password is weak")
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
