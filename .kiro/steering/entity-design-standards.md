---
inclusion: auto
---

# 实体类设计规范与问题分析

本文档分析项目中实体类的设计问题，并提供改进建议和设计规范。

## 一、发现的设计问题

### 1. 字段命名不一致 ⚠️

**问题描述**：
不同实体类中相同含义的字段使用了不同的命名。

```java
// SysUser.java
private String loginName;  // 登录账号
private String username;   // 用户昵称

// SysRole.java
private String roleName;   // 角色名称

// SysDept.java
private String deptName;   // 部门名称

// SysPost.java
private String postName;   // 岗位名称
```

**问题**：
- `SysUser` 中 `username` 表示昵称，而非登录名
- 其他实体使用 `xxxName` 表示名称，但 `SysUser` 使用 `username` 表示昵称
- 容易混淆，不符合统一命名规范

**建议**：
```java
// SysUser.java 应改为
private String loginName;  // 登录账号
private String nickname;   // 用户昵称（或 displayName）
```

---

### 2. 枚举类设计不统一 ⚠️

**问题描述**：
不同枚举类的设计风格不一致。

```java
// SysStatusEnum - 只有code，没有description
@EnumValue
private final int status;

// MenuTypeEnum - 有code和description
@EnumValue
@JsonValue
private final String code;
private final String description;

// SysDictTypeEnum - 没有@EnumValue注解
public enum SysDictTypeEnum {
    MENU, NUMBER, STRING, ARRAY
}
```

**问题**：
- 枚举字段名不统一（status vs code）
- 有的有描述字段，有的没有
- `SysDictTypeEnum` 缺少 `@EnumValue` 注解，无法正确映射数据库
- `@JsonValue` 使用不一致

**建议**：统一枚举设计模式
```java
@Getter
public enum XxxEnum {
    VALUE1("code1", "描述1"),
    VALUE2("code2", "描述2");
    
    @EnumValue  // 数据库存储值
    @JsonValue  // JSON序列化值
    private final String code;
    
    private final String description;  // 可选，用于显示
    
    XxxEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
```

---

### 3. 布尔字段类型不统一 ⚠️

**问题描述**：
删除标志字段在不同实体中类型不一致。

```java
// SysUser, SysRole, SysDept, SysDict
@TableLogic
private Boolean delFlag;  // 使用Boolean

// SysPost
// 没有delFlag字段

// SysTenant
@TableLogic
private Boolean delFlag;
```

**问题**：
- `SysPost` 缺少软删除标志
- 数据库应使用 `BIT(1)` 类型存储布尔值

**建议**：
- 所有需要软删除的实体都应该有 `delFlag` 字段
- 数据库使用 `BIT(1)` 类型
- Java使用 `Boolean` 类型

---

### 4. 用户类型字段使用Integer ❌

**问题描述**：
```java
// SysUser.java
private Integer userType;  // 用户类型（0系统用户 1注册用户）
```

**问题**：
- 应该使用枚举类型，而非Integer
- 缺乏类型安全
- 代码可读性差

**建议**：
```java
// 创建枚举
public enum UserTypeEnum {
    SYSTEM(0, "系统用户"),
    REGISTERED(1, "注册用户");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String description;
}

// 实体类使用枚举
private UserTypeEnum userType;
```

---

### 5. 敏感信息字段缺少保护 ⚠️

**问题描述**：
```java
// SysUser.java
private String password;  // 密码
private String salt;      // 盐加密
```

**问题**：
- 密码字段在查询时可能被意外返回
- 缺少 `@JsonIgnore` 或 `@TableField(select = false)` 保护

**建议**：
```java
@JsonIgnore  // JSON序列化时忽略
@TableField(select = false)  // 默认查询时不包含
private String password;

@JsonIgnore
@TableField(select = false)
private String salt;
```

---

### 6. 关联表缺少联合主键 ⚠️

**问题描述**：
```java
// SysUserRole, SysRoleMenu, SysRoleDept
@TableName(value ="sys_user_role")
public class SysUserRole {
    private Long userId;
    private Long roleId;
}
```

