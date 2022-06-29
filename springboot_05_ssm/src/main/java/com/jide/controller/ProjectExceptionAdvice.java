package com.jide.controller;

import com.jide.exception.BusinessException;
import com.jide.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 晓蝈
 * @version 1.0
 */
@RestControllerAdvice
public class ProjectExceptionAdvice {

    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex) {
        //记录日志
        //发消息给运维
        //发消息给开发人员
        return new Result(ex.getCode(), null, ex.getMessage());
    }


    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex) {
        return new Result(ex.getCode(), null, ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result doOtherException(Exception ex) {
        //记录日志
        //发消息给运维
        //发消息给开发人员
        return new Result(Code.SYSTEM_UNKNOW_ERR, null, "系统繁忙，请稍后重试");
    }
}
