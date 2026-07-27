---
inclusion: auto
---

# RESTful API 设计规范

本文档定义了项目中所有API接口的设计标准和规范，确保接口设计的一致性和可维护性。

## 1. 基本原则

### 1.1 资源导向设计
- API应该围绕**资源**（名词）而非**操作**（动词）设计
- 使用HTTP方法表示操作类型
- URL路径只包含资源名称，不包含动词

### 1.2 HTTP方法使用规范

| HTTP方法 | 用途 | 幂等性 | 示例 |
|---------|------|--------|------|
| GET | 获取资源 | 是 | `GET /users` 获取用户列表 |
| POST | 创建资源 | 否 | `POST /users` 创建新用户 |
| PUT | 完整更新资源 | 是 | `PUT /users/1` 完整更新用户1 |
| PATCH | 部分更新资源 | 否 | `PATCH /users/1/status` 更新用户状态 |
| DELETE | 删除资源 | 是 | `DELETE /users/1` 删除用户1 |

## 2. URL设计规范

### 2.1 路径命名规则
```
✅ 正确示例：
GET    /users              # 获取用户列表
GET    /users/123          # 获取指定用户
POST   /users              # 创建用户
PUT    /users/123          # 更新用户
DELETE /users/123          # 删除用户
PATCH  /users/123/status   # 更新用户状态

❌ 错误示例：
GET    /getUsers           # 不要在URL中使用动词
POST   /user/create        # 不要使用动词
PUT    /users/123/enable   # 状态操作应该用PATCH更新status
PUT    /users/123/disable  # 同上
```

### 2.2 资源层级关系
```
# 子资源访问
GET /users/123/roles        # 获取用户123的角色列表
GET /roles/456/menus        # 获取角色456的菜单列表
GET /roles/456/depts        # 获取角色456的部门列表

# 当前用户资源使用 /me
GET /users/me               # 获取当前登录用户信息
GET /users/me/roles         # 获取当前用户的角色
GET /users/me/menus         # 获取当前用户的菜单
```

### 2.3 URL格式规范
- 使用小写字母
- 多个单词使用连字符（kebab-case）分隔
- 不使用下划线
- 资源名使用复数形式

```
✅ 正确：/user-profiles, /api-keys
❌ 错误：/user_profiles, /UserProfiles, /apiKeys
```

## 3. 控制器设计规范

### 3.1 基础CRUD操作模板

```java
@RestController
@RequestMapping("/resources")
public class ResourceController {
    
    // 列表查询（支持分页和过滤）
    @GetMapping
    public IPage<ResourceResp> list(ResourceQueryReq req) {
        return resourceService.page(req);
    }
    
    // 获取单个资源
    @GetMapping("/{id}")
    public ResourceResp get(@PathVariable Long id) {
        return resourceService.getById(id);
    }
    
    // 创建资源
    @PostMapping
    public Long create(@Valid @RequestBody ResourceAddReq req) {
        return resourceService.add(req);
    }
    
    // 完整更新资源
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody ResourceUpdateReq req) {
        resourceService.update(id, req);
    }
    
    // 部分更新资源（如状态）
    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
        resourceService.updateStatus(id, status);
    }
    
    // 删除资源
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        resourceService.delete(id);
    }
}
```

### 3.2 状态更新操作

状态更新统一使用 `PATCH /{id}/status` 模式：

```java
// ✅ 正确：使用PATCH更新状态
@PatchMapping("/{id}/status")
public void updateStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
    service.updateStatus(id, status);
}

// ❌ 错误：不要为每个状态创建单独的端点
@PutMapping("/{id}/enable")   // 不推荐
@PutMapping("/{id}/disable")  // 不推荐
```

### 3.3 特殊操作命名

对于无法用标准CRUD表示的操作，使用子资源或动词名词组合：

```java
// 密码重置
@PatchMapping("/{id}/password")
public void resetPassword(@PathVariable Long id, @RequestParam String newPassword)

// 认证相关（特殊情况可以使用动词）
@PostMapping("/auth/login")
@PostMapping("/auth/logout")

// 批量操作
@PostMapping("/users/batch")
@DeleteMapping("/users/batch")
```

## 4. 请求和响应规范

### 4.1 请求体
- POST/PUT/PATCH 使用 `@RequestBody` 接收JSON数据
- 简单参数使用 `@RequestParam`
- 路径参数使用 `@PathVariable`

```java
// 复杂对象用RequestBody
@PostMapping
public Long create(@Valid @RequestBody UserAddReq req)

// 简单参数用RequestParam
@PatchMapping("/{id}/status")
public void updateStatus(@PathVariable Long id, @RequestParam StatusEnum status)

// 查询参数可以用对象接收
@GetMapping
public IPage<UserResp> list(UserQueryReq req)  // Spring自动绑定查询参数
```

### 4.2 响应格式
- 创建操作返回新资源的ID
- 更新/删除操作返回void或成功消息
- 查询操作返回资源对象或列表
- 分页查询返回 `IPage<T>` 类型

### 4.3 HTTP状态码使用

| 状态码 | 含义 | 使用场景 |
|-------|------|---------|
| 200 OK | 成功 | GET、PUT、PATCH成功 |
| 201 Created | 已创建 | POST创建成功 |
| 204 No Content | 无内容 | DELETE成功 |
| 400 Bad Request | 请求错误 | 参数验证失败 |
| 401 Unauthorized | 未认证 | 未登录或token失效 |
| 403 Forbidden | 无权限 | 已登录但权限不足 |
| 404 Not Found | 未找到 | 资源不存在 |
| 500 Internal Server Error | 服务器错误 | 系统异常 |

