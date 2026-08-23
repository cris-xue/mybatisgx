---
sidebar_position: 4
---

# 与其他框架对比

> 性能、易用性、可控性三维对比

## 对比概览

| 维度 | MyBatis | MyBatisGX | MyBatis-Plus | MyBatis-Flex | JPA |
|------|---------|-----------|--------------|--------------|-----|
| **性能** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **易用性** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **可控性** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |
| **查询收敛** | 依赖自觉 | 框架强制 | 依赖自觉 | 依赖自觉 | 依赖自觉 |

## 性能维度

### 实测数据

MyBatisGX 的 SQL 在启动阶段预生成，运行时无解析开销。以下数据来自 `mybatisgx-benchmark` 基准工程（JDK 21、MySQL 5.7、Spring Boot 3.x，热身后）：

| 场景 | MyBatisGX vs 原生 MyBatis |
|------|--------------------------|
| 单条插入 | 仅慢约 5%（1.9ms vs 1.8ms） |
| 批量插入 1 万条 | 仅慢约 5%（323ms vs 307ms） |
| 动态更新 | 慢约 16%（224μs 级） |
| 复杂条件查询 | 持平或更快 |

> 结论：**高抽象不等于高损耗。** MyBatisGX 在提供高抽象能力的同时，保持了接近原生 MyBatis 的性能。

### 为什么快：SQL 预生成

```
┌─────────────────────────────────────────────────────────────────┐
│                     SQL 预生成机制                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  启动阶段                        运行阶段                       │
│  ┌─────────────┐               ┌─────────────┐                 │
│  │ 扫描 DAO    │               │             │                 │
│  │ 解析方法名  │ ───────────▶  │ 直接执行    │                 │
│  │ 生成 SQL    │               │ 无解析开销  │                 │
│  └─────────────┘               └─────────────┘                 │
│                                                                 │
│  vs JPA 运行时反射/脏检查/自动 Flush                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

| 对比项 | MyBatisGX | JPA |
|--------|-----------|-----|
| SQL 生成时机 | 启动时预生成 | 运行时动态生成 |
| 运行时反射 | 无 | 有 |
| 脏检查 | 无 | 有 |
| 自动 Flush | 无 | 有 |
| 持久化上下文 | 无 | 有 |

### 关联查询性能

| 抓取模式 | 说明 | 适用场景 |
|----------|------|----------|
| SIMPLE | 简单查询，存在 N+1 问题 | 数据量小 |
| BATCH | 批量查询，N+1 → 1+M | 默认推荐 |
| JOIN | 联表查询，1+1 模式 | 结果集小 |
| NONE | 不抓取，完全手动控制 | 缓存/分离数据源 |

## 易用性维度

### 代码量对比

**原生 MyBatis**

```xml
<!-- 需要编写 XML -->
<select id="findByNameAndAge" resultType="User">
    SELECT * FROM user
    WHERE name = #{name} AND age = #{age}
</select>
```

**MyBatis-Plus**

```java
// Service 层拼装条件
userMapper.selectList(
    new LambdaQueryWrapper<User>()
        .eq(User::getName, name)
        .eq(User::getAge, age)
);
```

**MyBatis-Flex**

```java
// Service 层拼装条件
userMapper.selectListByQuery(
    QueryWrapper.create()
        .where(User::getName).eq(name)
        .and(User::getAge).eq(age)
);
```

**JPA**

```java
// Specification 拼装
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(root.get("name"), name));
    predicates.add(cb.equal(root.get("age"), age));
    return cb.and(predicates.toArray(new Predicate[0]));
};
```

**MyBatisGX**

```java
// 方法名即查询
List<User> findByNameAndAge(String name, Integer age);
```

### 查询实体解耦

MyBatisGX 通过 QueryEntity 将查询条件从 Service 层解耦：

```java
// 查询条件封装在 QueryEntity 中
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
    private List<Long> idIn;
}

// Service 层只负责业务流程
public List<User> searchUsers(UserQuery query) {
    return userDao.findList(query);
}
```

## 可控性维度

### SQL 可见性

| 框架 | SQL 可见性 | 说明 |
|------|------------|------|
| MyBatis | 完全可见 | XML 中明确定义 |
| MyBatisGX | 完全可见 | 启动时生成 SQL，可查看，可被 XML 接管 |
| MyBatis-Plus | 部分可见 | Wrapper 动态构建 |
| MyBatis-Flex | 部分可见 | QueryWrapper 动态构建 |
| JPA | 不可见 | 运行时生成，难以预测 |

### SQL 覆盖机制

MyBatisGX 的优先级机制确保可控性：

```
┌─────────────────────────────────────────────────────────────────┐
│                      SQL 优先级                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  mapper.xml 定义  ───────────────────────────▶  最高优先级      │
│       ↓                                                         │
│  @Dynamic        ─────────────────────────────▶  动态化控制      │
│       ↓                                                         │
│  @Statement（MGXQL）/ 实体/QueryEntity 字段  ─▶  声明式查询      │
│       ↓                                                         │
│  方法名派生  ──────────────────────────────▶  默认行为          │
│                                                                 │
│  任何级别都可以被上级覆盖，SQL 始终可控                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 查询收敛：防腐的关键

这是 MyBatisGX 与其他增强 ORM 最本质的差异——**查询逻辑被强制收敛在 DAO 层**：

```
Wrapper 方式（MyBatis-Plus / Flex）：
Service 层 ──────┐
                 ↓（构建 Wrapper）
             Mapper 层 ──> 数据库
                 
问题：持久化逻辑向上泄露（腐蚀）

MyBatisGX 的方式：
Service 层 ──────┐
                 ↓（调用明确的方法）
             DAO 层 ──> 数据库
                 
优势：层次边界清晰（防腐）
```

### 无隐式行为

| 行为 | MyBatisGX | JPA |
|------|-----------|-----|
| 隐式 SQL 执行 | ❌ 无 | ✅ 有 |
| 自动脏检查 | ❌ 无 | ✅ 有 |
| 延迟加载触发 | 显式调用 | 可能隐式触发 |
| 事务 Flush | 手动控制 | 自动 Flush |

## MGXSQL：MyBatis 生态通用的动态 SQL

MGXSQL 是防腐与优雅结出的果实，它不绑定 MyBatisGX，而是基于 MyBatis `LanguageDriver` 实现，**任何构建在 MyBatis 之上的框架都可以直接使用**：

- **MyBatis / MyBatis-Plus / MyBatis-Flex**：通过 `@Lang(MgxsqlLanguageDriver.class)` 注解 Mapper，或 XML 中 `lang="mgxsql"`，或全局配置 `default-scripting-language`
- **能力**：`#[...]` 自动条件判断、`#if(...)[]` 条件控制、`#choose/#when/#otherwise` 分支选择、集合参数处理、`#bind` 计算变量、`#include` 片段引用
- **价值**：把 XML 中冗长的 `<if>` / `<foreach>` 标签，写成保持 SQL 结构的动态语法

```sql
-- 原来的 MyBatis XML
<if test="name != null">
    and name = #{name}
</if>

-- MGXSQL 写法
#[and name = :name]
```

> 详见 [查询语言总览](../query-language/overview)
