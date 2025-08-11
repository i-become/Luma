package com.luma.framework.interceptor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 将本地时间转换为UTC时间
 * @author i-become
 */
public class LocalToUtcDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    public static final LocalToUtcDateTimeSerializer instance = new LocalToUtcDateTimeSerializer();

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String utcString = localDateTime.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
        jsonGenerator.writeString(utcString);
    }

}
