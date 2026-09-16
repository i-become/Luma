# 部署 alwaysShow 功能指南

## 问题诊断
前端显示所有菜单的 `alwaysShow` 都是 `null`，说明后端没有正确返回这个字段。

## 解决步骤

### 1. 执行数据库迁移脚本
```bash
# 连接到数据库
mysql -u root -p luma

# 执行迁移脚本
source docs/sql/add-menu-always-show.sql

# 或者直接执行 SQL
ALTER TABLE sys_menu ADD COLUMN always_show TINYINT(1) DEFAULT 0 COMMENT '是否默认展开(仅目录类型有效): 0-否 1-是' AFTER hidden;
UPDATE sys_menu SET always_show = 0 WHERE type = 'M';
```

### 2. 验证数据库字段
```sql
-- 查看表结构，确认 always_show 字段已添加
DESC sys_menu;

-- 查看菜单数据
SELECT id, title, type, always_show FROM sys_menu WHERE type = 'M';
```

### 3. 重新编译后端项目
MapStruct 需要重新生成映射器代码：

```bash
cd Luma
mvn clean compile

# 或者在 IDEA 中
# 1. 点击 Maven 面板
# 2. 点击 "Clean"
# 3. 点击 "Compile"
```

### 4. 重启后端服务
重新启动 Spring Boot 应用。

### 5. 测试功能

#### 5.1 测试菜单列表 API
```bash
# 获取菜单列表，检查返回数据中是否包含 alwaysShow 字段
curl -X GET "http://localhost:2025/api/v1/menu" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

返回数据应该包含 `alwaysShow` 字段：
```json
[
  {
    "id": 1,
    "title": "系统管理",
    "type": "M",
    "alwaysShow": false,
    ...
  }
]
```

#### 5.2 更新菜单的 alwaysShow 值
```sql
-- 将"系统管理"菜单设置为默认展开
UPDATE sys_menu SET always_show = 1 WHERE id = 1 AND type = 'M';
```

#### 5.3 前端测试
1. 清除浏览器缓存
2. 重新登录系统
3. 打开浏览器控制台
4. 查看 "Raw menus from backend:" 日志
5. 展开数组，检查 type='M' 的菜单对象是否有 `alwaysShow` 字段
6. 如果 `alwaysShow: true`，左侧菜单应该默认展开该目录

## 常见问题

### Q1: 数据库字段已添加，但后端返回的数据中还是没有 alwaysShow
**A**: MapStruct 映射器没有重新生成。执行 `mvn clean compile` 重新编译。

### Q2: 后端返回了 alwaysShow，但前端还是显示 null
**A**: 检查前端类型定义是否正确，确保 `MenuItem` 接口包含 `alwaysShow` 字段。

### Q3: 设置了 alwaysShow=true，但左侧菜单还是不展开
**A**: 
1. 检查浏览器控制台，查看 "Default openeds:" 日志
2. 确认路由的 meta.alwaysShow 是否为 true
3. 清除浏览器缓存，重新登录

## 验证清单
- [ ] 数据库表 `sys_menu` 已添加 `always_show` 字段
- [ ] 后端代码已重新编译（MapStruct 映射器已生成）
- [ ] 后端服务已重启
- [ ] API 返回的菜单数据包含 `alwaysShow` 字段
- [ ] 前端类型定义包含 `alwaysShow` 字段
- [ ] 浏览器缓存已清除
- [ ] 重新登录后，控制台显示正确的 `alwaysShow` 值
- [ ] 设置为 true 的目录菜单在左侧默认展开
