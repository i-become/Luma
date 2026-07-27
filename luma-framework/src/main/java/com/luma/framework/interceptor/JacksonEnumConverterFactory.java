package com.luma.framework.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * spring boot对于表单请求中枚举类的序列化处理
 * 使用Jackson代理处理
 * @author 刘靖
 */
@Component
public class JacksonEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public <T extends Enum<?>> @NotNull Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        return source -> {
            if (source.isBlank()) {
                return null;
            }
            return objectMapper.convertValue(source, targetType);
        };
    }

}
