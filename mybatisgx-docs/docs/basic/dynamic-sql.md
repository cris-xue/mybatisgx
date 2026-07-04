---
sidebar_position: 8
---

# 动态 SQL

> @Dynamic 注解，字段为空时自动跳过

## 概述

`@Dynamic` 注解用于标记方法为动态 SQL，当字段值为空时自动跳过该条件。

## 基本用法

```java
@Dynamic
List<User> findByNameAndAge(String name, Integer age);
```

- `name` 为 null 时，不生成 `name = ?` 条件
- `age` 为 null 时，不生成 `age = ?` 条件

## 动态范围

### 新增

所有插入字段动态：

```java
@Dynamic
int insertSelective(User user);
```

```sql
-- 只插入非空字段
INSERT INTO user (id, name) VALUES (?, ?)  -- age 为 null 时跳过
```

### 修改

所有修改字段和条件动态：

```java
@Dynamic
int updateByIdSelective(User user);

@Dynamic
int updateNameByAge(String name, Integer age);
```

```sql
-- 只更新非空字段，只添加非空条件
UPDATE user SET name = ? WHERE age = ?  -- 其他字段为 null 时跳过
```

### 删除

所有条件动态：

```java
@Dynamic
int deleteByNameAndAge(String name, Integer age);
```

```sql
-- 只添加非空条件
DELETE FROM user WHERE name = ?  -- age 为 null 时跳过
```

### 查询

所有条件动态：

```java
@Dynamic
List<User> findByNameAndAge(String name, Integer age);
```

```sql
-- 只添加非空条件
SELECT * FROM user WHERE name = ?  -- age 为 null 时跳过
```

## 内置动态方法

SelectDao 中的内置方法已标记 `@Dynamic`：

```java
public interface SelectDao<ENTITY, QUERY_ENTITY> extends Dao {

    @Dynamic
    ENTITY findOne(QUERY_ENTITY entity);

    @Dynamic
    List<ENTITY> findList(QUERY_ENTITY entity);

    @Dynamic
    Page<ENTITY> findPage(QUERY_ENTITY entity, Pageable pageable);
}
```

## 使用场景

### 1. 条件可选查询

```java
// 根据传入条件动态查询
@Dynamic
List<User> findByNameAndAgeAndDept(String name, Integer age, String dept);

// 调用示例
userDao.findByNameAndAgeAndDept("张三", null, "研发部");
// 生成 SQL: SELECT * FROM user WHERE name = '张三' AND dept = '研发部'
```

### 2. 选择性更新

```java
// 只更新非空字段
@Dynamic
int updateByIdSelective(User user);

// 调用示例
User user = new User();
user.setId(1L);
user.setName("新名字");
// age 为 null，不会被更新
userDao.updateByIdSelective(user);
// 生成 SQL: UPDATE user SET name = '新名字' WHERE id = 1
```

### 3. 动态条件删除

```java
// 根据非空条件删除
@Dynamic
int deleteByConditions(String name, Integer age, String dept);

// 调用示例
userDao.deleteByConditions(null, 25, null);
// 生成 SQL: DELETE FROM user WHERE age = 25
```

## 与静态 SQL 对比

### 静态 SQL

```java
// 不加 @Dynamic
int updateById(User user);
```

```sql
-- 所有字段都会被更新
UPDATE user SET name = ?, age = ? WHERE id = ?
-- 如果 name 或 age 为 null，数据库字段会被设为 NULL
```

### 动态 SQL

```java
// 加 @Dynamic
@Dynamic
int updateByIdSelective(User user);
```

```sql
-- 只有非空字段会被更新
UPDATE user SET name = ? WHERE id = ?
-- age 为 null 时不会被更新
```

## 空值判断规则

| 类型 | 判断为空的条件 |
|------|----------------|
| 基本类型包装类 | `== null` |
| String | `== null` 或 `isEmpty()` |
| Collection | `== null` 或 `isEmpty()` |
| 数组 | `== null` 或 `length == 0` |

## 自定义动态方法

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 动态查询
    @Dynamic
    List<User> search(String name, Integer age, String dept);

    // 动态更新
    @Dynamic
    int updateSelective(User user, UserQuery condition);

    // 动态删除
    @Dynamic
    int deleteByCondition(UserQuery condition);
}
```

## 注意事项

1. **内置方法无需重复标记**：`findOne`、`findList`、`findPage` 已内置 `@Dynamic`

2. **空字符串也会被跳过**：String 类型空字符串视为空值

3. **集合为空时跳过**：`In` 条件的集合为空或 null 时跳过

4. **所有条件为空时**：
   - 查询：生成无条件查询（查询全部）
   - 更新/删除：不生成 WHERE 子句（影响全表，需谨慎）

## 下一步

- 学习 [投影 DTO](./projection)
- 了解 [关联查询](../relation/overview)
