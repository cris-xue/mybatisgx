---
sidebar_position: 5
---

# 查询实体

> 解耦查询条件，避免 Service 层拼装

## 概述

QueryEntity 用于封装查询条件，将查询语义从 Service 层解耦到 DAO 层。当查询条件较多时，使用 QueryEntity 比方法名派生更清晰。

## 基本用法

### 定义查询实体

```java
@QueryEntity(User.class)
public class UserQuery extends User {

    private String nameLike;    // 模糊查询 name LIKE '%?%'

    private Integer ageGt;      // 大于查询 age > ?

    private List<Long> idIn;    // IN 查询 id IN (?)

    // getter/setter
}
```

### 使用查询实体

```java
UserQuery query = new UserQuery();
query.setNameLike("张");
query.setAgeGt(20);
query.setIdIn(Arrays.asList(1L, 2L, 3L));

List<User> users = userDao.findList(query);
```

## 字段命名规则

在 QueryEntity 中，字段名由 **属性名 + 条件后缀** 组成：

| 后缀 | 说明 | SQL | 示例 |
|------|------|-----|------|
| (无) | 等于 | `=` | `name` → `name = ?` |
| `Like` | 模糊匹配 | `LIKE '%?%'` | `nameLike` → `name LIKE '%?%'` |
| `StartingWith` | 前缀匹配 | `LIKE '?%'` | `nameStartingWith` |
| `EndingWith` | 后缀匹配 | `LIKE '%?'` | `nameEndingWith` |
| `Lt` | 小于 | `<` | `ageLt` → `age < ?` |
| `Lteq` | 小于等于 | `<=` | `ageLteq` |
| `Gt` | 大于 | `>` | `ageGt` → `age > ?` |
| `Gteq` | 大于等于 | `>=` | `ageGteq` |
| `Between` | 区间 | `BETWEEN` | `ageBetween` |
| `In` | 包含 | `IN` | `idIn` |
| `IsNull` | 为空 | `IS NULL` | `nameIsNull` |
| `NotNull` | 非空 | `IS NOT NULL` | `nameNotNull` |

### 示例

```java
@QueryEntity(User.class)
public class UserQuery extends User {

    // 等于
    private String name;

    // 模糊查询
    private String nameLike;

    // 大于
    private Integer ageGt;

    // 大于等于
    private Integer ageGteq;

    // 小于
    private Integer ageLt;

    // 小于等于
    private Integer ageLteq;

    // 区间
    private List<Integer> ageBetween;

    // IN 查询
    private List<Long> idIn;

    // 前缀匹配
    private String nameStartingWith;

    // 后缀匹配
    private String nameEndingWith;
}
```

## 与实体字段的关系

QueryEntity 继承实体类，同时拥有：

1. **实体字段**：继承自父类的字段，作为等于条件
2. **查询字段**：自定义的带后缀字段，作为特定条件

```java
@Entity
@Table(name = "user")
public class User {
    private Long id;
    private String name;
    private Integer age;
}

@QueryEntity(User.class)
public class UserQuery extends User {
    // 继承: id, name, age (等于条件)

    // 新增: nameLike, ageGt (特殊条件)
    private String nameLike;
    private Integer ageGt;
}
```

## 动态条件

QueryEntity 配合 `@Dynamic` 注解，字段为空时自动跳过：

```java
UserQuery query = new UserQuery();
// 只设置 nameLike，ageGt 为 null 会被跳过
query.setNameLike("张");

// 生成的 SQL: WHERE name LIKE '%张%'
List<User> users = userDao.findList(query);
```

## 组合查询示例

```java
UserQuery query = new UserQuery();
query.setNameLike("张");
query.setAgeGteq(20);
query.setAgeLteq(40);
query.setIdIn(Arrays.asList(1L, 2L, 3L));

// 生成的 SQL:
// WHERE name LIKE '%张%' AND age >= 20 AND age <= 40 AND id IN (1, 2, 3)
List<User> users = userDao.findList(query);
```

## 与方法名派生的对比

| 方式 | 适用场景 | 优点 | 缺点 |
|------|----------|------|------|
| 方法名派生 | 简单查询 | 简洁直观 | 条件多时方法名过长 |
| QueryEntity | 复杂查询 | 可复用、可组合 | 需要定义额外类 |

### 方法名派生方式

```java
// 条件多时方法名很长
List<User> findByNameLikeAndAgeGtAndAgeLtAndDept(String name, Integer ageGt, Integer ageLt, String dept);
```

### QueryEntity 方式

```java
// 条件封装清晰
UserQuery query = new UserQuery();
query.setNameLike(name);
query.setAgeGt(ageGt);
query.setAgeLt(ageLt);
query.setDept(dept);

List<User> users = userDao.findList(query);
```

## 注意事项

1. **必须标注 @QueryEntity**：框架才能识别为查询实体

2. **继承实体类**：推荐继承对应的实体类，复用字段定义

3. **字段类型**：
   - `Between` 和 `In` 后缀字段使用 `List` 类型
   - 其他后缀字段类型与实体字段一致

4. **不映射到数据库**：QueryEntity 不会创建对应的数据库表

5. **查询实体不建议定义普通字段**：按照规范，实体对应数据库字段，`nameLike` 这类查询字段应放在 QueryEntity 中

## 下一步

- 学习 [Statement 注解](./statement)
- 了解 [动态 SQL](./dynamic-sql)
