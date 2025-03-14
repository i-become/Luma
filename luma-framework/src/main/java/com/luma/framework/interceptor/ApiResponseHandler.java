package com.luma.framework.interceptor;

import com.luma.common.annotation.NoResponseWrapper;
import com.luma.common.domain.R;
import com.luma.common.utils.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局统一包装响应类型
 * @author 刘靖
 */
@ControllerAdvice
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.hasMethodAnnotation(NoResponseWrapper.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof R){
            return body;
        }
        // 如果是html则放行
        if (selectedContentType.toString().equals(MediaType.TEXT_HTML_VALUE)){
            return body;
        }

        // 对String类型的单独处理
        if (body instanceof String){
            return JsonUtil.toJsonString(R.success(body));
        }

        return R.success(body);
    }

}
