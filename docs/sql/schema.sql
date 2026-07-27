-- =============================================
-- Luma 权限管理系统 - 数据库建表脚本
-- 版本: 1.0.0
-- 兼容: MySQL 5.7+, PostgreSQL 12+, Oracle 12c+, SQL Server 2017+
-- =============================================

-- 说明：
-- 1. 本脚本使用标准SQL语法，兼容多种数据库
-- 2. 自增主键：MySQL使用AUTO_INCREMENT，PostgreSQL使用SERIAL，Oracle使用SEQUENCE
-- 3. 布尔类型：使用BIT(1)，MySQL/SQL Server原生支持，PostgreSQL使用BOOLEAN，Oracle使用NUMBER(1)
-- 4. 时间类型：使用DATETIME，PostgreSQL使用TIMESTAMP，Oracle使用TIMESTAMP
-- 5. 文本类型：VARCHAR，Oracle长文本使用CLOB
-- 6. 注释：使用COMMENT语法（MySQL），PostgreSQL使用COMMENT ON，Oracle使用COMMENT ON

-- =============================================
-- 1. 租户表
-- =============================================
CREATE TABLE sys_tenant (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '租户ID',
    name VARCHAR(50) NOT NULL COMMENT '租户名称',
    alias VARCHAR(50) NOT NULL COMMENT '租户别名',
    url VARCHAR(255) COMMENT '租户首页',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '租户状态(0正常 1停用)',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='租户表';

-- =============================================
-- 2. 部门表
-- =============================================
CREATE TABLE sys_dept (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
    ancestors VARCHAR(500) NOT NULL DEFAULT ',' COMMENT '祖级列表',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '部门状态(0正常 1停用)',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='部门表';

-- =============================================
-- 3. 用户表
-- =============================================
CREATE TABLE sys_user (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    login_name VARCHAR(50) NOT NULL COMMENT '登录账号',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    user_type TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型(0系统用户 1注册用户)',
    email VARCHAR(100) COMMENT '用户邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    sex TINYINT COMMENT '用户性别(0男 1女 2未知)',
    avatar VARCHAR(255) COMMENT '头像路径',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    salt VARCHAR(64) COMMENT '盐加密',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态(0正常 1停用)',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_date DATETIME COMMENT '最后登录时间',
    pwd_update_date DATETIME COMMENT '密码最后更新时间',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='用户信息表';

-- =============================================
-- 4. 角色表
-- =============================================
CREATE TABLE sys_role (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    data_scope TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '角色状态(0正常 1停用)',
    remark VARCHAR(500) COMMENT '备注',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='角色信息表';

-- =============================================
-- 5. 菜单表
-- =============================================
CREATE TABLE sys_menu (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    name VARCHAR(100) COMMENT '路由名称',
    redirect VARCHAR(255) COMMENT '路由重定向地址',
    component VARCHAR(255) COMMENT '视图文件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    title VARCHAR(100) NOT NULL COMMENT '菜单标题',
    target TINYINT COMMENT '打开方式(0内部 1外部)',
    active_menu VARCHAR(255) COMMENT '高亮菜单路径',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '菜单类型(0目录 1菜单 2按钮 3外链)',
    path VARCHAR(255) COMMENT '路由访问路径',
    link_url VARCHAR(500) COMMENT '外链URL',
    hidden BIT(1) NOT NULL DEFAULT 0 COMMENT '是否在菜单中隐藏(0显示 1隐藏)',
    fullscreen BIT(1) NOT NULL DEFAULT 0 COMMENT '是否全屏显示(0否 1是)',
    affix BIT(1) NOT NULL DEFAULT 0 COMMENT '是否固定在标签页(0否 1是)',
    keep_alive BIT(1) NOT NULL DEFAULT 1 COMMENT '是否缓存路由(0否 1是)',
    badge VARCHAR(20) COMMENT '菜单角标文本',
    badge_type VARCHAR(20) COMMENT '菜单角标类型',
    perms VARCHAR(200) COMMENT '权限标识',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态(0正常 1停用)',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='菜单权限表';

-- =============================================
-- 6. 岗位表
-- =============================================
CREATE TABLE sys_post (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    code VARCHAR(64) NOT NULL COMMENT '岗位编码',
    post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='岗位信息表';

-- =============================================
-- 7. 字典表
-- =============================================
CREATE TABLE sys_dict (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    parent_id INT NOT NULL DEFAULT 0 COMMENT '父级ID',
    ancestors VARCHAR(500) NOT NULL DEFAULT ',' COMMENT '祖籍列表',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '字典类型(0分组 1键值)',
    parent_key VARCHAR(100) COMMENT '上级节点key',
    `key` VARCHAR(100) COMMENT '字典键',
    value VARCHAR(500) COMMENT '字典值',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
    sys BIT(1) NOT NULL DEFAULT 0 COMMENT '是否为系统内置(0否 1是)',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    level INT NOT NULL DEFAULT 0 COMMENT '当前字典所在的层次',
    remark VARCHAR(500) COMMENT '备注',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='系统字典表';

-- =============================================
-- 8. 用户角色关联表
-- =============================================
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id)
) COMMENT='用户和角色关联表';

-- =============================================
-- 9. 用户岗位关联表
-- =============================================
CREATE TABLE sys_user_post (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, post_id)
) COMMENT='用户与岗位关联表';

-- =============================================
-- 10. 角色菜单关联表
-- =============================================
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id, menu_id)
) COMMENT='角色和菜单关联表';

-- =============================================
-- 11. 角色部门关联表
-- =============================================
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id, dept_id)
) COMMENT='角色和部门关联表';

