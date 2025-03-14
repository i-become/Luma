//package com.luma.framework.interceptor;
//
//
//import org.springframework.core.convert.converter.Converter;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * localDateTime转化
// * @author 刘靖
// */
//public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
//    private final DateTimeFormatter formatter;
//
//    public StringToLocalDateTimeConverter(DateTimeFormatter formatter) {
//        this.formatter = formatter;
//    }
//
//    @Override
//    public LocalDateTime convert(String source) {
//        return LocalDateTime.parse(source, formatter);
//    }
//}
//