**问题**：
- 关联表没有定义主键
- 可能导致重复数据
- 查询性能不佳

**建议**：
```java
@TableName(value ="sys_user_role")
public class SysUserRole {
    @TableId(type = IdType.INPUT)
    private Long userId;
    
    @TableId(type = IdType.INPUT)
    private Long roleId;
}

// 或使用复合主键注解
@TableName(value ="sys_user_role")
public class SysUserRole {
    private Long userId;
    private Long roleId;
}
// 在数据库层面定义联合主键
// PRIMARY KEY (user_id, role_id)
```

---

### 7. 字典表设计过于复杂 ⚠️

**问题描述**：
```java
// SysDict.java
private String ancestors;    // 祖籍列表
private String parentKey;    // 上级节点key
private String key;          // 字典键
private Integer level;       // 当前字典所在的层次
private SysDictTypeEnum type; // 字典类型
```

**问题**：
- `ancestors` 和 `parentId` 冗余
- `parentKey` 和 `key` 容易混淆
- `level` 字段可以通过ancestors计算得出
- `type` 字段的枚举值（MENU/NUMBER/STRING/ARRAY）语义不清

**建议**：
- 简化字段，移除冗余
- 重新设计字典类型枚举
- 考虑是否真的需要树形结构

---

### 8. 部门表ancestors字段维护复杂 ⚠️

**问题描述**：
```java
// SysDept.java
private String ancestors;  // 祖级列表
```

**问题**：
- 需要手动维护祖级列表
- 更新父节点时需要递归更新所有子节点
- 容易出现数据不一致

**建议**：
- 使用递归CTE查询代替ancestors字段
- 或使用专门的树形结构库（如nested set）
- 如果保留，需要在Service层严格维护一致性

---

### 9. 缺少索引定义 ⚠️

**问题描述**：
实体类中没有定义数据库索引。

**建议**：
使用 `@TableIndex` 注解定义索引
```java
@TableName(value = "sys_user", indexes = {
    @Index(name = "idx_login_name", value = "login_name", unique = true),
    @Index(name = "idx_dept_id", value = "dept_id"),
    @Index(name = "idx_status", value = "status")
})
public class SysUser extends TenantBaseEntity {
    // ...
}
```

---

### 10. 缺少字段长度限制 ⚠️

**问题描述**：
实体类字段没有定义长度限制。

**建议**：
使用 `@TableField` 注解定义字段属性
```java
@TableField(value = "login_name", jdbcType = JdbcType.VARCHAR)
@Size(max = 50, message = "登录名长度不能超过50个字符")
private String loginName;

@TableField(value = "email", jdbcType = JdbcType.VARCHAR)
@Email(message = "邮箱格式不正确")
@Size(max = 100, message = "邮箱长度不能超过100个字符")
private String email;
```

---

## 二、设计规范

### 1. 实体类命名规范

```java
// 实体类名：使用Sys前缀 + 业务名称
public class SysUser { }
public class SysRole { }

// 关联表：使用两个实体名组合
public class SysUserRole { }
public class SysRoleMenu { }
```

### 2. 字段命名规范

| 字段类型 | 命名规则 | 示例 |
|---------|---------|------|
| 主键 | id | id |
| 外键 | xxxId | userId, deptId |
| 名称 | xxxName 或 name | roleName, deptName |
| 编码 | xxxCode 或 code | roleCode, postCode |
| 状态 | status | status |
| 类型 | xxxType 或 type | userType, menuType |
| 标志 | xxxFlag | delFlag |
| 时间 | xxxTime 或 xxxDate | createTime, loginDate |
| 布尔 | 不使用is前缀 | hidden, enabled |

### 3. 枚举类设计规范

```java
@Getter
public enum XxxEnum {
    
    VALUE1("code1", "描述1"),
    VALUE2("code2", "描述2");
    
    @EnumValue  // 必须：数据库存储值
    @JsonValue  // 推荐：JSON序列化值
    private final String code;  // 或 Integer code
    
    private final String description;  // 可选：用于显示
    
    XxxEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
```

