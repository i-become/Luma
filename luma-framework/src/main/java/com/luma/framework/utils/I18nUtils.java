package com.luma.framework.utils;

import com.luma.common.exception.BaseException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类
 * @author i-become
 */
@Component
public class I18nUtils {

    private static MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }

    /**
     * 获取国际化消息
     * @param messageKey 消息键
     * @param args 消息参数
     * @return 国际化文本
     */
    public static String getMessage(String messageKey, Object... args) {
        if (messageSource == null || messageKey == null) {
            return messageKey;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageKey, args, messageKey, locale);
    }

    /**
     * 解析业务异常消息
     * @param e 业务异常
     * @return 国际化文本
     */
    public static String resolve(BaseException e) {
        if (e.getMessageKey() != null) {
            return getMessage(e.getMessageKey(), e.getArgs());
        }
        String message = e.getMessage();
        if (message != null && message.startsWith("exception.")) {
            return getMessage(message);
        }
        return message;
    }

}
