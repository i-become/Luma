package com.luma.common.exception;

import lombok.Data;

/**
 * 自定义基础异常
 * @author 刘靖
 */
@Data
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

    public BaseException(){}

}
