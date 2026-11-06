package com.sky.exception;

/**
 * 套餐起售失败异常
 */
public class SetmealEnableFailedException extends BaseException {

    public SetmealEnableFailedException() {
    }

    public SetmealEnableFailedException(String msg) {
        super(msg);
    }
}
