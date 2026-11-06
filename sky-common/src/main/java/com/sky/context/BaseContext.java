package com.sky.context;

/**
 * 基于 ThreadLocal 的请求上下文
 * 作用：在一次请求的任意层级获取当前登录用户 ID，避免层层传参
 */
public class BaseContext {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        THREAD_LOCAL.set(id);
    }

    public static Long getCurrentId() {
        return THREAD_LOCAL.get();
    }

    public static void removeCurrentId() {
        THREAD_LOCAL.remove();
    }
}
