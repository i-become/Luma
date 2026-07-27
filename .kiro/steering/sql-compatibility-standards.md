---
inclusion: auto
---

# SQL数据库兼容性规范

## 概述
本项目需要兼容多种数据库（MySQL、PostgreSQL、Oracle、SQL Server等），因此所有SQL语句必须使用标准SQL语法，避免使用特定数据库的方言。

## 禁止使用的MySQL方言

### 1. find_in_set() 函数
❌ 错误写法：
```sql
WHERE FIND_IN_SET(#{deptId}, ancestors)
```

✅ 正确写法：
```sql
WHERE ancestors LIKE CONCAT('%,', #{deptId}, ',%')
```

### 2. 布尔字面量
❌ 错误写法：
```sql
WHERE del_flag = false
WHERE status = true
```

✅ 正确写法：
```sql
WHERE del_flag = 0
WHERE status = 1
```

注意：数据库字段类型使用 BIT(1)，Java实体类使用 Boolean 类型，MyBatis会自动转换。

### 3. 反引号标识符
❌ 错误写法：
```sql
WHERE `key` = #{key}
WHERE `order` = #{order}
```

✅ 正确写法：
```sql
WHERE key = #{key}
WHERE "order" = #{order}  -- 如果是保留字，使用双引号（标准SQL）
```

## 其他需要注意的兼容性问题

### 1. 日期函数
- 避免使用 `DATE_FORMAT()`、`STR_TO_DATE()`
- 使用标准SQL的 `CAST()` 或 `TO_DATE()`

### 2. 字符串连接
- 避免使用 `CONCAT_WS()`、`GROUP_CONCAT()`
- 使用标准SQL的 `CONCAT()` 或 `||` 运算符

### 3. 条件函数
- 避免使用 `IFNULL()`、`IF()`
- 使用标准SQL的 `COALESCE()` 或 `CASE WHEN`

### 4. 分页查询
- 避免使用 `LIMIT offset, count`
- 使用 MyBatis-Plus 的分页插件自动处理

### 5. 自增主键
- 不在SQL中使用 `AUTO_INCREMENT`
- 使用 MyBatis-Plus 的 `@TableId(type = IdType.AUTO)` 注解

## 已修复的文件清单

1. `SysUserMapper.xml` - find_in_set → LIKE CONCAT，false → 0
2. `SysDictMapper.xml` - find_in_set → LIKE CONCAT，false → 0，反引号移除
3. `SysDeptMapper.xml` - find_in_set → LIKE CONCAT，false → 0
4. `SysRoleMapper.xml` - false → 0
5. `SysUserRoleMapper.xml` - false → 0
6. `DataScopeEnum.java` - find_in_set → LIKE CONCAT

## 测试建议

在不同数据库上进行测试：
- MySQL 5.7+
- PostgreSQL 12+
- Oracle 12c+
- SQL Server 2017+

确保所有SQL语句在各数据库上都能正常执行。
