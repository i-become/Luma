---
inclusion: auto
---

# 权限体系设计分析

本文档分析项目的权限体系设计，包括RBAC模型、数据权限、功能权限等方面。

## 一、权限体系架构

### 1.1 整体架构

```
用户(User) ──┬── 角色(Role) ──┬── 菜单权限(Menu)
             │                 └── 数据权限(DataScope)
             ├── 部门(Dept)
             └── 岗位(Post)
```

### 1.2 核心组件

| 组件 | 说明 | 实现类 |
|------|------|--------|
| 认证框架 | Sa-Token | StpUtil |
| 权限接口 | 权限获取 | IStpInterface |
| 功能权限 | 接口级权限控制 | @SaCheckPermission |
| 数据权限 | 数据级权限控制 | @DataScope |
| 多租户 | 租户隔离 | TenantContextHolder |

---

## 二、RBAC模型分析

### 2.1 模型设计 ✅

采用标准的RBAC（Role-Based Access Control）模型：

```
用户(User) ←→ 用户角色关联(UserRole) ←→ 角色(Role) ←→ 角色菜单关联(RoleMenu) ←→ 菜单(Menu)
```

**优点**：
- 标准的RBAC模型，易于理解和维护
- 支持一个用户多个角色
- 支持一个角色多个菜单权限
- 权限粒度细（到按钮级）

### 2.2 关联表设计

#### 用户角色关联 (sys_user_role)
```java
- userId: 用户ID
- roleId: 角色ID
- createTime: 创建时间 ✅
```

#### 角色菜单关联 (sys_role_menu)
```java
- roleId: 角色ID
- menuId: 菜单ID
- createTime: 创建时间 ✅
```

#### 角色部门关联 (sys_role_dept)
```java
- roleId: 角色ID
- deptId: 部门ID
- createTime: 创建时间 ✅
```

**问题**：
- ⚠️ 缺少联合主键约束（数据库层面）
- ⚠️ 缺少唯一索引，可能导致重复数据

---

## 三、功能权限设计

### 3.1 权限标识设计 ✅

采用分层命名规范：
```
模块:资源:操作

示例：
- system:user:query   (系统-用户-查询)
- system:user:add     (系统-用户-添加)
- system:user:edit    (系统-用户-编辑)
- system:user:remove  (系统-用户-删除)
- system:role:query   (系统-角色-查询)
```

**优点**：
- 命名规范清晰
- 易于管理和理解
- 支持通配符匹配

### 3.2 权限注解使用 ✅

```java
@GetMapping
@SaCheckPermission("system:user:query")
public IPage<SysUserPageResp> page(SysUserPageReq req) {
    return sysUserService.page(req);
}
```

**优点**：
- 声明式权限控制
- 代码清晰易读
- 与业务逻辑分离

### 3.3 权限缓存机制 ✅

```java
@Cacheable(cacheNames = "luma:role:perms", key = "#id")
public List<String> getRolePermissionListByRoleId(Long id) {
    return sysRoleMenuMapper.selectRolePermissionListByRoleId(id);
}
```

**优点**：
- 使用Redis缓存提高性能
- 缓存失效策略合理（增删改时清除）

---

## 四、数据权限设计

### 4.1 数据权限范围 ✅

```java
public enum DataScopeEnum {
    ALL(1),              // 全部数据权限
    CUSTOM(2),           // 自定义数据权限
    DEPT_AND_CHILD(3),   // 本部门及子部门数据
    DEPT(4),             // 本部门数据
    SELF(5),             // 仅本人数据
    NONE(6)              // 无数据权限
}
```

**优点**：
- 权限范围层次清晰
- 覆盖常见业务场景
- 支持自定义部门权限

### 4.2 数据权限实现 ✅

使用AOP切面 + SQL动态拼接：

```java
@DataScope(deptAlias = "d", deptIdColumnName = "id")
public IPage<SysUserPageResp> page(SysUserPageReq req) {
    return baseMapper.selectUserPage(req.toMpPage(), req);
}
```

**实现原理**：
1. 切面拦截带有@DataScope注解的方法
2. 根据用户角色的数据权限范围生成SQL条件
3. 通过ThreadLocal传递SQL片段
4. MyBatis-Plus拦截器自动拼接到查询SQL

