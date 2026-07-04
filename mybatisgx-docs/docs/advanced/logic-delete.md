---
sidebar_position: 1
---

# 逻辑删除

> @LogicDelete 注解使用指南

## 概述

逻辑删除将删除操作转换为更新操作，通过标记字段表示数据已被"删除"，而非物理删除。

## 基本用法

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 逻辑删除字段
    @LogicDelete
    private Integer status;  // 0: 正常, 1: 删除
}
```

### 数据库结构

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    status INT DEFAULT 0  -- 0: 正常, 1: 删除
);
```

### 使用效果

```java
// 调用删除方法
userDao.deleteById(1L);

// 实际执行的 SQL
UPDATE user SET status = 1 WHERE id = 1;

// 查询时自动过滤已删除数据
List<User> users = userDao.findList(query);
// 实际执行的 SQL
SELECT * FROM user WHERE status = 0 AND ...;
```

## @LogicDelete 属性

```java
@LogicDelete
private Integer status;
```

该注解标记字段为逻辑删除字段，删除时会将该字段设置为删除标记值。

## 删除标记值

框架默认将逻辑删除字段设置为非空值（通常为 1 或 true）。

### 数值类型

```java
@LogicDelete
private Integer status;  // 删除时设置为 1
```

### 布尔类型

```java
@LogicDelete
private Boolean deleted;  // 删除时设置为 true
```

## 可重复逻辑删除

### 问题场景

当业务允许"删除后重新新增相同数据"时，普通逻辑删除会导致唯一约束冲突：

```sql
-- 用户 A 被逻辑删除
UPDATE user SET status = 1 WHERE name = 'A';

-- 重新新增同名用户
INSERT INTO user (name, status) VALUES ('A', 0);  -- 唯一约束冲突！
```

### 解决方案

使用 `@LogicDeleteId` 配合 `@GeneratedValue` 实现：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @LogicDelete
    private Integer status;

    // 逻辑删除 ID
    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;
}
```

### 工作原理

```sql
-- 表结构增加 logic_delete_id 字段
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    status INT DEFAULT 0,
    logic_delete_id BIGINT,  -- 逻辑删除 ID
    UNIQUE (name, logic_delete_id)  -- 联合唯一约束
);

-- 删除时更新 logic_delete_id
UPDATE user SET status = 1, logic_delete_id = 123 WHERE id = 1;

-- 新增同名用户不会冲突
INSERT INTO user (name, status, logic_delete_id) VALUES ('A', 0, NULL);
-- 联合唯一约束：(A, NULL) ≠ (A, 123)
```

## ValueProcessor 实现

### 逻辑删除 ID 生成器

```java
public class LogicDeleteIdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "logicDeleteId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.DELETE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // 返回逻辑删除 ID（如雪花 ID）
        return SnowflakeIdGenerator.nextId();
    }
}
```

## 查询行为

### 自动过滤已删除数据

```java
// 所有查询自动添加 status = 0 条件
List<User> users = userDao.findList(query);
// SELECT * FROM user WHERE status = 0 AND ...

// 主键查询也会过滤
User user = userDao.findById(1L);
// SELECT * FROM user WHERE id = 1 AND status = 0
```

### 包含已删除数据

如需查询包含已删除的数据，需要在 mapper.xml 中自定义 SQL。

## 完整示例

```java
// 用户实体
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @LogicDelete
    private Integer status;

    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// 使用
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void deleteUser(Long id) {
        // 逻辑删除
        userDao.deleteById(id);
    }

    public List<User> listUsers() {
        // 自动过滤已删除数据
        return userDao.findList(new UserQuery());
    }
}
```

## 注意事项

1. **每个实体只能有一个逻辑删除字段**：多个 `@LogicDelete` 注解会导致异常

2. **唯一约束需特殊处理**：如果业务要求唯一约束，需要配合 `@LogicDeleteId`

3. **物理删除不触发**：如果在 mapper.xml 中定义了物理删除 SQL，会执行物理删除

4. **查询自动过滤**：所有查询都会自动添加未删除条件

## 下一步

- 学习 [乐观锁](/docs/advanced/optimistic-lock)
- 了解 [字段值生成](/docs/advanced/value-generation)
