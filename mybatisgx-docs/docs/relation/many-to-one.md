---
sidebar_position: 4
---

# 多对一关联

> @ManyToOne 注解使用指南

## 概述

多对一关联用于多个实体对应一个关联实体的场景，如多个用户属于一个部门。这是最常见的外键关联类型。

## 基本用法

### 实体定义

```java
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

@Entity
@Table(name = "org")
public class Org {

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
    org_id BIGINT,  -- 外键
    FOREIGN KEY (org_id) REFERENCES org(id)
);
```

### 查询结果

```java
User user = userDao.findById(1L);
Org org = user.getOrg();  // 获取关联部门
```

## @ManyToOne 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `fetch` | FetchType | 抓取策略 | LAZY |

## @JoinColumn 配置

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
    name = "org_id",           // 外键列名
    referencedColumnName = "id" // 关联的主键列名
)
private Org org;
```

| 属性 | 说明 |
|------|------|
| `name` | 外键列名 |
| `referencedColumnName` | 关联的主键列名（默认为主键） |

## 抓取策略

### 懒加载（默认）

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "org_id")
private Org org;

// 查询用户时不会查询部门
User user = userDao.findById(1L);

// 访问部门时才查询
Org org = user.getOrg();  // 触发查询
```

### 立即加载

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "org_id")
private Org org;

// 查询用户时一并查询部门
User user = userDao.findById(1L);
// user.org 已填充
```

## 与 @OneToMany 配合

多对一通常与一对多配合使用，形成双向关联：

```java
// User 端（多对一）
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

// Org 端（一对多）
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}
```

## 抓取模式

### SIMPLE 模式

```java
@ManyToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.SIMPLE)
@JoinColumn(name = "org_id")
private Org org;

// 每个 User 单独查询 Org
// 查询 100 个用户 = 1 + 100 条 SQL
```

### BATCH 模式（推荐）

```java
@ManyToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "org_id")
private Org org;

// 批量查询 Org
// 查询 100 个用户 = 1 + 1 条 SQL
```

### JOIN 模式

```java
@ManyToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
@JoinColumn(name = "org_id")
private Org org;

// JOIN 查询
// 1 条 SQL 完成所有查询
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

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    @JoinColumn(name = "org_id")
    private Org org;
}

// 部门实体
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "org", fetch = FetchType.LAZY)
    private List<User> userList;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// 使用
User user = userDao.findById(1L);
System.out.println(user.getName());
System.out.println(user.getOrg().getName());
```

## 多层关联

支持多层关联查询：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    @JoinColumn(name = "org_id")
    private Org org;
}

@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}

@Entity
@Table(name = "company")
public class Company {

    @Id
    private Long id;

    private String name;
}

// 使用
User user = userDao.findById(1L);
String companyName = user.getOrg().getCompany().getName();
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
    
    @Column(name = "org_id")
    private Long orgId;
    
    // 组织信息从缓存获取，不查数据库
    @ManyToOne(fetch = FetchType.EAGER)  // FetchType 失效
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.NONE)
    private Org org;  // 永远不会自动查询
}

// 业务代码
List<User> users = userDao.findAll();
// 手动从缓存设置 org
users.forEach(user -> {
    user.setOrg(orgCache.get(user.getOrgId()));
});
```

详见 [抓取模式 - NONE 模式](./fetch-mode#none-模式)

## 注意事项

1. **默认懒加载**：@ManyToOne 默认 fetch = LAZY，需要显式配置 EAGER

2. **外键列**：外键在当前实体对应的表中

3. **关联对象可能为 null**：外键值不存在时，关联对象为 null

4. **XML 优先**：mapper.xml 定义的方法不会触发自动关联

## 下一步

- 学习 [多对多关联](./many-to-many)
- 了解 [抓取模式](./fetch-mode)