**优点**：
- 对业务代码侵入小
- 灵活可配置
- 支持多角色权限合并

### 4.3 权限SQL生成 ✅

```java
// 全部数据
AND ( 1 = 1 )

// 本部门及子部门
OR d.dept_id IN ( SELECT id FROM sys_dept WHERE id = 10 or find_in_set(10, ancestors) )

// 本部门
OR d.dept_id = 10

// 仅本人
OR u.user_id = 123

// 自定义部门
OR d.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in (1,2,3) )
```

**优点**：
- SQL生成逻辑清晰
- 支持多种权限范围组合
- 性能优化（使用索引）

---

## 五、问题解决方案

### 5.1 已解决的问题 ✅

#### 1. 菜单权限标识为空问题 ✅

**解决方案**：在Service层添加业务校验

```java
private void validateMenuPermission(MenuTypeEnum type, String perms) {
    // 按钮类型必须有权限标识
    if (type == MenuTypeEnum.BUTTON && (perms == null || perms.trim().isEmpty())) {
        throw new IllegalArgumentException("按钮类型的菜单必须设置权限标识");
    }
    
    // 外链类型不应该有权限标识
    if (type == MenuTypeEnum.LINK && perms != null && !perms.trim().isEmpty()) {
        throw new IllegalArgumentException("外链类型的菜单不应该设置权限标识");
    }
}
```

**优点**：
- 在数据入库前进行校验
- 提供清晰的错误提示
- 保证数据一致性

#### 2. ancestors字段维护复杂问题 ⚠️

**当前方案**：手动维护ancestors字段

**说明**：
- 项目已有完善的ancestors维护逻辑
- 在添加/编辑部门时自动更新ancestors
- 使用 `updateDeptAncestors` 批量更新子部门

**优点**：
- 查询性能好（直接使用LIKE查询）
- 不依赖数据库版本
- 逻辑清晰可控

**注意事项**：
- 更新父部门时需要递归更新所有子部门
- 需要在事务中保证数据一致性
- 删除部门时需要同时删除子部门

**数据库层面优化建议（可选）**：
```sql
-- 如果使用MySQL 8.0+，可以考虑使用递归CTE查询
WITH RECURSIVE dept_tree AS (
    SELECT id, parent_id, dept_name, 0 as level
    FROM sys_dept
    WHERE id = #{deptId}
    
    UNION ALL
    
    SELECT d.id, d.parent_id, d.dept_name, dt.level + 1
    FROM sys_dept d
    INNER JOIN dept_tree dt ON d.parent_id = dt.id
)
SELECT * FROM dept_tree;
```

#### 3. 超级管理员硬编码问题 ✅

**解决方案**：配置化管理

**1. 创建配置类**：
```java
@Data
@Component
@ConfigurationProperties(prefix = "luma.system")
public class SystemConfig {
    private Long adminRoleId = 1L;
    private Long adminUserId = 1L;
    private Long systemTenantId = 1L;
    private Boolean multiTenantEnabled = true;
    private Boolean dataScopeEnabled = true;
}
```

**2. 更新UserUtil**：
```java
@Component
public class UserUtil {
    private static SystemConfig systemConfig;
    
    public static Long getAdminRoleId() {
        return systemConfig.getAdminRoleId();
    }
}
```

**3. 配置文件**：
```yaml
luma:
  system:
    admin-role-id: 1
    admin-user-id: 1
    system-tenant-id: 1
    multi-tenant-enabled: true
    data-scope-enabled: true
```

**优点**：
- 灵活配置，无需修改代码
- 支持不同环境不同配置
- 多租户场景下更安全
- 便于测试和维护

#### 4. 岗位功能说明 ✅

**结论**：岗位(Post)不参与权限体系

岗位主要用于：
- 组织架构管理
- 人员职位标识
- 业务流程中的角色标识
- 与权限体系解耦，保持灵活性

---

## 六、数据库优化建议

### 6.1 关联表添加联合主键

