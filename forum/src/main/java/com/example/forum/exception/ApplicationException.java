package com.example.forum.exception;

import com.example.forum.common.AppResult;

public class ApplicationException extends RuntimeException{


    // 在异常中持有一个错误信息对象
    protected AppResult errorResult;

    public AppResult getErrorResult() {
        return errorResult;
    }

    public ApplicationException (AppResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
