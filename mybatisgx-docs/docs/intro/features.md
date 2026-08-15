---
sidebar_position: 3
---

# 核心特性

> MyBatisGX 功能一览

## 基础功能

| 特性 | 说明 |
|------|------|
| **CRUD 接口即用** | SimpleDao、CurdDao、SelectDao 内置常用方法 |
| **方法名派生 SQL** | 根据方法名自动生成查询/更新/删除语句 |
| **查询实体（QueryEntity）** | 解耦查询条件，避免 Service 层拼装 |
| **动态 SQL** | @Dynamic 注解，字段为空时自动跳过 |
| **分页查询** | Pageable + Page，支持 PageHelper |
| **投影 DTO** | 部分字段返回，减少数据传输 |

## 查询语言

MyBatisGX 提供两种 DSL，分别解决「对象级声明」与「动态 SQL 编写」两个问题：

| 特性 | 说明 |
|------|------|
| **MGXQL** | 对象查询语言，基于实体名/属性名声明查询，方法名、QueryEntity、@Statement 统一落入此模型，支持 JOIN、聚合、投影 |
| **MGXSQL** | 动态 SQL 语法糖，基于 MyBatis LanguageDriver 实现，手写完整 SQL + `#[...]`、`#if(...)[]` 等动态指令，MyBatis 生态通用 |

所有查询最终都统一进入同一套生成链路：

```
方法名派生 ──┐
QueryEntity ──┤
@Statement ──┼──▶ MGXQL ──▶ MGXSQL ──▶ MyBatis SQL
MGXSQL 手写 ──┘
```

> 详见 [查询语言总览](../query-language/overview)

## 关联查询

| 特性 | 说明 |
|------|------|
| **一对一** | @OneToOne 注解，自动关联查询 |
| **一对多** | @OneToMany 注解，List 集合映射 |
| **多对一** | @ManyToOne 注解，对象引用映射 |
| **多对多** | @ManyToMany 注解，中间表自动处理 |
| **抓取模式** | SIMPLE/BATCH/JOIN/NONE 四种模式，精确控制关联查询 |

## 高级功能

| 特性 | 说明 |
|------|------|
| **逻辑删除** | @LogicDelete 注解，删除标记自动处理 |
| **可重复逻辑删除** | @LogicDeleteId 注解，支持删除后重复新增 |
| **乐观锁** | @Version 注解，版本号自动递增 |
| **字段值生成** | @GeneratedValue 注解，支持审计/加密/脱敏 |
| **数据审计** | 录入人/时间、修改人/时间自动填充 |
| **复合主键** | @EmbeddedId、@IdClass 注解支持 |
| **批量操作** | @BatchOperation 注解，支持批量增删改 |

## 架构特性

| 特性 | 说明 |
|------|------|
| **SQL 预生成** | 启动时生成 MyBatis SQL，运行时无解析开销 |
| **XML 优先级最高** | mapper.xml 中定义的 SQL 始终优先 |
| **@Dynamic 最高优先级** | 0.2.0 起，可覆盖 MGXQL 中定义的动态表达式 |
| **MyBatis 无缝升级** | 原有 MyBatis 项目可直接升级 |
| **多数据库支持** | 13 种主流数据库，见下方列表 |

## 支持的数据库

MySQL、MariaDB、OceanBase（MySQL 兼容模式）、SinoDB、Oracle、达梦（Dameng）、UXDB、OceanBase、PostgreSQL、GaussDB、Vastbase、人大金仓（Kingbase）、GBase。

## SQL 优先级

```
mapper.xml 定义  >  @Dynamic  >  @Statement（MGXQL）/ 实体/QueryEntity 参数  >  方法名关键字
```

mapper.xml 定义的方法拥有最高优先级，框架不再自动处理。

## 方法参数优先级

```
@Param 注解参数  >  实体字段  >  查询实体字段  >  无注解简单参数
```

## 支持的查询条件关键字

### 比较运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `Eq` / `Equal` | 等于 | `findByNameEq` |
| `Lt` | 小于 | `findByAgeLt` |
| `Lteq` | 小于等于 | `findByAgeLteq` |
| `Gt` | 大于 | `findByAgeGt` |
| `Gteq` | 大于等于 | `findByAgeGteq` |

### 模糊运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `Like` | 模糊匹配 | `findByNameLike` |
| `StartingWith` | 前缀匹配 | `findByNameStartingWith` |
| `EndingWith` | 后缀匹配 | `findByNameEndingWith` |

### 范围运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `Between` | 区间 | `findByAgeBetween` |
| `In` | 包含 | `findByIdIn` |

### 空值运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `IsNull` | 为空 | `findByNameIsNull` |
| `IsNotNull` / `NotNull` | 非空 | `findByNameNotNull` |

### 逻辑运算符

| 关键字 | 说明 | 示例 |
|--------|------|------|
| `And` | 与 | `findByNameAndAge` |
| `Or` | 或 | `findByNameOrAge` |
| `Not` | 非（可与其他组合） | `findByNameNotLike` |

## 下一步

- 了解 [设计理念](./philosophy)
- 学习 [与其他框架对比](./comparison)
