package com.luma.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * web配置属性
 * @author i-become
 */
@ConfigurationProperties(prefix = "luma.api")
@Data
public class WebProperties {

    private Api webApi;

    private Api openApi;

    @Data
    public static class Api {

        /**
         * 请求前缀
         */
        private String prefix;

        /**
         * Controller所在包名
         */
        private String controller;

        /**
         * 排除的url
         */
        private List<String> excludePathPatterns;

        public List<String> getExcludePathPatterns() {
            // 拼上前缀
            return excludePathPatterns.stream().map(s -> prefix + s).toList();
        }
    }

}
