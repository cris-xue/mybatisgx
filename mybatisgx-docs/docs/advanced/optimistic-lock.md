---
sidebar_position: 2
---

# 乐观锁

> @Version 注解使用指南

## 概述

乐观锁通过版本号机制解决并发更新问题，在更新时检查版本号是否匹配，防止数据覆盖。

## 基本用法

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    // 乐观锁版本号
    @Version
    private Integer version;
}
```

### 数据库结构

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    version INT DEFAULT 0  -- 版本号，初始值为 0
);
```

### 使用效果

```java
// 查询用户（version = 0）
User user = userDao.findById(1L);

// 更新用户
user.setName("新名字");
userDao.updateById(user);

// 实际执行的 SQL
UPDATE user SET name = '新名字', age = 25, version = 1
WHERE id = 1 AND version = 0;

// 如果 version 不匹配，更新行数为 0，表示并发冲突
```

## 工作原理

```
┌─────────────────────────────────────────────────────────────────┐
│                     乐观锁工作流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 查询数据                                                    │
│     SELECT * FROM user WHERE id = 1                             │
│     返回: { id: 1, name: '张三', version: 0 }                   │
│                                                                 │
│  2. 更新数据（自动版本号 +1）                                    │
│     UPDATE user SET name = '李四', version = 1                  │
│     WHERE id = 1 AND version = 0                                │
│                                                                 │
│  3. 检查更新结果                                                │
│     - 更新成功：返回行数 = 1                                     │
│     - 版本冲突：返回行数 = 0（version 已被其他事务修改）          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## @Version 属性

```java
@Version
private Integer version;
```

该注解标记字段为乐观锁版本号字段。

## 版本号类型

### Integer 类型（推荐）

```java
@Version
private Integer version;  // 初始值 0，每次更新 +1
```

### Long 类型

```java
@Version
private Long version;  // 初始值 0，每次更新 +1
```

## 并发冲突处理

### 检测冲突

```java
User user = userDao.findById(1L);
user.setName("新名字");

int rows = userDao.updateById(user);
if (rows == 0) {
    // 版本冲突，更新失败
    throw new OptimisticLockException("数据已被其他用户修改，请刷新后重试");
}
```

### 重试机制

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Retryable(value = OptimisticLockException.class, maxAttempts = 3)
    public void updateUserName(Long id, String newName) {
        User user = userDao.findById(id);
        user.setName(newName);

        int rows = userDao.updateById(user);
        if (rows == 0) {
            throw new OptimisticLockException("乐观锁冲突");
        }
    }
}
```

## 完整示例

```java
// 用户实体
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    @Version
    private Integer version;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// Service 使用
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean updateUser(Long id, String newName, Integer newAge) {
        User user = userDao.findById(id);
        if (user == null) {
            return false;
        }

        user.setName(newName);
        user.setAge(newAge);

        int rows = userDao.updateById(user);
        return rows > 0;  // 返回是否更新成功
    }

    public void updateWithRetry(Long id, String newName) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            User user = userDao.findById(id);
            user.setName(newName);

            int rows = userDao.updateById(user);
            if (rows > 0) {
                return;  // 更新成功
            }
            // 版本冲突，重试
        }
        throw new RuntimeException("更新失败，请稍后重试");
    }
}
```

## 与其他功能配合

### 与逻辑删除配合

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @LogicDelete
    private Integer status;

    @Version
    private Integer version;
}

// 删除时也会检查版本号
// UPDATE user SET status = 1, version = version + 1
// WHERE id = 1 AND version = 0 AND status = 0
```

### 与字段值生成配合

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    @Version
    private Integer version;
}
```

## 注意事项

1. **每个实体只能有一个版本号字段**：多个 `@Version` 注解会导致异常

2. **版本号初始值**：建议初始值为 0 或 null（自动填充为 0）

3. **更新返回值检查**：务必检查更新返回的行数，判断是否冲突

4. **批量更新不适用**：乐观锁不适用于批量更新场景

5. **new 对象更新**：如果 new 一个新对象设置 id 和 version 进行更新，确保 version 正确

## 下一步

- 学习 [字段值生成](/docs/advanced/value-generation)
- 了解 [数据审计](/docs/advanced/audit)
