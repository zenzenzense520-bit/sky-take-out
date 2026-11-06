package com.sky.exception;

/**
 * 业务异常基类，所有自定义异常继承此类，由全局异常处理器统一捕获
 */
public class BaseException extends RuntimeException {

    public BaseException() {
        super();
    }

    public BaseException(String msg) {
        super(msg);
    }
}