```sql
-- 用户角色关联表
ALTER TABLE sys_user_role ADD PRIMARY KEY (user_id, role_id);
ALTER TABLE sys_user_role ADD INDEX idx_create_time (create_time);

-- 角色菜单关联表
ALTER TABLE sys_role_menu ADD PRIMARY KEY (role_id, menu_id);
ALTER TABLE sys_role_menu ADD INDEX idx_create_time (create_time);

-- 角色部门关联表
ALTER TABLE sys_role_dept ADD PRIMARY KEY (role_id, dept_id);
ALTER TABLE sys_role_dept ADD INDEX idx_create_time (create_time);

-- 用户岗位关联表
ALTER TABLE sys_user_post ADD PRIMARY KEY (user_id, post_id);
ALTER TABLE sys_user_post ADD INDEX idx_create_time (create_time);
```

### 6.2 菜单表优化

```sql
-- 按钮类型的菜单权限标识不能为空
ALTER TABLE sys_menu ADD CONSTRAINT chk_button_perms 
CHECK (type != 'F' OR (perms IS NOT NULL AND perms != ''));

-- 添加索引优化查询
ALTER TABLE sys_menu ADD INDEX idx_type_status (type, status);
ALTER TABLE sys_menu ADD INDEX idx_parent_id (parent_id);
```

### 6.3 部门表优化（可选）

```sql
-- 如果使用MySQL 8.0+，可以考虑使用生成列
ALTER TABLE sys_dept ADD COLUMN level INT GENERATED ALWAYS AS (
    (LENGTH(ancestors) - LENGTH(REPLACE(ancestors, ',', '')) - 1)
) STORED;

-- 添加索引
ALTER TABLE sys_dept ADD INDEX idx_ancestors (ancestors(100));
ALTER TABLE sys_dept ADD INDEX idx_level (level);
```

---

## 七、改进后的优势

### 7.1 数据完整性 ✅
- 关联表联合主键防止重复数据
- 菜单权限标识业务校验
- 数据库约束保证一致性

### 7.2 可维护性 ✅
- TreeUtil工具类简化树形结构操作
- 配置化管理提高灵活性
- 清晰的错误提示便于调试

### 7.3 扩展性 ✅
- 配置化支持多环境部署
- 工具类支持复杂业务场景
- 预留优化空间（递归CTE）

### 7.4 性能优化 ✅
- 联合主键提高查询效率
- 索引优化常用查询
- 缓存机制减少数据库压力

---

## 八、后续优化建议

### 8.1 短期优化（1-2周）
1. ✅ 执行数据库优化SQL
2. ✅ 更新配置文件
3. ✅ 测试权限功能
4. 🔲 添加单元测试

### 8.2 中期优化（1-2月）
1. 🔲 实现权限审计日志
2. 🔲 优化权限缓存策略
3. 🔲 添加权限管理界面
4. 🔲 完善文档和示例

### 8.3 长期优化（3-6月）
1. 🔲 考虑使用递归CTE替代ancestors
2. 🔲 实现细粒度的字段级权限
3. 🔲 支持动态权限配置
4. 🔲 权限性能监控和优化

---

### 5.1 高优先级问题 🔴

#### 1. 关联表缺少数据库约束 ❌

**问题**：
```java
// sys_user_role, sys_role_menu, sys_role_dept 等关联表
// 没有定义联合主键或唯一索引
```

**风险**：
- 可能插入重复数据
- 数据一致性无法保证
- 查询性能不佳

**建议**：
```sql
-- 添加联合主键
ALTER TABLE sys_user_role ADD PRIMARY KEY (user_id, role_id);
ALTER TABLE sys_role_menu ADD PRIMARY KEY (role_id, menu_id);
ALTER TABLE sys_role_dept ADD PRIMARY KEY (role_id, dept_id);
ALTER TABLE sys_user_post ADD PRIMARY KEY (user_id, post_id);

-- 或添加唯一索引
CREATE UNIQUE INDEX uk_user_role ON sys_user_role(user_id, role_id);
```

#### 2. 菜单权限标识可能为空 ⚠️

**问题**：
```java
// SysMenu.perms 字段可以为null
// 但在权限判断时没有充分处理空值情况
```

**建议**：
- 按钮类型的菜单必须有权限标识
- 添加数据库约束或业务校验

#### 3. 数据权限的ancestors字段维护复杂 ⚠️

**问题**：
```java
// SysDept.ancestors 需要手动维护
// 更新父部门时需要递归更新所有子部门
```

**风险**：
- 容易出现数据不一致
- 维护成本高

**建议**：
- 使用递归CTE查询代替ancestors
- 或使用触发器自动维护

