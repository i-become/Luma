-- =============================================
-- Luma 权限管理系统 - 初始化数据脚本
-- 版本: 1.0.0
-- 说明: 插入系统运行所需的基础数据
-- =============================================

-- =============================================
-- 1. 初始化租户数据
-- =============================================
INSERT INTO sys_tenant (id, name, alias, url, status, del_flag, create_by, create_time) VALUES
(1, '系统租户', 'system', 'http://localhost:2025', 0, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 2. 初始化部门数据
-- =============================================
INSERT INTO sys_dept (id, tenant_id, parent_id, ancestors, dept_name, sort, leader, phone, email, status, del_flag, create_by, create_time) VALUES
(1, 1, 0, ',1,', '总公司', 0, '管理员', '15888888888', 'admin@luma.com', 0, 0, 'system', CURRENT_TIMESTAMP),
(2, 1, 1, ',1,2,', '研发部门', 1, '研发经理', '15888888889', 'dev@luma.com', 0, 0, 'system', CURRENT_TIMESTAMP),
(3, 1, 1, ',1,3,', '市场部门', 2, '市场经理', '15888888890', 'market@luma.com', 0, 0, 'system', CURRENT_TIMESTAMP),
(4, 1, 1, ',1,4,', '财务部门', 3, '财务经理', '15888888891', 'finance@luma.com', 0, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 3. 初始化用户数据
-- 密码: admin123 (使用SM3加密后的值)
-- =============================================
INSERT INTO sys_user (id, tenant_id, dept_id, login_name, nickname, user_type, email, phone, sex, avatar, password, status, del_flag, create_by, create_time) VALUES
(1, 1, 1, 'admin', '超级管理员', 0, 'admin@luma.com', '15888888888', 0, '', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 0, 0, 'system', CURRENT_TIMESTAMP),
(2, 1, 2, 'developer', '开发人员', 0, 'dev@luma.com', '15888888889', 0, '', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 0, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 4. 初始化角色数据
-- =============================================
INSERT INTO sys_role (id, tenant_id, role_name, role_key, sort, data_scope, status, remark, del_flag, create_by, create_time) VALUES
(1, 1, '超级管理员', 'admin', 1, 1, 0, '超级管理员，拥有所有权限', 0, 'system', CURRENT_TIMESTAMP),
(2, 1, '普通角色', 'common', 2, 5, 0, '普通角色，仅拥有基本权限', 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 5. 初始化菜单数据
-- =============================================
-- 一级菜单
INSERT INTO sys_menu (id, parent_id, name, title, type, path, component, icon, sort, hidden, keep_alive, status, create_by, create_time) VALUES
(1, 0, 'System', '系统管理', 0, '/system', 'Layout', 'setting', 1, 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(2, 0, 'Monitor', '系统监控', 0, '/monitor', 'Layout', 'monitor', 2, 0, 1, 0, 'system', CURRENT_TIMESTAMP);

-- 系统管理子菜单
INSERT INTO sys_menu (id, parent_id, name, title, type, path, component, icon, sort, perms, hidden, keep_alive, status, create_by, create_time) VALUES
(101, 1, 'User', '用户管理', 1, '/system/user', 'system/user/index', 'user', 1, 'system:user:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(102, 1, 'Role', '角色管理', 1, '/system/role', 'system/role/index', 'peoples', 2, 'system:role:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(103, 1, 'Menu', '菜单管理', 1, '/system/menu', 'system/menu/index', 'tree-table', 3, 'system:menu:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(104, 1, 'Dept', '部门管理', 1, '/system/dept', 'system/dept/index', 'tree', 4, 'system:dept:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(105, 1, 'Post', '岗位管理', 1, '/system/post', 'system/post/index', 'post', 5, 'system:post:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(106, 1, 'Dict', '字典管理', 1, '/system/dict', 'system/dict/index', 'dict', 6, 'system:dict:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(107, 1, 'Tenant', '租户管理', 1, '/system/tenant', 'system/tenant/index', 'company', 7, 'system:tenant:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP),
(108, 1, 'Client', '应用管理', 1, '/system/client', 'system/client/index', 'link', 8, 'system:client:list', 0, 1, 0, 'system', CURRENT_TIMESTAMP);

-- 用户管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1011, 101, '', '用户查询', 2, 'system:user:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1012, 101, '', '用户新增', 2, 'system:user:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1013, 101, '', '用户修改', 2, 'system:user:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1014, 101, '', '用户删除', 2, 'system:user:remove', 4, 0, 'system', CURRENT_TIMESTAMP),
(1015, 101, '', '用户导出', 2, 'system:user:export', 5, 0, 'system', CURRENT_TIMESTAMP);

-- 角色管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1021, 102, '', '角色查询', 2, 'system:role:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1022, 102, '', '角色新增', 2, 'system:role:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1023, 102, '', '角色修改', 2, 'system:role:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1024, 102, '', '角色删除', 2, 'system:role:remove', 4, 0, 'system', CURRENT_TIMESTAMP),
(1025, 102, '', '角色导出', 2, 'system:role:export', 5, 0, 'system', CURRENT_TIMESTAMP);

-- 菜单管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1031, 103, '', '菜单查询', 2, 'system:menu:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1032, 103, '', '菜单新增', 2, 'system:menu:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1033, 103, '', '菜单修改', 2, 'system:menu:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1034, 103, '', '菜单删除', 2, 'system:menu:remove', 4, 0, 'system', CURRENT_TIMESTAMP);

-- 部门管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1041, 104, '', '部门查询', 2, 'system:dept:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1042, 104, '', '部门新增', 2, 'system:dept:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1043, 104, '', '部门修改', 2, 'system:dept:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1044, 104, '', '部门删除', 2, 'system:dept:remove', 4, 0, 'system', CURRENT_TIMESTAMP);

-- 岗位管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1051, 105, '', '岗位查询', 2, 'system:post:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1052, 105, '', '岗位新增', 2, 'system:post:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1053, 105, '', '岗位修改', 2, 'system:post:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1054, 105, '', '岗位删除', 2, 'system:post:remove', 4, 0, 'system', CURRENT_TIMESTAMP),
(1055, 105, '', '岗位导出', 2, 'system:post:export', 5, 0, 'system', CURRENT_TIMESTAMP);

-- 字典管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1061, 106, '', '字典查询', 2, 'system:dict:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1062, 106, '', '字典新增', 2, 'system:dict:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1063, 106, '', '字典修改', 2, 'system:dict:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1064, 106, '', '字典删除', 2, 'system:dict:remove', 4, 0, 'system', CURRENT_TIMESTAMP),
(1065, 106, '', '字典导出', 2, 'system:dict:export', 5, 0, 'system', CURRENT_TIMESTAMP);

-- 租户管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1071, 107, '', '租户查询', 2, 'system:tenant:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1072, 107, '', '租户新增', 2, 'system:tenant:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1073, 107, '', '租户修改', 2, 'system:tenant:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1074, 107, '', '租户删除', 2, 'system:tenant:remove', 4, 0, 'system', CURRENT_TIMESTAMP);

-- 应用管理按钮
INSERT INTO sys_menu (id, parent_id, name, title, type, perms, sort, status, create_by, create_time) VALUES
(1081, 108, '', '应用查询', 2, 'system:client:query', 1, 0, 'system', CURRENT_TIMESTAMP),
(1082, 108, '', '应用新增', 2, 'system:client:add', 2, 0, 'system', CURRENT_TIMESTAMP),
(1083, 108, '', '应用修改', 2, 'system:client:edit', 3, 0, 'system', CURRENT_TIMESTAMP),
(1084, 108, '', '应用删除', 2, 'system:client:remove', 4, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 6. 初始化岗位数据
-- =============================================
INSERT INTO sys_post (id, tenant_id, code, post_name, sort, status, del_flag, create_by, create_time) VALUES
(1, 1, 'CEO', '董事长', 1, 0, 0, 'system', CURRENT_TIMESTAMP),
(2, 1, 'CTO', '技术总监', 2, 0, 0, 'system', CURRENT_TIMESTAMP),
(3, 1, 'DEV', '开发工程师', 3, 0, 0, 'system', CURRENT_TIMESTAMP),
(4, 1, 'TEST', '测试工程师', 4, 0, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 7. 初始化用户角色关联
-- =============================================
INSERT INTO sys_user_role (user_id, role_id, create_time) VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 2, CURRENT_TIMESTAMP);

-- =============================================
-- 8. 初始化用户岗位关联
-- =============================================
INSERT INTO sys_user_post (user_id, post_id, create_time) VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 3, CURRENT_TIMESTAMP);

-- =============================================
-- 9. 初始化角色菜单关联（超级管理员拥有所有菜单权限）
-- =============================================
INSERT INTO sys_role_menu (role_id, menu_id, create_time)
SELECT 1, id, CURRENT_TIMESTAMP FROM sys_menu;

-- =============================================
-- 10. 初始化字典数据
-- =============================================
-- 用户性别
INSERT INTO sys_dict (id, tenant_id, parent_id, ancestors, name, type, parent_key, `key`, value, status, sys, sort, level, del_flag, create_by, create_time) VALUES
(1, 1, 0, ',1,', '用户性别', 0, NULL, 'sys_user_sex', NULL, 0, 1, 1, 0, 0, 'system', CURRENT_TIMESTAMP),
(2, 1, 1, ',1,2,', '男', 1, 'sys_user_sex', '0', '男', 0, 1, 1, 1, 0, 'system', CURRENT_TIMESTAMP),
(3, 1, 1, ',1,3,', '女', 1, 'sys_user_sex', '1', '女', 0, 1, 2, 1, 0, 'system', CURRENT_TIMESTAMP),
(4, 1, 1, ',1,4,', '未知', 1, 'sys_user_sex', '2', '未知', 0, 1, 3, 1, 0, 'system', CURRENT_TIMESTAMP);

-- 菜单状态
INSERT INTO sys_dict (id, tenant_id, parent_id, ancestors, name, type, parent_key, `key`, value, status, sys, sort, level, del_flag, create_by, create_time) VALUES
(5, 1, 0, ',5,', '菜单状态', 0, NULL, 'sys_menu_status', NULL, 0, 1, 2, 0, 0, 'system', CURRENT_TIMESTAMP),
(6, 1, 5, ',5,6,', '正常', 1, 'sys_menu_status', '0', '正常', 0, 1, 1, 1, 0, 'system', CURRENT_TIMESTAMP),
(7, 1, 5, ',5,7,', '停用', 1, 'sys_menu_status', '1', '停用', 0, 1, 2, 1, 0, 'system', CURRENT_TIMESTAMP);

-- 系统状态
INSERT INTO sys_dict (id, tenant_id, parent_id, ancestors, name, type, parent_key, `key`, value, status, sys, sort, level, del_flag, create_by, create_time) VALUES
(8, 1, 0, ',8,', '系统状态', 0, NULL, 'sys_status', NULL, 0, 1, 3, 0, 0, 'system', CURRENT_TIMESTAMP),
(9, 1, 8, ',8,9,', '正常', 1, 'sys_status', '0', '正常', 0, 1, 1, 1, 0, 'system', CURRENT_TIMESTAMP),
(10, 1, 8, ',8,10,', '停用', 1, 'sys_status', '1', '停用', 0, 1, 2, 1, 0, 'system', CURRENT_TIMESTAMP);

-- 数据范围
INSERT INTO sys_dict (id, tenant_id, parent_id, ancestors, name, type, parent_key, `key`, value, status, sys, sort, level, del_flag, create_by, create_time) VALUES
(11, 1, 0, ',11,', '数据范围', 0, NULL, 'sys_data_scope', NULL, 0, 1, 4, 0, 0, 'system', CURRENT_TIMESTAMP),
(12, 1, 11, ',11,12,', '全部数据权限', 1, 'sys_data_scope', '1', '全部数据权限', 0, 1, 1, 1, 0, 'system', CURRENT_TIMESTAMP),
(13, 1, 11, ',11,13,', '自定义数据权限', 1, 'sys_data_scope', '2', '自定义数据权限', 0, 1, 2, 1, 0, 'system', CURRENT_TIMESTAMP),
(14, 1, 11, ',11,14,', '本部门数据权限', 1, 'sys_data_scope', '3', '本部门数据权限', 0, 1, 3, 1, 0, 'system', CURRENT_TIMESTAMP),
(15, 1, 11, ',11,15,', '本部门及以下数据权限', 1, 'sys_data_scope', '4', '本部门及以下数据权限', 0, 1, 4, 1, 0, 'system', CURRENT_TIMESTAMP),
(16, 1, 11, ',11,16,', '仅本人数据权限', 1, 'sys_data_scope', '5', '仅本人数据权限', 0, 1, 5, 1, 0, 'system', CURRENT_TIMESTAMP);

-- =============================================
-- 说明
-- =============================================
-- 1. 默认管理员账号: admin / admin123
-- 2. 默认开发人员账号: developer / admin123
-- 3. 密码需要使用SM3加密，示例中的密码哈希值仅为占位符
-- 4. 实际部署时请修改默认密码
-- 5. 超级管理员(ID=1)拥有所有菜单权限
-- 6. 租户ID=1为系统租户，不可删除
