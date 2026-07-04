---
sidebar_position: 1
---

# 什么是 MyBatisGX

> 保留 MyBatis 的可控性，提供接近 JPA 的开发效率

## 概述

MyBatisGX 是一个基于 MyBatis 的增强型 ORM 框架。它不是 MyBatis-Plus 的替代品，也不是 JPA 的模仿者，而是为「讨厌黑盒，但又不想写重复 SQL」的开发者准备的。

## 背景

在企业级项目实践中，传统的持久化层框架始终存在难以调和的"铁三角"难题：

### 业务逻辑的"侵入性"负担

当前的通用 Mapper 方案（如 MyBatis Plus/Flex）或 JPA 的 Specification，往往需要在 Service 层构建复杂的 Wrapper 或 Criteria。这导致业务代码中充斥着大量的数据库字段名和查询逻辑，造成**持久层逻辑向业务层严重泄露**。

### "样板代码"与"灵活性"的博弈

原生 MyBatis 拥有极高的 SQL 控制力，但即便是最基础的单表 CRUD 也需编写冗长的 XML 片段。开发者需要一种**"零配置启动，无上限定制"**的平衡体验。

### 对象关联查询的"工程化难题"

简单的对象关联（1:1, 1:N）在传统 MyBatis 中需要手动编写 ResultMap 和 Join SQL，不仅繁琐且容易引发 N+1 查询性能问题。

## MyBatisGX 与 JPA 的关系

**MyBatisGX 并不是 JPA 的替代品，也不是 MyBatis 与 JPA 的折中产物。**

我们选择复用 JPA 中成熟、被长期验证的元数据表达方式（如实体映射与关联注解），只是为了统一对象模型与关系描述的语义，而不是引入 JPA 的运行机制。

在 MyBatisGX 中：

- 不会引入 EntityManager、Session 生命周期
- 不存在持久化上下文（Persistence Context）
- 不会发生隐式 SQL 执行、脏检查或自动 Flush
- 任何 SQL 的生成与执行时机都是显式、可预测、可覆盖的

**JPA 注解在 MyBatisGX 中只承担"结构描述"的角色，而不参与"行为控制"。**

所有 SQL 仍然遵循 MyBatis 的执行模型：
- SQL 可被预生成
- SQL 可被 mapper.xml 完全接管
- SQL 的最终形态始终对开发者可见、可干预

## 核心理念

```
MyBatisGX = 保留 MyBatis 的可控性 + 提供接近 JPA 的开发效率
```

如果你厌恶 JPA 的黑盒运行时，但又认可它在对象建模与关系表达上的成熟设计，那么 MyBatisGX 正是为这种矛盾需求而生。

## 支持的数据库

- MySQL
- Oracle
- PostgreSQL

## 适用场景

- 希望减少样板代码，但不想牺牲 SQL 可控性
- 希望使用声明式关联查询，但不想引入 JPA 的复杂性
- 希望在 AI 时代减少上下文负担，让代码更加简洁
- 希望从原生 MyBatis 无缝升级
