package com.luma.common.domain;

import com.luma.common.utils.MapstructUtil;

/**
 * 实体类与其它类转换基类，结合Mapstruct进行使用，给每个实体类赋予与其它vo、bo等类的转换函数
 */
public class EntityConvert {

    /**
     * 将当前对象转换为目标类型对象
     * 需要在目标对象上添加@AutoMapper映射关系注解
     * @param tClass 目标对象类型
     * @return
     * @param <T>
     */
    public <T> T to(Class<T> tClass){
        return MapstructUtil.convert(this, tClass);
    }

}
