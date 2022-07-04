package com.jide.reggie.common;

/**
 * 基于 封装的工具类，保存和获取当前登录用户的id
 */
public class BaseContext {
    private BaseContext(){}
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