**规则**：
- 所有枚举必须有 `@EnumValue` 注解
- 推荐使用 `@JsonValue` 注解
- 字段名统一使用 `code`
- 提供 `description` 字段用于显示

### 4. 基础实体类继承规范

```java
// 不需要租户隔离的实体
public class XxxEntity extends BaseEntity {
    // BaseEntity包含：createBy, createTime, updateBy, updateTime
}

// 需要租户隔离的实体
public class XxxEntity extends TenantBaseEntity {
    // TenantBaseEntity包含：tenantId + BaseEntity的字段
}

// 关联表（不需要审计字段）
public class XxxRelation implements Serializable {
    // 只包含业务字段
}
```

### 5. 软删除规范

```java
// 所有需要软删除的实体都应该有
@TableLogic
private Boolean delFlag;  // Java使用Boolean

// 数据库定义
del_flag BIT(1) DEFAULT b'0' COMMENT '删除标志(0存在 1删除)'
```

### 6. 敏感字段保护

```java
// 密码等敏感字段
@JsonIgnore  // JSON序列化时忽略
@TableField(select = false)  // 默认查询时不包含
private String password;

@JsonIgnore
@TableField(select = false)
private String salt;
```

### 7. 字段验证注解

```java
@NotBlank(message = "登录名不能为空")
@Size(max = 50, message = "登录名长度不能超过50个字符")
private String loginName;

@Email(message = "邮箱格式不正确")
@Size(max = 100, message = "邮箱长度不能超过100个字符")
private String email;

@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
private String phone;
```

### 8. 关联表设计规范

```java
@TableName(value = "sys_user_role")
public class SysUserRole implements Serializable {
    
    private Long userId;
    private Long roleId;
    
    // 数据库层面定义联合主键
    // PRIMARY KEY (user_id, role_id)
    
    // 可选：添加创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

### 9. 数据库字段类型映射

| Java类型 | 数据库类型 | 说明 |
|---------|-----------|------|
| Long | BIGINT | 主键、外键 |
| Integer | INT | 数字 |
| String | VARCHAR(n) | 字符串 |
| Boolean | BIT(1) | 布尔值 |
| LocalDateTime | DATETIME | 时间 |
| BigDecimal | DECIMAL(p,s) | 金额 |
| 枚举 | CHAR(n) 或 INT | 根据@EnumValue定义 |

### 10. 索引设计建议

```java
// 常见索引场景
- 主键：自动创建
- 外键：手动创建索引
- 状态字段：创建索引（经常用于筛选）
- 唯一字段：创建唯一索引（如loginName）
- 排序字段：创建索引（如sort）
- 联合查询：创建联合索引
```

---

## 三、改进优先级

### 高优先级（必须改进）
1. ✅ 菜单字段改进（已完成）
2. 🔴 SysUser.username 重命名为 nickname
3. 🔴 创建 UserTypeEnum 枚举
4. 🔴 密码字段添加 @JsonIgnore 和 @TableField(select = false)
5. 🔴 统一枚举类设计（添加@EnumValue和@JsonValue）

### 中优先级（建议改进）
6. 🟡 SysPost 添加 delFlag 字段
7. 🟡 关联表添加联合主键约束
8. 🟡 所有布尔字段数据库改为 BIT(1)
9. 🟡 添加字段验证注解

### 低优先级（可选改进）
10. 🟢 简化字典表设计
11. 🟢 优化ancestors字段维护
12. 🟢 添加索引定义注解

---

## 四、检查清单

在创建或修改实体类时，请检查：

- [ ] 继承了正确的基类（BaseEntity 或 TenantBaseEntity）
- [ ] 字段命名符合规范（不使用is前缀）
- [ ] 枚举类型有 @EnumValue 注解
- [ ] 敏感字段有 @JsonIgnore 保护
- [ ] 软删除字段使用 @TableLogic
- [ ] 布尔字段数据库使用 BIT(1)
- [ ] 添加了必要的验证注解
- [ ] 关联表定义了联合主键
- [ ] 字段注释完整清晰
- [ ] 序列化字段定义正确

---

**最后更新**: 2026-03-02  
**维护者**: 开发团队  
**适用范围**: 所有实体类设计
