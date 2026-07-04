---
sidebar_position: 2
---

# 一对一关联

> @OneToOne 注解使用指南

## 概述

一对一关联用于两个实体之间 1:1 的关系，如用户与用户详情。

## 单向关联

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 用户详情（单向）
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    private UserDetail userDetail;
}

@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    private String phone;

    private String address;
}
```

### 数据库结构

```sql
-- user 表
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    detail_id BIGINT  -- 外键
);

-- user_detail 表
CREATE TABLE user_detail (
    id BIGINT PRIMARY KEY,
    phone VARCHAR(20),
    address VARCHAR(200)
);
```

### 查询结果

```java
User user = userDao.findById(1L);
// user.userDetail 自动填充
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

    // mappedBy 指定由对方维护
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;
}

@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    private String phone;

    // 关联用户
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.BATCH)
    private User user;
}
```

### 数据库结构

```sql
-- user 表
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

-- user_detail 表
CREATE TABLE user_detail (
    id BIGINT PRIMARY KEY,
    phone VARCHAR(20),
    user_id BIGINT  -- 外键指向 user
);
```

## @OneToOne 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `fetch` | FetchType | 抓取策略 | LAZY |
| `mappedBy` | String | 关系维护方字段名 | "" |

## @JoinColumn 属性

| 属性 | 说明 |
|------|------|
| `name` | 外键列名 |
| `referencedColumnName` | 关联的主键列名 |

## 抓取策略配置

### 立即加载

```java
@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "detail_id")
private UserDetail userDetail;
```

### 懒加载

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "detail_id")
private UserDetail userDetail;

// 访问时才查询
User user = userDao.findById(1L);
UserDetail detail = user.getUserDetail();  // 此时触发查询
```

### 批量抓取

```java
@OneToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "detail_id")
private UserDetail userDetail;
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

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;
}

// 用户详情实体
@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    private String phone;

    private String address;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.BATCH)
    private User user;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// 使用
User user = userDao.findById(1L);
System.out.println(user.getName());
System.out.println(user.getUserDetail().getPhone());
```

## NONE 模式：手动控制

当关联数据从缓存或其他数据源获取时，可使用 `FetchMode.NONE`：

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    // 头像数据从 CDN/缓存获取，不查数据库
    @OneToOne(fetch = FetchType.EAGER)  // FetchType 失效
    @JoinColumn(name = "avatar_id")
    @Fetch(FetchMode.NONE)
    private Avatar avatar;  // 永远不会自动查询
}

// 业务代码
User user = userDao.findById(1L);
// 手动从缓存设置 avatar
user.setAvatar(avatarCache.get(user.getAvatarId()));
```

详见 [抓取模式 - NONE 模式](./fetch-mode#none-模式)

## 注意事项

1. **mappedBy 与 @JoinColumn 二选一**：
   - 使用 `mappedBy` 表示由对方维护关系，不需要 `@JoinColumn`
   - 使用 `@JoinColumn` 表示当前方维护关系

2. **外键位置**：
   - 哪个表有外键，哪个实体配置 `@JoinColumn`

3. **懒加载要求**：懒加载需要在事务内或 Session 上下文中访问

4. **XML 优先**：如果 mapper.xml 定义了方法，自动关联失效

## 下一步

- 学习 [一对多关联](./one-to-many)
- 了解 [抓取模式](./fetch-mode)
