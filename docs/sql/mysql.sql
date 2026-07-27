-- =============================================
-- Luma 权限管理系统 - MySQL完整脚本
-- 版本: 1.0.0
-- 兼容: MySQL 5.7+, MariaDB 10.2+
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 表结构
-- =============================================

-- 租户表
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '租户名称',
    `alias` VARCHAR(50) NOT NULL COMMENT '租户别名',
    `url` VARCHAR(255) COMMENT '租户首页',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '租户状态(0正常 1停用)',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_alias` (`alias`),
    KEY `idx_tenant_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 部门表
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
    `ancestors` VARCHAR(500) NOT NULL DEFAULT ',' COMMENT '祖级列表',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `leader` VARCHAR(50) COMMENT '负责人',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '部门状态(0正常 1停用)',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dept_tenant` (`tenant_id`),
    KEY `idx_dept_parent` (`parent_id`),
    KEY `idx_dept_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `dept_id` BIGINT NOT NULL COMMENT '部门ID',
    `login_name` VARCHAR(50) NOT NULL COMMENT '登录账号',
    `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `user_type` TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型(0系统用户 1注册用户)',
    `email` VARCHAR(100) COMMENT '用户邮箱',
    `phone` VARCHAR(20) COMMENT '手机号码',
    `sex` TINYINT COMMENT '用户性别(0男 1女 2未知)',
    `avatar` VARCHAR(255) COMMENT '头像路径',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `salt` VARCHAR(64) COMMENT '盐加密',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态(0正常 1停用)',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `login_date` DATETIME COMMENT '最后登录时间',
    `pwd_update_date` DATETIME COMMENT '密码最后更新时间',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_login_tenant` (`login_name`, `tenant_id`),
    KEY `idx_user_tenant` (`tenant_id`),
    KEY `idx_user_dept` (`dept_id`),
    KEY `idx_user_status` (`status`),
    KEY `idx_user_phone` (`phone`),
    KEY `idx_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `data_scope` TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '角色状态(0正常 1停用)',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key_tenant` (`role_key`, `tenant_id`),
    KEY `idx_role_tenant` (`tenant_id`),
    KEY `idx_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';

-- 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    `name` VARCHAR(100) COMMENT '路由名称',
    `redirect` VARCHAR(255) COMMENT '路由重定向地址',
    `component` VARCHAR(255) COMMENT '视图文件路径',
    `icon` VARCHAR(100) COMMENT '菜单图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `title` VARCHAR(100) NOT NULL COMMENT '菜单标题',
    `target` TINYINT COMMENT '打开方式(0内部 1外部)',
    `active_menu` VARCHAR(255) COMMENT '高亮菜单路径',
    `type` TINYINT NOT NULL DEFAULT 0 COMMENT '菜单类型(0目录 1菜单 2按钮 3外链)',
    `path` VARCHAR(255) COMMENT '路由访问路径',
    `link_url` VARCHAR(500) COMMENT '外链URL',
    `hidden` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否在菜单中隐藏(0显示 1隐藏)',
    `fullscreen` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否全屏显示(0否 1是)',
    `affix` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否固定在标签页(0否 1是)',
    `keep_alive` BIT(1) NOT NULL DEFAULT 1 COMMENT '是否缓存路由(0否 1是)',
    `badge` VARCHAR(20) COMMENT '菜单角标文本',
    `badge_type` VARCHAR(20) COMMENT '菜单角标类型',
    `perms` VARCHAR(200) COMMENT '权限标识',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态(0正常 1停用)',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_menu_parent` (`parent_id`),
    KEY `idx_menu_status` (`status`),
    KEY `idx_menu_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 岗位表
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `code` VARCHAR(64) NOT NULL COMMENT '岗位编码',
    `post_name` VARCHAR(50) NOT NULL COMMENT '岗位名称',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code_tenant` (`code`, `tenant_id`),
    KEY `idx_post_tenant` (`tenant_id`),
    KEY `idx_post_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位信息表';

-- 字典表
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父级ID',
    `ancestors` VARCHAR(500) NOT NULL DEFAULT ',' COMMENT '祖籍列表',
    `name` VARCHAR(100) NOT NULL COMMENT '名称',
    `type` TINYINT NOT NULL DEFAULT 0 COMMENT '字典类型(0分组 1键值)',
    `parent_key` VARCHAR(100) COMMENT '上级节点key',
    `key` VARCHAR(100) COMMENT '字典键',
    `value` VARCHAR(500) COMMENT '字典值',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
    `sys` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否为系统内置(0否 1是)',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `level` INT NOT NULL DEFAULT 0 COMMENT '当前字典所在的层次',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_tenant` (`tenant_id`),
    KEY `idx_dict_parent` (`parent_id`),
    KEY `idx_dict_key` (`key`),
    KEY `idx_dict_type` (`type`),
    KEY `idx_dict_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- 用户岗位关联表
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT NOT NULL COMMENT '岗位ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `post_id`),
    KEY `idx_user_post_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与岗位关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_role_menu_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- 角色部门关联表
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `dept_id` BIGINT NOT NULL COMMENT '部门ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `dept_id`),
    KEY `idx_role_dept_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和部门关联表';

-- 应用表
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '应用ID',
    `name` VARCHAR(100) NOT NULL COMMENT '应用名称',
    `secret` VARCHAR(128) NOT NULL COMMENT '应用密钥',
    `scope` VARCHAR(500) COMMENT '应用签约的所有用户权限',
    `sys_scope` VARCHAR(500) COMMENT '应用签约的所有系统权限',
    `allow_url` VARCHAR(1000) COMMENT '应用允许授权的所有URL',
    `grant_type` VARCHAR(200) COMMENT '应用允许的所有grant_type',
    `is_confirm` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否静默授权(0否 1是)',
    `logout_call` VARCHAR(255) COMMENT '用户注销登录通知地址',
    `push_url` VARCHAR(255) COMMENT '数据推送地址',
    `del_flag` BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_client_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';

-- 应用用户关联表
DROP TABLE IF EXISTS `client_sys_user`;
CREATE TABLE `client_sys_user` (
    `client_id` BIGINT NOT NULL COMMENT '应用ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    PRIMARY KEY (`client_id`, `user_id`),
    KEY `idx_client_user_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用用户关联表';

-- 应用租户关联表
DROP TABLE IF EXISTS `client_sys_tenant`;
CREATE TABLE `client_sys_tenant` (
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `client_id` BIGINT NOT NULL COMMENT '应用ID',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`tenant_id`, `client_id`),
    KEY `idx_client_tenant_client` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用与租户关联表';

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 初始化数据（从init-data.sql导入）
-- =============================================
-- 请执行 init-data.sql 文件插入初始数据
