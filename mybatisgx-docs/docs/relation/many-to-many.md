---
sidebar_position: 5
---

# 多对多关联

> @ManyToMany 注解使用指南

## 概述

多对多关联用于两个实体之间 M:N 的关系，需要通过中间表实现，如用户与角色。

## 基本结构

```
┌────────┐         ┌──────────┐         ┌────────┐
│  User  │ ──────▶ │ UserRole │ ◀────── │  Role  │
└────────┘         └──────────┘         └────────┘
                        │
                   中间表（user_role）
                   - user_id
                   - role_id
```

## 单向关联

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 用户的角色列表
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}

@Entity
@Table(name = "role")
public class Role {

    @Id
    private Long id;

    private String name;
}
```

### 数据库结构

```sql
-- user 表
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

-- role 表
CREATE TABLE role (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

-- 中间表
CREATE TABLE user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);
```

## 双向关联

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 用户拥有的角色
    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}

@Entity
@Table(name = "role")
public class Role {

    @Id
    private Long id;

    private String name;

    // 拥有该角色的用户
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}

// 中间表实体（可选）
@Entity
@Table(name = "user_role")
public class UserRole {

    private Long userId;

    private Long roleId;
}
```

## @ManyToMany 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `fetch` | FetchType | 抓取策略 | LAZY |
| `mappedBy` | String | 关系维护方字段名 | "" |

## @JoinTable 属性

```java
@JoinTable(
    name = "user_role",                          // 中间表名
    joinColumns = @JoinColumn(name = "user_id"), // 当前实体的外键列
    inverseJoinColumns = @JoinColumn(name = "role_id") // 关联实体的外键列
)
private List<Role> roleList;
```

| 属性 | 说明 |
|------|------|
| `name` | 中间表名 |
| `joinColumns` | 当前实体在中间表的外键 |
| `inverseJoinColumns` | 关联实体在中间表的外键 |

## 抓取策略

### 立即加载

```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(...)
@Fetch(FetchMode.BATCH)
private List<Role> roleList;
```

### 懒加载

```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(...)
private List<Role> roleList;

// 访问时才查询
User user = userDao.findById(1L);
List<Role> roles = user.getRoleList();  // 触发查询
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

    @ManyToMany(mappedBy = "userList", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}

// 角色实体
@Entity
@Table(name = "role")
public class Role {

    @Id
    private Long id;

    private String code;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

@Repository
public interface RoleDao extends SimpleDao<Role, RoleQuery, Long> {
}

// 使用
User user = userDao.findById(1L);
System.out.println(user.getName());
for (Role role : user.getRoleList()) {
    System.out.println(role.getName());
}
```

## 关系维护

### 维护方

拥有 `@JoinTable` 的一方是关系维护方：

```java
// Role 是关系维护方
@Entity
public class Role {
    @ManyToMany
    @JoinTable(...)  // 有 @JoinTable，是维护方
    private List<User> userList;
}

// User 是被维护方
@Entity
public class User {
    @ManyToMany(mappedBy = "userList")  // mappedBy，是被维护方
    private List<Role> roleList;
}
```

### 注意事项

- 关系维护方负责中间表的数据变更
- 被维护方不直接操作中间表
- 建议双方都配置关联，便于双向查询

## 注意事项

1. **中间表必需**：多对多关联必须有中间表

2. **性能考虑**：多对多关联可能产生大量数据，建议使用懒加载

3. **集合初始化**：建议初始化集合

   ```java
   private List<Role> roleList = new ArrayList<>();
   ```

4. **双向关联注意 mappedBy**：一方使用 `@JoinTable`，另一方使用 `mappedBy`

5. **XML 优先**：mapper.xml 定义的方法不会触发自动关联

6. **手动控制数据**：如需从缓存或其他数据源加载关联数据，可使用 `FetchMode.NONE`，详见 [NONE 模式](./fetch-mode#none-模式)

## 下一步

- 学习 [抓取模式](./fetch-mode)
- 了解 [高级功能](../advanced/logic-delete)
