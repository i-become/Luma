# 数据库脚本说明

## 文件说明

- `schema.sql` - 数据库表结构脚本（兼容多种数据库）
- `init-data.sql` - 初始化数据脚本
- `mysql.sql` - MySQL专用完整脚本
- `postgresql.sql` - PostgreSQL专用完整脚本
- `oracle.sql` - Oracle专用完整脚本
- `sqlserver.sql` - SQL Server专用完整脚本

## 快速开始

### 方案1：使用通用脚本（推荐用于快速测试）

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE luma CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行通用建表脚本
mysql -u root -p luma < schema.sql

# 执行初始化数据
mysql -u root -p luma < init-data.sql
```

### 方案2：使用MySQL优化脚本（推荐用于生产环境）

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE luma CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行MySQL专用脚本（包含更多优化）
mysql -u root -p luma < mysql.sql

# 执行初始化数据
mysql -u root -p luma < init-data.sql
```

**两种方案的区别：**
- `schema.sql`：标准SQL语法，可在MySQL直接使用，但缺少MySQL特定优化
- `mysql.sql`：包含MySQL特定优化（ENGINE、字符集、ON UPDATE等），性能更好

### PostgreSQL 12+

```bash
# 创建数据库
createdb -U postgres luma

# 执行脚本
psql -U postgres -d luma -f postgresql.sql
```

### Oracle 12c+

```sql
-- 使用SQL*Plus或SQL Developer执行
@oracle.sql
```

### SQL Server 2017+

```sql
-- 使用SSMS或sqlcmd执行
:r sqlserver.sql
GO
```

## 数据库差异说明

### 1. 自增主键

| 数据库 | 语法 |
|--------|------|
| MySQL | `AUTO_INCREMENT` |
| PostgreSQL | `SERIAL` 或 `BIGSERIAL` |
| Oracle | `GENERATED AS IDENTITY` 或使用 `SEQUENCE` |
| SQL Server | `IDENTITY(1,1)` |

### 2. 布尔类型

| 数据库 | 类型 | 值 |
|--------|------|-----|
| MySQL | `BIT(1)` | 0/1 |
| PostgreSQL | `BOOLEAN` | true/false 或 0/1 |
| Oracle | `NUMBER(1)` | 0/1 |
| SQL Server | `BIT` | 0/1 |

### 3. 时间类型

| 数据库 | 类型 | 默认值 |
|--------|------|--------|
| MySQL | `DATETIME` | `CURRENT_TIMESTAMP` |
| PostgreSQL | `TIMESTAMP` | `CURRENT_TIMESTAMP` |
| Oracle | `TIMESTAMP` | `SYSTIMESTAMP` |
| SQL Server | `DATETIME2` | `GETDATE()` |

### 4. 字符串类型

| 数据库 | 短文本 | 长文本 |
|--------|--------|--------|
| MySQL | `VARCHAR(n)` | `TEXT` |
| PostgreSQL | `VARCHAR(n)` | `TEXT` |
| Oracle | `VARCHAR2(n)` | `CLOB` |
| SQL Server | `NVARCHAR(n)` | `NVARCHAR(MAX)` |

### 5. 注释语法

**MySQL:**
```sql
CREATE TABLE table_name (
    id BIGINT COMMENT '主键'
) COMMENT='表注释';
```

**PostgreSQL:**
```sql
CREATE TABLE table_name (
    id BIGINT
);
COMMENT ON TABLE table_name IS '表注释';
COMMENT ON COLUMN table_name.id IS '主键';
```

**Oracle:**
```sql
CREATE TABLE table_name (
    id NUMBER(19)
);
COMMENT ON TABLE table_name IS '表注释';
COMMENT ON COLUMN table_name.id IS '主键';
```

**SQL Server:**
```sql
CREATE TABLE table_name (
    id BIGINT
);
EXEC sp_addextendedproperty 'MS_Description', '表注释', 'SCHEMA', 'dbo', 'TABLE', 'table_name';
EXEC sp_addextendedproperty 'MS_Description', '主键', 'SCHEMA', 'dbo', 'TABLE', 'table_name', 'COLUMN', 'id';
```

