---
sidebar_position: 6
---

# Statement 注解

> 使用 MGXQL 对象查询语言声明 SQL，优先级仅次于 mapper.xml

## 概述

`@Statement` 注解用于在 Mapper 接口方法上声明 MGXQL 查询语句。MGXQL 是基于实体对象的查询语言，支持 SELECT、DELETE、UPDATE、INSERT 四种语句类型。

## 基本用法

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 查询
    @Statement("select * from User where id = :id")
    User findUser(@Param("id") Long id);

    // 条件查询
    @Statement("select * from User where name like :name and age > :age")
    List<User> search(@Param("name") String name, @Param("age") Integer age);

    // 关联查询
    @Statement("select u.* from User u left join Role r on u = r where u.id = :id")
    User findWithRoles(@Param("id") Long id);

    // 聚合查询
    @Statement("select count(*) from User where status = :status")
    long countByStatus(@Param("status") Integer status);

    // 动态条件（参数为空时自动跳过）
    @Statement("select * from User where #[name = :name] #[and age > :age]")
    List<User> dynamicSearch(@Param("name") String name, @Param("age") Integer age);

    // 删除
    @Statement("delete User where id = :id")
    int deleteById(@Param("id") Long id);

    // 更新
    @Statement("update User where id = :id")
    int updateById(@Param("id") Long id, User entity);

    // 插入
    @Statement("insert User")
    int insert(User user);
}
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

| 场景 | 示例 |
|------|------|
| 多表 JOIN 查询 | `select u.* from User u left join Role r on u = r` |
| 聚合统计 | `select count(*) from User group by dept` |
| 自定义投影 | `select name, age from User where ...` |
| 动态可选条件 | `where #[name = :name] #[and age > :age]` |
| 固定查询语义 | 强制使用特定条件，不被实体字段覆盖 |

## 完整语法

MGXQL 支持完整的 SELECT 子句（FROM/JOIN、WHERE、GROUP BY、HAVING、ORDER BY、LIMIT）、动态条件块（`#[body]`、`#if(expr)[body]`、`#choose`）、IN/LIKE 简写等。

详见 [MGXQL 对象查询语言完整教程](../query-language/mgxql)。

## 注意事项

1. **基于实体名**：MGXQL 使用 Java 实体类名（`User`）和属性名（`name`），而非表名和列名

2. **参数引用**：使用 `:paramName` 引用方法参数（对应 `@Param` 注解）

3. **DELETE/UPDATE 必须有 WHERE**：安全要求，防止全表操作

4. **JOIN 必须用别名**：多表查询时所有实体必须声明唯一别名

## 下一步

- 学习 [MGXQL 完整语法](../query-language/mgxql)
- 了解 [MGXSQL 动态 SQL](../query-language/mgxsql)（基于真实表名的动态条件）
- 查看 [查询语言总览](../query-language/overview)（选择合适的查询方式）
