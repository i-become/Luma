package com.luma.framework.interceptor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 将客户端utc时间转换为服务端本地时间
 * @author i-become
 */
public class UtcToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> implements Converter<String, LocalDateTime> {

    public static final UtcToLocalDateTimeDeserializer instance = new UtcToLocalDateTimeDeserializer();

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return convert(jsonParser.getText());
    }

    @Override
    public LocalDateTime convert(String source) {
        if (source.trim().isEmpty()) {
            return null;
        }
        // 解析 UTC
        Instant instant = Instant.parse(source);
        // 转服务器本地时间
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