## 索引设计说明

### 1. 主键索引
所有表都有主键索引，使用自增ID。

### 2. 唯一索引
- `sys_user.login_name + tenant_id` - 确保同一租户下登录名唯一
- `sys_role.role_key + tenant_id` - 确保同一租户下角色标识唯一
- `sys_post.code + tenant_id` - 确保同一租户下岗位编码唯一

### 3. 普通索引
- 租户ID索引 - 多租户查询优化
- 父级ID索引 - 树形结构查询优化
- 状态索引 - 状态过滤查询优化
- 外键索引 - 关联查询优化

### 4. 复合索引
关联表使用复合主键，自动创建复合索引。

## 性能优化建议

### 1. 分区表（大数据量场景）

对于用户表、日志表等数据量大的表，建议使用分区：

**MySQL:**
```sql
ALTER TABLE sys_user PARTITION BY RANGE (id) (
    PARTITION p0 VALUES LESS THAN (1000000),
    PARTITION p1 VALUES LESS THAN (2000000),
    PARTITION p2 VALUES LESS THAN MAXVALUE
);
```

**PostgreSQL:**
```sql
CREATE TABLE sys_user_partitioned (
    LIKE sys_user INCLUDING ALL
) PARTITION BY RANGE (id);

CREATE TABLE sys_user_p0 PARTITION OF sys_user_partitioned
    FOR VALUES FROM (0) TO (1000000);
```

### 2. 读写分离

建议配置主从复制，读操作使用从库。

### 3. 缓存策略

- 角色权限缓存：`luma:role:perms:{roleId}`
- 用户角色缓存：`luma:user:roles:{userId}`
- 部门列表缓存：`luma:dept:list`

### 4. 连接池配置

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 数据迁移

### 从MySQL迁移到PostgreSQL

```bash
# 使用pgloader工具
pgloader mysql://user:pass@localhost/luma postgresql://user:pass@localhost/luma
```

### 从MySQL迁移到Oracle

使用Oracle SQL Developer的迁移工具。

## 备份与恢复

### MySQL

```bash
# 备份
mysqldump -u root -p luma > luma_backup.sql

# 恢复
mysql -u root -p luma < luma_backup.sql
```

### PostgreSQL

```bash
# 备份
pg_dump -U postgres luma > luma_backup.sql

# 恢复
psql -U postgres luma < luma_backup.sql
```

## 注意事项

1. **字符集**: 建议使用UTF-8编码
   - MySQL: `utf8mb4`
   - PostgreSQL: `UTF8`
   - Oracle: `AL32UTF8`
   - SQL Server: `UTF-8`

2. **大小写敏感**: 
   - MySQL: 表名在Windows下不区分大小写，Linux下区分
   - PostgreSQL: 默认不区分大小写，但建议使用小写
   - Oracle: 默认转换为大写，建议使用大写或加引号
   - SQL Server: 取决于排序规则设置

3. **保留字**: 
   - `key`, `name`, `type`, `level`, `sort`, `sys` 等字段名在某些数据库中是保留字
   - 建议使用反引号(MySQL)、双引号(PostgreSQL/Oracle)或方括号(SQL Server)包裹

4. **事务隔离级别**: 
   - 建议使用 `READ COMMITTED` 或 `REPEATABLE READ`

5. **密码加密**: 
   - 示例中的密码哈希值仅为占位符
   - 实际使用时需要使用SM3算法加密
   - 默认密码: `admin123`

## 初始账号

| 账号 | 密码 | 角色 | 说明 |
|------|------|------|------|
| admin | admin123 | 超级管理员 | 拥有所有权限 |
| developer | admin123 | 普通角色 | 仅拥有基本权限 |

**重要**: 生产环境部署后请立即修改默认密码！

## 技术支持

如有问题，请查看项目文档或提交Issue。
