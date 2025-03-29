package com.luma.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 数据权限枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * 全部数据权限
     */
    ALL(1, " ( 1 == 1 ) ") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            return this.getSqlTemplate();
        }
    },

    /**
     * 自定数据权限
     */
    CUSTOM(2, " OR {}{} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            return StrUtil.format(this.getSqlTemplate(), deptAlias, deptIdColumnName, String.join(StrUtil.COMMA, customRoleIdList));
        }
    },

    /**
     * 部门及以下数据权限
     */
    DEPT_AND_CHILD(3, " OR {}{} IN ( SELECT id FROM sys_dept WHERE id = {} or find_in_set( {} , ancestors ) )") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            return StrUtil.format(this.getSqlTemplate(), deptAlias, deptIdColumnName, deptId, deptId);
        }
    },

    /**
     * 部门数据权限
     */
    DEPT(4, " OR {}{} = {} ") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            return StrUtil.format(" OR {}{} = {} ", deptAlias, deptIdColumnName, deptId);
        }
    },

    /**
     * 仅本人数据权限
     */
    SELF(5, " OR {}{} = {} ") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            if (StrUtil.isNotBlank(userAlias)) {
                return StrUtil.format(this.getSqlTemplate(), userAlias, userIdColumnName, userId);
            } else {
                // 数据权限为仅本人且没有userAlias别名不查询任何数据
                return NONE.generateSql(deptAlias, deptIdColumnName, customRoleIdList, deptId, userAlias, userIdColumnName, userId);
            }
        }
    },

    /**
     * 没有数据权限
     */
    NONE(6, " OR {}{} = -1 ") {
        @Override
        public String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId) {
            return StrUtil.format(this.getSqlTemplate(), deptAlias, deptIdColumnName);
        }
    };

    @EnumValue
    private final int code;

    /**
     * sql模板
     */
    private final String sqlTemplate;

    /**
     * 生成sql
     * @return
     */
    public abstract String generateSql(String deptAlias, String deptIdColumnName, List<String> customRoleIdList, Long deptId, String userAlias, String userIdColumnName, Long userId);

}
