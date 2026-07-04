---
sidebar_position: 1
---

# 关联查询概述

> 自动关联查询，告别繁琐的 ResultMap

## 概述

MyBatisGX 支持声明式的关联查询配置，通过注解定义实体间的关联关系，框架自动处理关联数据的抓取。

## 关联类型

```
┌─────────────────────────────────────────────────────────────────┐
│                      关联类型                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  一对一 (OneToOne)                                              │
│  ┌────────┐         ┌────────────┐                              │
│  │  User  │ ──────▶ │ UserDetail │                              │
│  └────────┘  1:1    └────────────┘                              │
│                                                                 │
│  一对多 (OneToMany)                                             │
│  ┌────────┐         ┌────────┐                                  │
│  │  Org   │ ──────▶ │  User  │                                  │
│  └────────┘  1:N    └────────┘                                  │
│                                                                 │
│  多对一 (ManyToOne)                                             │
│  ┌────────┐         ┌────────┐                                  │
│  │  User  │ ──────▶ │  Org   │                                  │
│  └────────┘  N:1    └────────┘                                  │
│                                                                 │
│  多对多 (ManyToMany)                                            │
│  ┌────────┐         ┌──────────┐         ┌────────┐             │
│  │  User  │ ──────▶ │ UserRole │ ◀────── │  Role  │             │
│  └────────┘  M:N    └──────────┘         └────────┘             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 注解体系

| 注解 | 说明 |
|------|------|
| `@OneToOne` | 一对一关联 |
| `@OneToMany` | 一对多关联 |
| `@ManyToOne` | 多对一关联 |
| `@ManyToMany` | 多对多关联 |
| `@JoinColumn` | 外键列配置 |
| `@JoinTable` | 中间表配置 |
| `@Fetch` | 抓取模式配置 |
| `FetchMode` | 抓取模式枚举 |

## 关联方向

### 单向关联

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    // 只有 User 知道 UserDetail 的存在
    @OneToOne
    @JoinColumn(name = "detail_id")
    private UserDetail userDetail;
}
```

### 双向关联

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @OneToOne(mappedBy = "user")
    private UserDetail userDetail;
}

@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

## 抓取策略

### FetchType

| 策略 | 说明 |
|------|------|
| `LAZY` | 懒加载，访问时才查询 |
| `EAGER` | 立即加载，主查询时一并查询 |

```java
@OneToOne(fetch = FetchType.EAGER)
private UserDetail userDetail;

@OneToMany(fetch = FetchType.LAZY)
private List<User> userList;
```

### FetchMode

| 模式 | 说明 | SQL 行为 |
|------|------|----------|
| `SIMPLE` | 简单查询 | 存在 N+1 问题 |
| `BATCH` | 批量查询 | 1+M 查询，解决 N+1 |
| `JOIN` | 联表查询 | 1+1 查询，可能结果膨胀 |
| `NONE` | 不抓取 | 完全不生成关联 SQL |

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
private List<User> userList;
```

## 关联字段命名

### mappedBy

指定关系由对方维护，用于双向关联：

```java
// User 端
@OneToMany(mappedBy = "org")  // 由 Org 端的 org 字段维护
private List<User> userList;

// Org 端
@ManyToOne
@JoinColumn(name = "org_id")
private Org org;
```

### @JoinColumn

指定外键列名：

```java
@ManyToOne
@JoinColumn(name = "org_id", referencedColumnName = "id")
private Org org;
```

| 属性 | 说明 |
|------|------|
| `name` | 外键列名 |
| `referencedColumnName` | 关联的主键列名 |

## 自动抓取条件

当 mapper.xml 中存在对应方法时，自动抓取**失效**：

```java
// 如果在 UserMapper.xml 中定义了 findById 方法
// MyBatisGX 不会自动处理关联抓取
```

## 基本示例

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 一对一
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;

    // 多对一
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.SIMPLE)
    private Org org;

    // 多对多
    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}
```

## 下一步

- 学习 [一对一关联](./one-to-one)
- 了解 [抓取模式](./fetch-mode)
