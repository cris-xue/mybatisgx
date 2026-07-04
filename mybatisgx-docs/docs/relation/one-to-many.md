---
sidebar_position: 3
---

# 一对多关联

> @OneToMany 注解使用指南

## 概述

一对多关联用于一个实体对应多个关联实体的场景，如部门与用户。

## 单向关联

### 实体定义

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // 部门下的用户列表
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;
}
```

### 数据库结构

```sql
-- org 表
CREATE TABLE org (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

-- user 表
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    org_id BIGINT  -- 外键
);
```

## 双向关联

### 实体定义

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // mappedBy 指定由 User 端维护
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;

    // 父部门
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Org parent;

    // 子部门
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<Org> children;
}

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 所属部门
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;
}
```

## @OneToMany 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `fetch` | FetchType | 抓取策略 | LAZY |
| `mappedBy` | String | 关系维护方字段名 | "" |

## 集合类型

支持多种集合类型：

```java
// List
@OneToMany(mappedBy = "org")
private List<User> userList;

// Set（自动去重）
@OneToMany(mappedBy = "org")
private Set<User> userSet;
```

## 抓取策略配置

### 立即加载

```java
@OneToMany(fetch = FetchType.EAGER)
@JoinColumn(name = "org_id")
private List<User> userList;
```

### 懒加载

```java
@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(name = "org_id")
private List<User> userList;

// 访问时才查询
Org org = orgDao.findById(1L);
List<User> users = org.getUserList();  // 此时触发查询
```

### 批量抓取（推荐）

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "org_id")
private List<User> userList;
```

## N+1 问题

### 问题场景

```java
// 使用 SIMPLE 模式
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.SIMPLE)
@JoinColumn(name = "org_id")
private List<User> userList;

// 查询 10 个部门
List<Org> orgs = orgDao.findList(new OrgQuery());
// 会执行 1 + 10 = 11 条 SQL
// 1 条查询部门 + 10 条查询每个部门的用户
```

### 解决方案

使用 BATCH 模式：

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "org_id")
private List<User> userList;

// 查询 10 个部门
List<Org> orgs = orgDao.findList(new OrgQuery());
// 会执行 1 + 1 = 2 条 SQL
// 1 条查询部门 + 1 条批量查询所有用户
```

## 完整示例

```java
// 部门实体
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    private Long parentId;

    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Org parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<Org> children;
}

// 用户实体
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;
}

// 使用
Org org = orgDao.findById(1L);
System.out.println(org.getName());
for (User user : org.getUserList()) {
    System.out.println(user.getName());
}
```

## 注意事项

1. **推荐使用 mappedBy**：双向关联时，由多的一方维护关系更合理

2. **集合初始化**：建议初始化集合，避免空指针

   ```java
   private List<User> userList = new ArrayList<>();
   ```

3. **懒加载异常**：懒加载需要在事务内访问，否则抛出异常

4. **结果集大小**：一对多关联可能导致结果集膨胀

5. **手动控制数据**：如需从缓存加载集合数据，可使用 `FetchMode.NONE`，详见 [NONE 模式](./fetch-mode#none-模式)

## 下一步

- 学习 [多对一关联](./many-to-one)
- 了解 [抓取模式](./fetch-mode)
