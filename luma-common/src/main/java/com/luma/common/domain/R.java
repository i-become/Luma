package com.luma.common.domain;

import com.luma.common.constant.ResultCode;
import lombok.Data;

/**
 * @author 刘靖
 */
@Data
public class R<T> {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应体
     */
    private T data;

    /**
     * 响应描述
     */
    private String message;

    public static <T> R<T> success(){
        return restResult(null, ResultCode.OK, null);
    }

    public static <T> R<T> success(T data){
        return restResult(data, ResultCode.OK, null);
    }

    public static <T> R<T> success(T data, String msg){
        return restResult(data, ResultCode.OK, msg);
    }

    public static <T> R<T> fail(){
        return restResult(null, ResultCode.FAIL, null);
    }

    public static <T> R<T> fail(T data){
        return restResult(data, ResultCode.FAIL, null);
    }

    public static <T> R<T> fail(String msg){
        return restResult(null, ResultCode.FAIL, msg);
    }

    public static <T> R<T> fail(String message, int code){
        return restResult(null, code, message);
    }

    public static <T> R<T> fail(T data, String message){
        return restResult(data, ResultCode.FAIL, message);
    }

    public R<T> code(int code){
        this.code = code;
        return this;
    }

    public R<T> data(T data){
        this.data = data;
        return this;
    }

    public R<T> message(String message){
        this.message = message;
        return this;
    }

    private static <T> R<T> restResult(T data, int code, String message){
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(message);
        return apiResult;
    }

    @Override
    public String toString(){
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"message\": " + transValue(this.getMessage())
                + ", \"data\": " + transValue(this.getData())
                + "}";
    }

    /**
     * 转换 value 值：
     * 	如果 value 值属于 String 类型，则在前后补上引号
     * 	如果 value 值属于其它类型，则原样返回
     *
     * @param value 具体要操作的值
     * @return 转换后的值
     */
    private String transValue(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

}
