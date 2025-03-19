package com.luma.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;


/**
 * json工具类
 * @author i-become
 */
public class JsonUtil {

    private final static ObjectMapper MAPPER = new ObjectMapper()
            // 将long类型转换为字符串
            .registerModule(new SimpleModule().addSerializer(Long.class, ToStringSerializer.instance))
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final static ObjectMapper MAPPER_NONNULL = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @SneakyThrows(value = JsonProcessingException.class)
    public static String toJsonString(Object obj) {
        return MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows(value = JsonProcessingException.class)
    public static String toJsonStringNonNull(Object obj) {
        return MAPPER_NONNULL.writeValueAsString(obj);
    }

    @SneakyThrows(value = JsonProcessingException.class)
    public static <T> T parse(String json, Class<T> cls) {
        return MAPPER.readValue(json, cls);
    }

    @SneakyThrows(value = JsonProcessingException.class)
    public static <T> T parse(String json, TypeReference<T> type) {
        return MAPPER.readValue(json, type);
    }

    @SneakyThrows(value = JsonProcessingException.class)
    public static JsonNode parse(String json) {
        return MAPPER.readTree(json);
    }

    public static <T> T convert(Object object, Class<T> cls){
        return MAPPER.convertValue(object, cls);
    }

    public static <T> T convert(Object object, TypeReference<T> toValueTypeRef){
        return MAPPER.convertValue(object, toValueTypeRef);
    }

}

