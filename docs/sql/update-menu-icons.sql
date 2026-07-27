-- =============================================
-- Luma 权限管理系统 - 更新菜单图标脚本
-- 版本: 1.0.0
-- 说明: 将菜单图标名称更新为 Element Plus 图标库的正确名称
-- =============================================

-- 更新一级菜单图标
UPDATE sys_menu SET icon = 'Setting' WHERE id = 1 AND icon = 'setting';
UPDATE sys_menu SET icon = 'Monitor' WHERE id = 2 AND icon = 'monitor';

-- 更新系统管理子菜单图标
UPDATE sys_menu SET icon = 'User' WHERE id = 101 AND icon = 'user';
UPDATE sys_menu SET icon = 'UserFilled' WHERE id = 102 AND icon = 'peoples';
UPDATE sys_menu SET icon = 'Menu' WHERE id = 103 AND icon = 'tree-table';
UPDATE sys_menu SET icon = 'Operation' WHERE id = 104 AND icon = 'tree';
UPDATE sys_menu SET icon = 'Briefcase' WHERE id = 105 AND icon = 'post';
UPDATE sys_menu SET icon = 'Document' WHERE id = 106 AND icon = 'dict';
UPDATE sys_menu SET icon = 'OfficeBuilding' WHERE id = 107 AND icon = 'company';

-- =============================================
-- 图标映射说明
-- =============================================
-- setting       -> Setting         (设置图标)
-- monitor       -> Monitor         (监控图标)
-- user          -> User            (用户图标)
-- peoples       -> UserFilled      (多用户图标)
-- tree-table    -> Menu            (菜单图标)
-- tree          -> Operation       (树形/操作图标)
-- post          -> Briefcase       (岗位/公文包图标)
-- dict          -> Document        (字典/文档图标)
-- company       -> OfficeBuilding  (公司/办公楼图标)

-- =============================================
-- 执行说明
-- =============================================
-- 1. 此脚本将更新 sys_menu 表中的图标字段
-- 2. 仅更新已知的不匹配图标名称
-- 3. 执行前请备份数据库
-- 4. 执行后刷新前端页面即可看到图标正常显示
