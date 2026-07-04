---
sidebar_position: 6
---

# Statement 注解

> 支持复杂条件分组，优先级最高

## 概述

`@Statement` 注解用于声明查询语句的定义入口，支持方法名派生规则，优先级高于方法名和实体字段。

## 基本用法

```java
@Statement(value = "findByNameLikeAndAge", language = StatementLanguage.METHOD)
List<User> findByNameLikeAndAge(String name, Integer age);
```

| 属性 | 说明 |
|------|------|
| `value` | 语句描述内容，支持方法名规则 |
| `language` | 语言类型，默认 `StatementLanguage.METHOD` |

## 条件分组

`@Statement` 支持复杂的条件分组，使用 `And()` 和 `Or()` 包裹条件：

### And 分组

```java
// WHERE name LIKE ? AND (age = ? OR sex = ?)
@Statement("findByNameLikeAnd(AgeOrSex)")
User findByNameLikeAndAgeOrSex(String name, Integer age, Integer sex);
```

### Or 分组

```java
// WHERE (name = ? OR age = ?) AND dept = ?
@Statement("findBy(NameOrAge)AndDept")
List<User> findByNameOrAgeAndDept(String name, Integer age, String dept);
```

### 嵌套分组

```java
// WHERE (name LIKE ? AND (age > ? OR sex = ?)) OR dept = ?
@Statement("findBy(NameLikeAnd(AgeGtOrSex)OrDept)")
List<User> findComplex(String name, Integer age, Integer sex, String dept);
```

## 与方法名派生的区别

### 方法名派生

```java
// 不支持条件分组
List<User> findByNameLikeAndAgeOrSex(String name, Integer age, Integer sex);
// 生成: WHERE name LIKE ? AND age = ? OR sex = ?
```

### Statement 注解

```java
// 支持条件分组
@Statement("findByNameLikeAnd(AgeOrSex)")
User findByNameLikeAndAgeOrSex(String name, Integer age, Integer sex);
// 生成: WHERE name LIKE ? AND (age = ? OR sex = ?)
```

## 优先级

```
┌─────────────────────────────────────────────────────────────────┐
│                      SQL 优先级                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  mapper.xml 定义  ──────────▶  最高优先级（框架不处理）          │
│       ↓                                                         │
│  @Statement 注解  ──────────▶  次高优先级                       │
│       ↓                                                         │
│  实体/QueryEntity 字段  ─────▶  中等优先级                       │
│       ↓                                                         │
│  方法名派生  ──────────────▶  默认行为                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 使用场景

### 1. 需要条件分组

```java
// 复杂 OR 条件需要分组
@Statement("findBy(NameLikeOrCode)AndStatus")
List<User> findByNameLikeOrCodeAndStatus(String name, String code, Integer status);
```

### 2. 方法名过长

```java
// 方法名过长时可读性差
// findByUserNameLikeAndUserAgeGtAndUserDeptInAndUserStatusAndCreateTimeBetween

// 使用 Statement 简化
@Statement("findByUserNameLikeAndUserAgeGtAndUserDeptInAndUserStatusAndCreateTimeBetween")
List<User> search(String userName, Integer userAge, List<Long> userDept, Integer userStatus, List<LocalDateTime> createTime);
```

### 3. 固定查询语义

```java
// 强制使用特定条件，不被实体字段覆盖
@Statement("findByNameAndAge")
User findStrict(String name, Integer age);
```

## 支持的操作类型

`@Statement` 主要用于查询操作：

| 操作 | 说明 |
|------|------|
| `find` | 查询列表 |
| `get` | 查询单条 |
| `query` | 查询 |
| `select` | 查询 |
| `count` | 统计数量 |

## 完整示例

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 简单分组
    @Statement("findByNameLikeAnd(AgeGtOrDept)")
    List<User> findByNameLikeAndAgeOrDept(String name, Integer age, String dept);

    // 多层分组
    @Statement("findBy((NameLikeOrCode)AndStatus)OrCreateTimeBetween")
    List<User> findComplex(String name, String code, Integer status,
                           LocalDateTime startTime, LocalDateTime endTime);

    // 统计查询
    @Statement("countBy(AgeGtOrAgeLt)")
    Long countByAgeRange(Integer ageGt, Integer ageLt);
}
```

## 注意事项

1. **只支持查询**：`@Statement` 注解仅用于查询方法

2. **优先级最高**：会覆盖实体字段和查询实体字段的条件

3. **参数顺序**：方法参数顺序与条件字段顺序一致

4. **分组语法**：
   - `And(条件)` - AND 分组
   - `Or(条件)` - OR 分组
   - 支持嵌套

## 下一步

- 学习 [分页查询](./pagination)
- 了解 [动态 SQL](./dynamic-sql)
