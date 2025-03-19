package com.luma.common.exception.system;

import com.luma.common.exception.BaseException;

/**
 * 系统模块异常
 * @author i-become
 */
public class ISystemException extends BaseException {

    private static final String MODULE = "system";

    public ISystemException(){
        this.setModule(MODULE);
    }

    public ISystemException(Integer code, String message){
        super(code, message);
        this.setModule(MODULE);
    }

    public ISystemException(Integer code){
        super(code);
        this.setModule(MODULE);
    }

    public ISystemException(String message){
        super(message);
        this.setModule(MODULE);
    }

}
