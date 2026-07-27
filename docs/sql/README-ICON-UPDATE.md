# 菜单图标更新说明

## 问题描述
数据库中的菜单图标名称与 Element Plus 图标库不匹配，导致前端无法正常显示图标。

## 原因分析
- 数据库使用的是小写或连字符格式：`setting`, `monitor`, `user`, `peoples`, `tree-table`, `tree`, `post`, `dict`, `company`
- Element Plus 图标库使用 PascalCase 格式：`Setting`, `Monitor`, `User`, `UserFilled`, `Menu`, `Operation`, `Briefcase`, `Document`, `OfficeBuilding`

## 解决方案
执行 `update-menu-icons.sql` 脚本更新数据库中的图标名称。

## 执行步骤

### 1. 备份数据库（重要！）
```bash
# 使用 mysqldump 备份
mysqldump -u root -p luma > luma_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 2. 执行更新脚本
```bash
# 方式1: 使用 mysql 命令行
mysql -u root -p luma < docs/sql/update-menu-icons.sql

# 方式2: 使用 MySQL Workbench 或其他数据库工具
# 打开 update-menu-icons.sql 文件并执行
```

### 3. 验证更新结果
```sql
-- 查询更新后的图标
SELECT id, title, icon FROM sys_menu WHERE icon IS NOT NULL ORDER BY id;
```

### 4. 刷新前端页面
- 清除浏览器缓存
- 重新登录系统
- 查看菜单管理页面，图标应该正常显示

## 图标映射表

| 原图标名称 | 新图标名称 | 说明 |
|-----------|-----------|------|
| setting | Setting | 设置图标 |
| monitor | Monitor | 监控图标 |
| user | User | 用户图标 |
| peoples | UserFilled | 多用户图标 |
| tree-table | Menu | 菜单图标 |
| tree | Operation | 树形/操作图标 |
| post | Briefcase | 岗位/公文包图标 |
| dict | Document | 字典/文档图标 |
| company | OfficeBuilding | 公司/办公楼图标 |

## 注意事项
1. 执行前务必备份数据库
2. 确保数据库连接正常
3. 如果有自定义菜单使用了其他图标，需要手动更新
4. 所有 Element Plus 可用图标列表：https://element-plus.org/zh-CN/component/icon.html

## 回滚方案
如果更新后出现问题，可以使用备份恢复：
```bash
mysql -u root -p luma < luma_backup_YYYYMMDD_HHMMSS.sql
```