-- =============================================
-- 12. 应用表
-- =============================================
CREATE TABLE client (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '应用ID',
    name VARCHAR(100) NOT NULL COMMENT '应用名称',
    secret VARCHAR(128) NOT NULL COMMENT '应用密钥',
    scope VARCHAR(500) COMMENT '应用签约的所有用户权限',
    sys_scope VARCHAR(500) COMMENT '应用签约的所有系统权限',
    allow_url VARCHAR(1000) COMMENT '应用允许授权的所有URL',
    grant_type VARCHAR(200) COMMENT '应用允许的所有grant_type',
    is_confirm BIT(1) NOT NULL DEFAULT 0 COMMENT '是否静默授权(0否 1是)',
    logout_call VARCHAR(255) COMMENT '用户注销登录通知地址',
    push_url VARCHAR(255) COMMENT '数据推送地址',
    del_flag BIT(1) NOT NULL DEFAULT 0 COMMENT '删除标志(0存在 1删除)',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间'
) COMMENT='应用表';

-- =============================================
-- 13. 应用用户关联表
-- =============================================
CREATE TABLE client_sys_user (
    client_id BIGINT NOT NULL COMMENT '应用ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    PRIMARY KEY (client_id, user_id)
) COMMENT='应用用户关联表';

-- =============================================
-- 14. 应用租户关联表
-- =============================================
CREATE TABLE client_sys_tenant (
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    client_id BIGINT NOT NULL COMMENT '应用ID',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (tenant_id, client_id)
) COMMENT='应用与租户关联表';

-- =============================================
-- 索引创建
-- =============================================

-- 租户表索引
CREATE INDEX idx_tenant_alias ON sys_tenant(alias);
CREATE INDEX idx_tenant_status ON sys_tenant(status);

-- 部门表索引
CREATE INDEX idx_dept_tenant ON sys_dept(tenant_id);
CREATE INDEX idx_dept_parent ON sys_dept(parent_id);
CREATE INDEX idx_dept_status ON sys_dept(status);

-- 用户表索引
CREATE UNIQUE INDEX uk_user_login_tenant ON sys_user(login_name, tenant_id);
CREATE INDEX idx_user_tenant ON sys_user(tenant_id);
CREATE INDEX idx_user_dept ON sys_user(dept_id);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_phone ON sys_user(phone);
CREATE INDEX idx_user_email ON sys_user(email);

-- 角色表索引
CREATE UNIQUE INDEX uk_role_key_tenant ON sys_role(role_key, tenant_id);
CREATE INDEX idx_role_tenant ON sys_role(tenant_id);
CREATE INDEX idx_role_status ON sys_role(status);

-- 菜单表索引
CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_menu_status ON sys_menu(status);
CREATE INDEX idx_menu_type ON sys_menu(type);

-- 岗位表索引
CREATE UNIQUE INDEX uk_post_code_tenant ON sys_post(code, tenant_id);
CREATE INDEX idx_post_tenant ON sys_post(tenant_id);
CREATE INDEX idx_post_status ON sys_post(status);

-- 字典表索引
CREATE INDEX idx_dict_tenant ON sys_dict(tenant_id);
CREATE INDEX idx_dict_parent ON sys_dict(parent_id);
CREATE INDEX idx_dict_key ON sys_dict(`key`);
CREATE INDEX idx_dict_type ON sys_dict(type);
CREATE INDEX idx_dict_status ON sys_dict(status);

-- 用户角色关联表索引
CREATE INDEX idx_user_role_role ON sys_user_role(role_id);

-- 用户岗位关联表索引
CREATE INDEX idx_user_post_post ON sys_user_post(post_id);

-- 角色菜单关联表索引
CREATE INDEX idx_role_menu_menu ON sys_role_menu(menu_id);

-- 角色部门关联表索引
CREATE INDEX idx_role_dept_dept ON sys_role_dept(dept_id);

-- 应用表索引
CREATE INDEX idx_client_name ON client(name);

-- 应用用户关联表索引
CREATE INDEX idx_client_user_user ON client_sys_user(user_id);

-- 应用租户关联表索引
CREATE INDEX idx_client_tenant_client ON client_sys_tenant(client_id);
