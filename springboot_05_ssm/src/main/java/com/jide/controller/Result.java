package com.jide.controller;

/**
 * @author 晓蝈
 * @version 1.0
 */
public class Result {
    private Integer Code;
    private Object data;
    private String message;

    public Result() {
    }

    public Result(Integer code, Object data) {
        Code = code;
        this.data = data;
    }

    public Result(Integer code, Object data, String message) {
        Code = code;
        this.data = data;
        this.message = message;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
