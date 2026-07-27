-- =============================================
-- Luma 权限管理系统 - 删除重置密码独立权限
-- 版本: 1.0.0
-- 说明: 删除 system:user:resetPwd 权限，统一使用 system:user:edit
-- =============================================

-- 删除重置密码权限按钮（ID=1015）
DELETE FROM sys_menu WHERE id = 1015 AND perms = 'system:user:resetPwd';

-- 删除角色菜单关联中的重置密码权限
DELETE FROM sys_role_menu WHERE menu_id = 1015;

-- =============================================
-- 说明
-- =============================================
-- 1. 重置密码功能本质上是编辑用户信息的一种操作
-- 2. 使用 system:user:edit 权限即可控制重置密码功能
-- 3. 简化权限体系，避免权限冗余
-- 4. 后端接口已使用 system:user:edit 权限
-- 5. 前端已修改为使用 system:user:edit 权限

-- =============================================
-- 执行后验证
-- =============================================
-- 查看用户管理的权限按钮
-- SELECT id, title, perms FROM sys_menu WHERE parent_id = 101 AND type = 'F';
-- 
-- 应该看到：
-- 1011 | 用户查询 | system:user:query
-- 1012 | 用户新增 | system:user:add
-- 1013 | 用户修改 | system:user:edit
-- 1014 | 用户删除 | system:user:remove
-- 1016 | 用户导出 | system:user:export
-- 
-- 注意：1015（重置密码）已被删除
