package com.svm.backend.common.api;

/**
 * 封装API的錯誤碼
 *
 * @author : kevin Chang
 */
public interface IErrorCode {
    /**
     * Get Code number
     * @return
     */
    long getCode();

    /**
     * Get Message
     * @return
     */
    String getMessage();
}
