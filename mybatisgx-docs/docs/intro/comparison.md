---
sidebar_position: 4
---

# 与其他框架对比

> 性能、易用性、可控性三维对比

## 对比概览

| 维度 | MyBatis | MyBatisGX | MyBatis-Plus | JPA |
|------|---------|-----------|--------------|-----|
| **性能** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **易用性** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **可控性** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |

## 性能维度

### MyBatisGX 优势

```
┌─────────────────────────────────────────────────────────────────┐
│                     SQL 预生成机制                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  启动阶段                        运行阶段                       │
│  ┌─────────────┐               ┌─────────────┐                 │
│  │ 扫描 DAO    │               │             │                 │
│  │ 解析方法名  │ ───────────▶  │ 直接执行    │                 │
│  │ 生成 XML    │               │ 无额外开销  │                 │
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
| MyBatisGX | 完全可见 | 启动时生成 XML，可查看 |
| MyBatis-Plus | 部分可见 | Wrapper 动态构建 |
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
│  @Statement 注解  ───────────────────────────▶  次高优先级      │
│       ↓                                                         │
│  实体/QueryEntity 字段  ────────────────────▶  中等优先级       │
│       ↓                                                         │
│  方法名派生  ──────────────────────────────▶  默认行为          │
│                                                                 │
│  任何级别都可以被上级覆盖，SQL 始终可控                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 无隐式行为

| 行为 | MyBatisGX | JPA |
|------|-----------|-----|
| 隐式 SQL 执行 | ❌ 无 | ✅ 有 |
| 自动脏检查 | ❌ 无 | ✅ 有 |
| 延迟加载触发 | 显式调用 | 可能隐式触发 |
| 事务 Flush | 手动控制 | 自动 Flush |

## 选择建议

### 选择 MyBatisGX 如果你：

- 需要减少样板代码，但不想牺牲 SQL 可控性
- 希望查询逻辑收敛在 DAO 层，不侵入 Service 层
- 需要声明式关联查询，但不想引入 JPA 的复杂性
- 希望在 AI 时代减少代码量，降低上下文负担

### 选择 MyBatis 如果你：

- 需要绝对控制每一条 SQL
- 项目简单，不需要 ORM 增强

### 选择 MyBatis-Plus 如果你：

- 习惯 Wrapper 方式构建查询
- 已有项目生态

### 选择 JPA 如果你：

- 完全面向对象建模
- 可以接受黑盒运行时
