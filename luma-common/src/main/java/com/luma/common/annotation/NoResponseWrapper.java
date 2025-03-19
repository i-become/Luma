package com.luma.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应不包装返回注解
 * 被此注解标注的Controller接口将不会进行包装返回
 * @author i-become
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResponseWrapper {

}
