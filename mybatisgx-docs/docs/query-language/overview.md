---
sidebar_position: 1
---

# 查询语言总览

> MGXQL 与 MGXSQL：两种互补的 SQL 简化方案

## 概述

MyBatisGX 提供两种 DSL 来简化 SQL 编写，它们定位不同、适用场景不同：

| | MGXQL | MGXSQL |
|---|---|---|
| **定位** | 对象查询语言（类 HQL） | 动态 SQL 语法糖 |
| **注解** | `@Statement` | `@Lang` + `@Select`/`@Update`/`@Delete` |
| **SQL 基础** | 实体名 + 属性名（`User`、`name`） | 真实表名 + 列名（`t_user`、`name`） |
| **核心能力** | JOIN、聚合、投影、对象建模（受限 DSL 子集） | 手写透传完整 SQL（JOIN/聚合/子查询照写）+ 动态糖（`#if`/`#for`/`#bind`） |

## 决策树

```
需要动态 WHERE / SET 条件？
├── 简单（1-3 个）→ 方法名查询 或 QueryEntity
└── 复杂 ↓

希望对象级声明（实体/属性名、框架自动处理关联）？
├── 是 → @Statement（MGXQL）— JOIN/聚合对象级声明，框架自动翻译
└── 否 → @Lang + @Select（MGXSQL）— 手写完整 SQL（JOIN/聚合/子查询照写），只加 # 动态指令

需要自动生成 <foreach> / <bind>？
└── 是 → MGXSQL（in :list / %:name% 语法）
```

## 核心差异对比

| 维度 | MGXQL (@Statement) | MGXSQL (@Lang + @Select) |
|------|-------------------|--------------------------|
| 表达层级 | 对象级：实体名/属性名 | SQL 级：真实表名/列名 |
| JOIN | `left join Entity on alias = alias`（对象关联，仅支持 left join） | 手写透传：`left join t_order o on o.user_id = u.id`，任意 join 类型 |
| 聚合函数 | `count(*)`、`max(field)`、`avg(field)`（对象属性） | 手写透传：`count(*)`、`max(column)` 等任意写法 |
| 子查询 | 不支持 | 手写透传：`in (select ...)`、派生表等 |
| 动态条件 | `#[body]`（isNotEmpty）、`#if(expr)[body]`、`#choose` | `#[body]`、`#if(expr)[body]`、`#condition`、`#and/#or`、`#choose` |
| IN 简写 | `in :list`、`in (item:list)=>$item.id` | `in :list`、`in (item:list)=>$item.id`、`#for(item:list)=>expr` |
| LIKE 简写 | `%:name%`、`:name%`、`%:name` | `%:name%`、`:name%`、`%:name` |
| 片段引用 | 不支持 | `#include[sqlId]` |
| 计算变量 | 不支持 | `#bind[name = expr]` + `$name` |
| 能力边界 | DSL 语法子集（受语法+语义校验约束） | 与 MyBatis 手写 SQL 对齐，无限制 |
| 使用场景 | 对象级声明查询、多表关联、聚合统计 | 手写任意 SQL + 动态条件、可选参数、批量操作 |

## 动态条件机制对比

两者都支持 `#[body]` 动态条件，但上下文不同：

```java
// MGXQL — 基于实体/属性名，支持 JOIN
@Statement("select * from User where #[name = :name] #[and age > :age]")
List<User> search(@Param("name") String name, @Param("age") Integer age);

// MGXSQL — 基于真实表/列名，手写 SQL 透传 + 更灵活的动态控制
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where\n  #name = :name\n  #and age > :age")
List<User> search(@Param("name") String name, @Param("age") Integer age);
```

## 下一步

- 学习 [MGXQL 对象查询语言](./mgxql) — JOIN、聚合、投影、动态条件
- 学习 [MGXSQL 动态 SQL 语法](./mgxsql) — 动态条件、foreach、bind、include
