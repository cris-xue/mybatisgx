---
sidebar_position: 4
---

# 方法名派生查询

> 根据方法名自动生成 SQL

## 概述

MyBatisGX 支持根据 DAO 接口方法名自动派生 SQL，无需编写 XML 或注解。

## 查询方法

### 基本格式

```java
// find/get/query/select 开头
List<User> findByName(String name);
User getByName(String name);
List<User> queryByName(String name);
List<User> selectByName(String name);
```

### 条件组合

```java
// AND 条件
List<User> findByNameAndAge(String name, Integer age);

// OR 条件
List<User> findByNameOrAge(String name, Integer age);

// 复杂组合
List<User> findByNameAndAgeOrDept(String name, Integer age, String dept);
```

### 排序

```java
// 单字段排序
List<User> findByNameOrderByAgeDesc(String name);

// 多字段排序
List<User> findByNameOrderByAgeDescNameAsc(String name);
```

### 限制结果

```java
// Top 关键字
List<User> findTop10ByAgeGt(Integer age);
User findFirstByName(String name);
User findTopByName(String name);
```

### 统计查询

```java
// count 开头
Long countByAgeGt(Integer age);
Long countByNameLike(String name);
```

## 更新方法

### 基本格式

```java
// update/modify 开头，By 后面是条件
int updateNameById(String name, Long id);
int modifyAgeByName(Integer age, String name);
```

### 条件组合

```java
int updateNameByAgeAndDept(String name, Integer age, String dept);
```

### 批量更新

```java
@BatchOperation
int updateStatusBatch(@BatchData List<User> users, @BatchSize int batchSize);
```

## 删除方法

### 基本格式

```java
// delete/remove 开头，By 后面是条件
int deleteByName(String name);
int removeByAge(Integer age);
```

### 条件组合

```java
int deleteByNameAndAge(String name, Integer age);
```

### 批量删除

```java
@BatchOperation
int deleteBatchByIds(@BatchData List<Long> ids, @BatchSize int batchSize);
```

## 新增方法

### 基本格式

```java
// insert/add 开头
int insertUser(User user);
int addUser(User user);
```

### 批量新增

```java
@BatchOperation
int insertBatch(@BatchData List<User> users, @BatchSize int batchSize);
```

## 条件关键字

### 比较运算符

| 关键字 | 说明 | SQL | 示例 |
|--------|------|-----|------|
| `Eq` / `Equal` | 等于 | `=` | `findByNameEq` |
| `Lt` | 小于 | `<` | `findByAgeLt` |
| `Lteq` | 小于等于 | `<=` | `findByAgeLteq` |
| `Gt` | 大于 | `>` | `findByAgeGt` |
| `Gteq` | 大于等于 | `>=` | `findByAgeGteq` |

### 模糊运算符

| 关键字 | 说明 | SQL | 示例 |
|--------|------|-----|------|
| `Like` | 模糊匹配 | `LIKE '%x%'` | `findByNameLike` |
| `StartingWith` | 前缀匹配 | `LIKE 'x%'` | `findByNameStartingWith` |
| `EndingWith` | 后缀匹配 | `LIKE '%x'` | `findByNameEndingWith` |

### 范围运算符

| 关键字 | 说明 | SQL | 示例 |
|--------|------|-----|------|
| `Between` | 区间 | `BETWEEN` | `findByAgeBetween` |
| `In` | 包含 | `IN` | `findByIdIn` |

### 空值运算符

| 关键字 | 说明 | SQL | 示例 |
|--------|------|-----|------|
| `IsNull` | 为空 | `IS NULL` | `findByNameIsNull` |
| `IsNotNull` / `NotNull` | 非空 | `IS NOT NULL` | `findByNameNotNull` |

### 逻辑运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `And` | 与 | `findByNameAndAge` |
| `Or` | 或 | `findByNameOrAge` |
| `Not` | 非（可组合） | `findByNameNotLike` |

### Not 组合使用

```java
// Not 和比较运算符组合
List<User> findByAgeNotGt(Integer age);    // age NOT > ?

// Not 和模糊运算符组合
List<User> findByNameNotLike(String name); // name NOT LIKE ?

// Not 和范围运算符组合
List<User> findByAgeNotBetween(Integer a, Integer b); // age NOT BETWEEN ? AND ?
List<User> findByIdNotIn(List<Long> ids);  // id NOT IN (?)
```

## 方法命名规则

```
┌─────────────────────────────────────────────────────────────────┐
│                     方法命名结构                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  (操作)(条件)(排序)                                              │
│                                                                 │
│  find    ByNameAndAge      OrderByAgeDesc                       │
│  ├───    └────────────┘    └─────────────┘                      │
│  操作      条件              排序                                │
│                                                                 │
│  update  NameByAgeAndDept                                       │
│  ──────  └──┘└────────────┘                                     │
│  操作    SET   WHERE条件                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 参数顺序

方法参数顺序与条件字段顺序一致：

```java
// 参数顺序：name, age, dept
List<User> findByNameAndAgeOrDept(String name, Integer age, String dept);
```

## 注意事项

1. **字段名大小写敏感**：方法名中的字段名首字母大写，与实体字段对应

2. **条件优先级**：
   ```
   @Statement 注解 > 实体/QueryEntity 参数 > 方法名关键字
   ```

3. **XML 优先**：如果在 mapper.xml 中定义了同名方法，使用 XML 中的 SQL

4. **复杂查询建议**：条件过多时建议使用 [QueryEntity](./query-entity) 或 [@Statement](./statement)

## 下一步

- 了解 [查询实体](./query-entity)
- 学习 [Statement 注解](./statement)
