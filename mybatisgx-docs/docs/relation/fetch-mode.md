---
sidebar_position: 6
---

# 抓取模式

> SIMPLE/BATCH/JOIN/NONE 四种抓取模式详解

## 概述

MyBatisGX 提供四种抓取模式解决关联查询的性能问题，默认使用 BATCH 模式。

## 四种抓取模式

```
┌─────────────────────────────────────────────────────────────────┐
│                      抓取模式对比                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  SIMPLE: N+1 问题                                               │
│  ┌────────┐      ┌────────┐      ┌────────┐                    │
│  │ 查询主表 │ ───▶ │ 查询关联1│ ───▶ │ 查询关联2│  ...           │
│  └────────┘      └────────┘      └────────┘                    │
│  1 条 + N 条 = N+1 条 SQL                                       │
│                                                                 │
│  BATCH: 批量查询                                                │
│  ┌────────┐      ┌────────────────────────────────┐            │
│  │ 查询主表 │ ───▶ │ 批量查询所有关联（WHERE id IN）│            │
│  └────────┘      └────────────────────────────────┘            │
│  1 条 + M 条 = 1+M 条 SQL（M 为关联表数量）                      │
│                                                                 │
│  JOIN: 联表查询                                                 │
│  ┌────────────────────────────────────────────────┐            │
│  │ SELECT * FROM main LEFT JOIN related ON ...    │            │
│  └────────────────────────────────────────────────┘            │
│  1 + 1 = 2 条 SQL（主表 + 无限 JOIN）                           │
│                                                                 │
│  NONE: 不抓取                                                   │
│  ┌────────┐      ┌──────────────┐                              │
│  │ 查询主表 │ ───▶ │ 不查询关联    │                             │
│  └────────┘      └──────────────┘                              │
│  1 条 SQL（仅主表，关联字段为 null）                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## SIMPLE 模式

### 特点

- 简单查询模式
- 每条关联数据单独查询
- 存在 N+1 问题
- 支持懒加载

### 使用场景

- 结果集很小
- 关联数据访问不频繁

### 示例

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.SIMPLE)
@JoinColumn(name = "org_id")
private List<User> userList;
```

### SQL 行为

```sql
-- 查询 10 个部门
SELECT * FROM org;

-- 对每个部门单独查询用户（10 次）
SELECT * FROM user WHERE org_id = 1;
SELECT * FROM user WHERE org_id = 2;
...
SELECT * FROM user WHERE org_id = 10;

-- 总计：1 + 10 = 11 条 SQL
```

## BATCH 模式（推荐）

### 特点

- 批量查询模式
- 收集所有外键后批量查询
- 解决 N+1 问题
- 支持懒加载
- **默认推荐模式**

### 使用场景

- 结果集较大
- 需要访问所有关联数据

### 示例

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "org_id")
private List<User> userList;
```

### SQL 行为

```sql
-- 查询 10 个部门
SELECT * FROM org;

-- 批量查询所有用户
SELECT * FROM user WHERE org_id IN (1, 2, 3, ..., 10);

-- 总计：1 + 1 = 2 条 SQL
```

## JOIN 模式

### 特点

- 联表查询模式
- 第一级单表查询，获取主键
- 第二级无限 JOIN 查询
- 可能产生结果膨胀
- 不支持懒加载

### 使用场景

- 结果集很小
- 需要一次性获取所有关联数据

### 示例

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
@JoinColumn(name = "org_id")
private List<User> userList;
```

### SQL 行为

```sql
-- 第一步：单表查询获取主键
SELECT id FROM org;

-- 第二步：无限 JOIN 查询
SELECT *
FROM org
LEFT JOIN user ON user.org_id = org.id
LEFT JOIN user_detail ON user_detail.user_id = user.id
...
WHERE org.id IN (1, 2, 3, ...);

-- 总计：1 + 1 = 2 条 SQL
```

### 结果膨胀问题

```sql
-- 一对多关联可能导致结果集膨胀
-- 1 个部门 + 100 个用户 = 100 行结果
-- 需要在应用层去重
```

## NONE 模式

### 特点

- 完全不抓取关联数据
- 不生成任何关联 SQL
- 覆盖 FetchType 设置
- 适合手动控制数据填充

### 使用场景

1. **缓存优先场景**
   - 关联数据从 Redis/本地缓存获取
   - 避免数据库查询