### 5.2 中优先级问题 🟡

#### 3. ancestors字段维护需要注意 ⚠️

**当前实现**：
- 已有完善的维护逻辑
- 在Service层自动处理

**注意事项**：
- 更新父部门时需要递归更新子部门
- 必须在事务中执行
- 删除操作需要级联处理

**建议**：
- 保持现有实现
- 确保事务完整性
- 添加单元测试验证

#### 4. 权限缓存粒度可优化 ⚠️

**问题**：
```java
// 当前按角色ID缓存权限
// 用户权限变更时需要清除多个缓存
```

**建议**：
- 考虑按用户ID缓存完整权限
- 或使用缓存标签统一管理

#### 5. 岗位(Post)功能未完全实现 ⚠️

**问题**：
- 有 sys_user_post 关联表
- 但岗位在权限体系中的作用不明确
- 没有岗位相关的权限控制

**建议**：
- 明确岗位在权限体系中的定位
- 或考虑移除岗位功能

#### 6. 超级管理员硬编码 ⚠️

**问题**：
```java
public static final Long ADMIN_ROLE_ID = 1L;
```

**风险**：
- 硬编码不灵活
- 多租户场景下可能冲突

**建议**：
- 使用配置文件或数据库配置
- 或使用特殊标识字段

### 5.3 低优先级问题 🟢

#### 7. 缺少权限审计日志 ⚠️

**建议**：
- 记录权限变更历史
- 记录敏感操作日志

#### 8. 缺少权限测试用例 ⚠️

**建议**：
- 添加权限控制的单元测试
- 添加数据权限的集成测试

---

## 六、设计优点总结

### 6.1 架构设计 ✅

1. **标准RBAC模型**：易于理解和扩展
2. **功能权限与数据权限分离**：职责清晰
3. **多租户支持**：良好的租户隔离
4. **缓存机制**：提高性能

### 6.2 代码实现 ✅

1. **声明式权限控制**：代码清晰
2. **AOP切面**：低侵入性
3. **枚举类型**：类型安全
4. **权限标识规范**：命名清晰

### 6.3 扩展性 ✅

1. **支持多角色**：灵活的权限组合
2. **支持自定义数据权限**：满足复杂业务
3. **支持权限继承**：通过部门树实现

---

## 七、改进建议优先级

### 高优先级（必须改进）
1. 🔴 添加关联表的联合主键或唯一索引
2. 🔴 按钮菜单的权限标识必填校验
3. 🔴 优化ancestors字段维护机制

### 中优先级（建议改进）
4. 🟡 优化权限缓存策略
5. 🟡 明确岗位功能定位
6. 🟡 超级管理员配置化

### 低优先级（可选改进）
7. 🟢 添加权限审计日志
8. 🟢 添加权限测试用例
9. 🟢 添加权限管理界面的操作日志

---

## 八、权限体系最佳实践

### 8.1 权限设计原则

1. **最小权限原则**：默认无权限，显式授权
2. **职责分离**：功能权限与数据权限分离
3. **权限继承**：通过角色和部门实现
4. **审计追踪**：记录权限变更历史

### 8.2 权限命名规范

```
模块:资源:操作

- 模块：system, business, report 等
- 资源：user, role, dept, order 等
- 操作：query, add, edit, remove, export, import 等
```

### 8.3 数据权限使用建议

```java
// 1. 明确指定表别名
@DataScope(deptAlias = "d", userAlias = "u")

// 2. 指定字段名（如果不是默认的dept_id）
@DataScope(deptIdColumnName = "department_id")

// 3. 指定权限标识（如果不使用接口权限）
@DataScope(permission = "system:user:query")

// 4. 禁用自动SQL（手动处理）
@DataScope(autoSql = false)
```

---

## 九、检查清单

在开发权限相关功能时，请检查：

- [ ] 接口是否添加了 @SaCheckPermission 注解
- [ ] 权限标识是否符合命名规范
- [ ] 是否需要数据权限控制
- [ ] 数据权限注解参数是否正确
- [ ] 关联表是否有唯一约束
- [ ] 权限变更是否清除缓存
- [ ] 是否考虑多租户隔离
- [ ] 是否记录操作日志

---

**最后更新**: 2026-03-02  
**维护者**: 开发团队  
**适用范围**: 所有权限相关开发
