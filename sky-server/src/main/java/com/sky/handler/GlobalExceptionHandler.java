package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，统一异常响应格式，避免堆栈直接暴露给前端
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 业务异常：返回异常中定义的提示信息 */
    @ExceptionHandler(BaseException.class)
    public Result<String> baseExceptionHandler(BaseException ex) {
        log.info("业务异常: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /** 唯一约束冲突：提取冲突字段给出友好提示 */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> sqlExceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        log.info("SQL 约束异常: {}", message);
        if (message != null && message.contains("Duplicate entry")) {
            String[] parts = message.split(" ");
            return Result.error(parts[2] + " " + MessageConstant.ALREADY_EXISTS);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /** 兜底异常：记录完整日志，前端只看到通用提示 */
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        log.error("系统异常", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