2. **分离查询控制**
   - 关联数据通过独立接口查询
   - 精细控制数据访问权限

3. **性能极致优化**
   - 明确不需要某些关联数据
   - 减少无意义的 SQL 生成

4. **手动数据组装**
   - 通过业务逻辑手动填充
   - 从多个数据源聚合

### 示例

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    // 头像数据从 CDN 获取，不查数据库
    @ManyToOne(fetch = FetchType.EAGER)  // EAGER 失效
    @Fetch(FetchMode.NONE)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;  // 永远不会自动查询
}
```

### SQL 行为

```sql
-- 即使配置了关联，也不会生成任何关联查询
SELECT * FROM user;  -- 只查主表

-- user.avatar 为 null
-- 需要手动设置数据
```

### 与 LAZY 的区别

| 特性 | FetchType.LAZY | FetchMode.NONE |
|------|----------------|----------------|
| 初始查询 | 不查询 | 不查询 |
| 访问时 | 自动生成 SQL 查询 | 仍然不查询，返回 null/空 |
| 适用场景 | 延迟加载优化 | 完全不使用自动查询 |
| 覆盖 FetchType | - | 是，NONE 优先级最高 |

### 完整示例

```java
// 场景：组织信息从缓存加载
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @Column(name = "org_id")
    private Long orgId;
    
    // 组织信息不从数据库查询
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.NONE)
    @JoinColumn(name = "org_id")
    private Org org;
}

// 业务代码
List<User> users = userDao.findAll();
// SELECT * FROM user  （只查用户表）

// 手动从缓存填充组织信息
users.forEach(user -> {
    Org org = orgCache.get(user.getOrgId());
    user.setOrg(org);
});
```

## 模式选择建议

| 场景 | 推荐模式 | 原因 |
|------|----------|------|
| 结果集小 | JOIN | 一次查询，性能好 |
| 结果集大 | BATCH | 避免 N+1，结果集可控 |
| 懒加载需求 | SIMPLE / BATCH | JOIN 不支持懒加载 |
| 关联层级深 | BATCH | JOIN 可能过度膨胀 |
| 缓存/手动控制 | NONE | 完全不生成 SQL |
| 分离数据源 | NONE | 从其他系统获取数据 |

## FetchType 与 FetchMode 配合

### FetchType

| 值 | 说明 |
|------|------|
| `LAZY` | 懒加载，访问时才查询 |
| `EAGER` | 立即加载，主查询时一并查询 |

### 组合使用

```java
// 立即加载 + 批量查询（推荐）
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
private List<User> userList;

// 懒加载 + 简单查询
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.SIMPLE)
private List<User> userList;

// 立即加载 + 联表查询
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
private List<User> userList;

// 不抓取 + 手动控制（FetchType 失效）
@ManyToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.NONE)
private Org org;  // 永远不查询，需手动设置
```

## 自动抓取失效条件

当 mapper.xml 中存在对应方法时，自动抓取**失效**：

```java
// 如果 UserMapper.xml 定义了 findById
// MyBatisGX 不会自动处理关联抓取
// 需要在 XML 中手动定义关联查询
```

## 完整示例

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // 推荐配置：立即加载 + 批量查询
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;

    // 懒加载配置
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Org parent;

    // 小结果集：JOIN 模式
    @OneToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "manager_id")
    private User manager;
}
```

## 性能对比

查询 100 个部门，每个部门平均 10 个用户：

| 模式 | SQL 条数 | 性能 | 备注 |
|------|----------|------|------|
| SIMPLE | 1 + 100 = 101 | 差 | N+1 问题 |
| BATCH | 1 + 1 = 2 | 好 | 推荐 |
| JOIN | 1 + 1 = 2 | 好 | 结果膨胀 |
| NONE | 1 | 最优 | 不查关联，手动控制 |

## 注意事项

1. **默认模式**：框架默认使用 BATCH 模式

2. **JOIN 模式限制**：不支持懒加载，可能结果膨胀

3. **懒加载要求**：懒加载需要在事务内或 Session 上下文访问

4. **XML 优先**：mapper.xml 定义的方法不会触发自动抓取

## 下一步

- 学习 [逻辑删除](../advanced/logic-delete)
- 了解 [字段值生成](../advanced/value-generation)