## 5. 项目中的实际应用

### 5.1 认证接口 (AuthController)
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")   // 登录
    @PostMapping("/logout")  // 登出（使用POST而非GET）
}
```

### 5.2 用户接口 (SysUserController)
```java
@RestController
@RequestMapping("/user")
public class SysUserController {
    @GetMapping                    // 用户列表
    @GetMapping("/{id}")           // 获取指定用户
    @GetMapping("/me")             // 获取当前用户（使用/me）
    @PostMapping                   // 创建用户
    @PutMapping("/{id}")           // 更新用户
    @DeleteMapping("/{id}")        // 删除用户
    @PatchMapping("/{id}/status")  // 更新状态（统一接口）
    @PatchMapping("/{id}/password")// 重置密码
    @GetMapping("/me/roles")       // 当前用户的角色
    @GetMapping("/{id}/roles")     // 指定用户的角色
    @GetMapping("/me/menus")       // 当前用户的菜单
}
```

### 5.3 角色接口 (SysRoleController)
```java
@RestController
@RequestMapping("/role")
public class SysRoleController {
    @GetMapping                    // 角色列表
    @PostMapping                   // 创建角色
    @PutMapping("/{id}")           // 更新角色
    @DeleteMapping("/{id}")        // 删除角色
    @PatchMapping("/{id}/status")  // 更新状态（统一接口）
    @GetMapping("/{id}/menus")     // 角色的菜单
    @GetMapping("/{id}/depts")     // 角色的部门
}
```

### 5.4 字典接口 (SysDictController)
```java
@RestController
@RequestMapping("/dict")
public class SysDictController {
    @GetMapping                    // 获取字典树（使用format参数区分返回格式）
    // ?format=full   返回完整信息
    // ?format=simple 返回简化信息
    @PostMapping                   // 创建字典
    @PutMapping("/{id}")           // 更新字典
    @DeleteMapping("/{id}")        // 删除字典
}
```

## 6. 版本控制

### 6.1 API版本策略
建议在URL中包含版本号：
```
/api/v1/users
/api/v2/users
```

或使用请求头：
```
Accept: application/vnd.api+json; version=1
```

## 7. 查询参数规范

### 7.1 分页参数
```
GET /users?page=1&size=20
```

### 7.2 过滤参数
```
GET /users?status=ACTIVE&deptId=10
```

### 7.3 排序参数
```
GET /users?sort=createTime,desc
```

### 7.4 字段选择
```
GET /users?fields=id,name,email
```

## 8. 安全规范

### 8.1 权限注解
使用 `@SaCheckPermission` 标注需要权限的接口：
```java
@PostMapping
@SaCheckPermission("system:user:add")
public Long add(@Valid @RequestBody SysUserAddReq req)
```

### 8.2 参数验证
- 使用 `@Valid` 验证请求体
- 使用 `@NotNull` 等注解验证参数
- 在类上添加 `@Validated` 启用方法级验证

```java
@Validated
@RestController
@RequestMapping("/user")
public class SysUserController {
    @GetMapping("/{id}")
    public UserResp get(@NotNull @PathVariable Long id) {
        // ...
    }
}
```

## 9. 文档规范

### 9.1 接口注释
每个接口方法必须包含：
- 功能描述
- 参数说明
- 返回值说明

```java
/**
 * 获取用户信息
 * @param id 用户编号
 * @return 用户信息
 */
@GetMapping("/{id}")
public SysUserInfoResp info(@PathVariable Long id)
```

## 10. 检查清单

在创建或修改API时，请检查：

- [ ] URL中只包含名词（资源），不包含动词
- [ ] 使用了正确的HTTP方法（GET/POST/PUT/PATCH/DELETE）
- [ ] 状态更新使用 `PATCH /{id}/status` 而非单独的enable/disable端点
- [ ] 当前用户资源使用 `/me` 路径
- [ ] URL使用小写和连字符，不使用下划线
- [ ] 添加了适当的权限注解
- [ ] 添加了参数验证注解
- [ ] 编写了完整的JavaDoc注释
- [ ] 子资源访问使用 `/{parentId}/{subResource}` 格式
- [ ] 复杂对象使用 `@RequestBody`，简单参数使用 `@RequestParam`

## 11. 常见错误示例

```java
// ❌ 错误：URL中包含动词
@GetMapping("/getUserList")
@PostMapping("/createUser")
@PutMapping("/updateUser")

// ✅ 正确：使用HTTP方法表示操作
@GetMapping
@PostMapping
@PutMapping("/{id}")

// ❌ 错误：为每个状态创建端点
@PutMapping("/{id}/enable")
@PutMapping("/{id}/disable")

// ✅ 正确：统一的状态更新接口
@PatchMapping("/{id}/status")

// ❌ 错误：使用下划线
@PutMapping("/{id}/rest_password")

// ✅ 正确：使用连字符或直接用单词
@PatchMapping("/{id}/password")

// ❌ 错误：当前用户使用/info
@GetMapping("/info")

// ✅ 正确：当前用户使用/me
@GetMapping("/me")
```

---

**最后更新**: 2026-03-02
**维护者**: 开发团队
**适用范围**: 所有后端API开发
