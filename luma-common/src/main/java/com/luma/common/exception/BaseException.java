package com.luma.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义基础异常
 * @author i-become
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException{

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 异常原因
     */
    private String message;

    /**
     * 异常模块
     */
    private String module;

    /**
     * 国际化消息键
     */
    private String messageKey;

    /**
     * 国际化消息参数
     */
    private Object[] args;

    public BaseException(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code){
        this.code = code;
    }

    public BaseException(String message){
        this.message = message;
    }

    /**
     * 国际化业务异常
     * @param messageKey 消息键
     * @param args 消息参数
     */
    public BaseException(String messageKey, Object... args){
        this.messageKey = messageKey;
        this.args = args;
    }

    public BaseException(){}

}
