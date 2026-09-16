package com.luma.framework.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.luma.common.domain.R;
import com.luma.common.exception.BaseException;
import com.luma.framework.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

/**
 * 全局异常处理器
 * @author i-become
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常拦截器
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = (Objects.requireNonNull(e.getBindingResult().getFieldError())).getDefaultMessage();
        return R.fail(message);
    }

    /**
     * 业务异常
     * @param e
     * @return
     */
    @ExceptionHandler({BaseException.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> handlerBaseException(BaseException e){
        String message = I18nUtils.resolve(e);
        if (e.getCode() == null){
            return R.fail(message);
        }
        return R.fail(message, e.getCode());
    }

    /**
     * 资源未找到异常
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> handlerException(NoResourceFoundException e){
        return R.fail(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * 没有登录异常
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({NotLoginException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public <T> R<T> handlerNotLoginException(NotLoginException e){
        return R.fail(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * SQL 语法错误异常
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({BadSqlGrammarException.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> sqlSyntaxErrorException(BadSqlGrammarException e){
        log.error("SQL 语法错误异常：", e);
        return R.fail(I18nUtils.getMessage("exception.common.sortField.error"));
    }

    /**
     * satoken鉴权异常
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({NotPermissionException.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> sqlSyntaxErrorException(NotPermissionException e){
        log.error("satoken鉴权异常", e);
        return R.fail(e.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    /**
     * 其余异常
     * @param e
     * @return
     * @param <T>
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public <T> R<T> handlerException(Exception e){
        log.error("未知异常：", e);
        return R.fail(e.getMessage());
    }

}
