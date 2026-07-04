---
sidebar_position: 5
---

# 设计理念

> 为什么这样设计 MyBatisGX

## 核心原则

```
MyBatisGX = 保留 MyBatis 的可控性 + 提供接近 JPA 的开发效率
```

## SQL 可控性

### XML 优先级最高

在 MyBatisGX 中，mapper.xml 中定义的 SQL 始终拥有最高优先级。这意味着：

- 框架自动生成的 SQL 可以被 XML 完全覆盖
- 开发者始终拥有最终控制权
- 调试时可以查看生成的 XML，也可以手动修改

### SQL 预生成

```
┌─────────────────────────────────────────────────────────────────┐
│                     SQL 预生成流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. MyBatis 加载 mapper.xml                                     │
│  2. 扫描所有实体并解析字段依赖关系                                │
│  3. 扫描 DAO 接口并解析方法名                                    │
│  4. 根据实体和方法信息生成 MyBatis XNode                         │
│  5. 注册 XNode 到 MyBatis                                       │
│                                                                 │
│  → 启动完成后，所有 SQL 已生成，运行时无额外开销                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 无隐式行为

MyBatisGX 不会：

- 隐式执行 SQL
- 自动进行脏检查
- 自动 Flush
- 引入持久化上下文

任何 SQL 的执行都是显式、可预测的。

## Service 层职责分离

### 问题：持久层逻辑泄露

在大量企业级项目中，查询条件以 Wrapper、Criteria、Specification 等形式被动态拼装在 Service 层：

```java
// 典型的 Service 层查询拼装（不推荐）
public List<User> searchUsers(String name, Integer age, String dept) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.isNotBlank(name)) {
        wrapper.like(User::getName, name);
    }
    if (age != null) {
        wrapper.gt(User::getAge, age);
    }
    if (StringUtils.isNotBlank(dept)) {
        wrapper.eq(User::getDept, dept);
    }
    return userMapper.selectList(wrapper);
}
```

问题：

- 查询语义分散在多个 Service 方法中
- 业务代码充斥字段名和数据库细节
- 查询逻辑难以复用和演进

### 解决：查询收敛至 DAO 层

MyBatisGX 的设计立场：

- **查询本身是稳定的业务能力，而非实现细节**
- **Service 层只表达业务流程，不承担数据库查询语义**
- **所有查询以方法定义和查询实体形式收敛至 DAO 层**

```java
// 推荐方式：查询实体封装
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
    private String dept;
}

// Service 层简洁
public List<User> searchUsers(UserQuery query) {
    return userDao.findList(query);
}
```

## AI 时代的优势

### 上下文负担

目前大模型的上下文很有限。我们应该在更加宝贵的上下文中减少非必要的代码，让上下文承载更多有意义的 token。

### 代码量对比

**传统方式**

```java
// Service 层 + Wrapper（大量字段名和条件逻辑）
public List<User> search(String name, Integer age) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.isNotBlank(name)) {
        wrapper.like(User::getName, name);
    }
    if (age != null) {
        wrapper.gt(User::getAge, age);
    }
    return userMapper.selectList(wrapper);
}
```

**MyBatisGX 方式**

```java
// DAO 接口定义（一行代码）
List<User> findByNameLikeAndAgeGt(String name, Integer age);

// 或查询实体方式
List<User> findList(UserQuery query);
```

### 优势总结

| 方面 | 传统方式 | MyBatisGX |
|------|----------|-----------|
| 代码量 | 多 | 少 |
| 上下文占用 | 高 | 低 |
| 可读性 | 分散 | 集中 |
| 可复用性 | 低 | 高 |

## 与 JPA 的边界

### 复用注解，不复用机制

MyBatisGX 复用 JPA 注解的语义，但不引入 JPA 的运行时机制：

```
┌─────────────────────────────────────────────────────────────────┐
│                    JPA 注解在 MyBatisGX 中                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  @Entity, @Table    ─────▶  实体与表的映射描述                   │
│  @Id, @Column       ─────▶  字段与列的映射描述                   │
│  @OneToOne...       ─────▶  关联关系的描述                       │
│                                                                 │
│  【只承担"结构描述"角色】                                        │
│                                                                 │
│  ❌ 不引入 EntityManager                                         │
│  ❌ 不引入 Session 生命周期                                      │
│  ❌ 不引入持久化上下文                                           │
│  ❌ 不引入隐式 SQL 执行                                          │
│                                                                 │
│  【不参与"行为控制"】                                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 如果你厌倦了 JPA 的黑盒

如果你：

- 厌恶 JPA 的黑盒运行时
- 认可 JPA 在对象建模与关系表达上的成熟设计
- 希望 SQL 执行时机显式可控

那么 MyBatisGX 正是为这种矛盾需求而生。

## 设计决策总结

| 决策 | 理由 |
|------|------|
| SQL 预生成 | 运行时无开销，性能最优 |
| XML 最高优先级 | 保留绝对控制权 |
| 查询实体解耦 | Service 层职责清晰 |
| 复用 JPA 注解 | 语义成熟，学习成本低 |
| 不引入 JPA 运行时 | 避免 N+1、脏检查等隐式行为 |
