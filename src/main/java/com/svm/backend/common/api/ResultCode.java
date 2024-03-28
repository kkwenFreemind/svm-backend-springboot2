package com.svm.backend.common.api;

/**
 * 列舉常用API操作碼
 *
 * @author : kevin Chang
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "成功"),
    FAILED(500, "失敗"),
    UNAUTHORIZED(401, "尚未登入或令牌token已過期"),
    PASSWORD_FAILED(402, "密碼強度不足"),
    FORBIDDEN(403, "無此權限"),
    VALIDATE_FAILED(404, "參數有誤");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
