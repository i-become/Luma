package com.luma.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author ruoyi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 需要判断的带有dept_id表的别名
     */
    String deptAlias() default "";

    /**
     * 需要判断的带有user_id表的别名
     */
    String userAlias() default "";

    /**
     * dept_id字段的查询名称
     * 应对有些表中dept_id名称不叫作dept_id
     * @return
     */
    String deptIdColumnName() default "dept_id";

    /**
     * user_id字段的查询名称
     * 应对有些表中user_id名称不叫作user_id
     * @return
     */
    String userIdColumnName() default "user_id";

    /**
     * 权限字符（用于多个角色匹配符合要求的权限）默认根据权限注解@SaCheckPermission获取，多个权限用逗号分隔开来
     */
    String permission() default "";

    /**
     * 是否自动拼接权限范围sql
     * @return
     */
    boolean autoSql() default true;

}
