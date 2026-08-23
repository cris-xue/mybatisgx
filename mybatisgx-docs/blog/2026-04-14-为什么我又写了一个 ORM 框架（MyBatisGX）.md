---
title: 为什么我又写了一个 ORM 框架（MyBatisGX）
authors: [XueChengCheng]
tags: [mybatis, mybatis-plus, jpa, 架构, orm]
description: 从 MyBatis Plus、JPA 到 MyBatis 的选择困境，以及 MyBatisGX 的解决之道
---

## ORM 的"铁三角诅咒"

写了十年 Java，持久层框架一直没找到理想的选择。

**选 MyBatis？**  
灵活归灵活，但增删改查、分页、批量操作全得手写 XML。每个表都要写一遍，机械重复的工作量很大。

**选 MyBatis Plus？**  
开始用的时候确实爽，慢慢就发现不对劲：
- 在 Service 层拼 `LambdaQueryWrapper`，业务代码里到处都是数据库字段名
- 后来我在 Dao 写 default 方法封装 Wrapper。service 是干净了，但 dao 里全是样板代码，问题只是挪了个地方
- 简单的 1:1 关联查询也得手写 SQL 或者查两次
- **批量插入/更新又要继承接口又要继承类，用起来很麻烦**

**选 JPA？**  
写代码的时候确实省心，出问题就头疼了。不知道哪里又偷偷执行了一条 SQL，Specification 写动态查询像在用 Java 翻译 SQL 语句。N+1 查询、分页数据爆炸这些坑踩过几次就不想再碰了。复杂点的报表查询？还是老老实实写原生 SQL。

这三个框架分别代表了持久层的三个方向：
- 要灵活 → 写得累
- 要省事 → 架构糊
- 要自动 → 黑盒坑

---

## MyBatisGX 的做法：定义查询，不实现查询

MyBatisGX 的核心思路：**查询是稳定的业务能力，不是临时拼出来的实现细节。**

Service 层只写业务流程，查询逻辑全部收敛到 Dao 层，用方法定义清楚地表达出来。

### 方法名查询

```java
// 简单查询
List<User> findByNameLike(String name);

// 复杂条件
List<User> findByNameLikeAndAgeBetweenOrStatusEq(
    String name, Integer minAge, Integer maxAge, Integer status
);

// 支持任意分组（用括号）
@Statement(value = "findByNameLikeAnd(AgeOrSex)", language = StatementLanguage.METHOD)
List<User> findByNameLikeAndAgeOrSex(String name, Integer age, Integer sex);
```

方法名就是查询语义，不用 Wrapper，Service 里也不用拼条件。

### 查询实体

条件太多的时候方法名会很长，这时候用 QueryEntity：

```java
@QueryEntity
public class UserQuery extends User {
    private String nameLike;      // 自动解析成模糊查询
    private List<Long> idIn;      // 自动解析成 IN 查询
    private List<Long> ageBetween; // 自动解析成 BETWEEN
}

// Dao 层
List<User> findList(UserQuery query);
```

查询条件作为结构化对象传递，类型安全，IDE 有提示。Service 层不用关心字段名和查询逻辑。

### 动态查询

加个 `@Dynamic` 注解，自动生成动态 SQL：

```java
@Dynamic
List<User> findByNameLikeAndId(String name, Long id);
```

参数为 null 的时候，条件自动不加入 SQL。

### 关联查询

```java
@Entity
public class User {
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)  // 批量抓取，解决 N+1
    private UserDetail userDetail;
    
    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)   // JOIN 抓取，分页不爆炸
    private List<Role> roleList;
}
```

不用写 ResultMap，不用手写 JOIN SQL。支持 4 种抓取策略：`SIMPLE` / `BATCH` / `JOIN` / `NONE`，按场景选。

### 批量操作

业务开发里批量插入/更新/删除太常见了。导入功能、数据同步、批量审核，到处都要用。

MyBatisGX 直接内置了批量方法：

```java
// 继承 SimpleDao 就有，不用继承额外的接口或类
int insertBatch(List<User> users);         // 批量插入
int updateBatchById(List<User> users);     // 批量更新
int deleteBatchById(List<Long> ids);       // 批量删除
```

框架提供的不够？用 `@BatchOperation` 自己定义：

```java
@BatchOperation
int insertBatchCustom(@BatchData List<User> users, @BatchSize int batchSize);
```

不用为了批量功能改架构，直接在 Dao 层调用。

---

## 是 JPA 的换皮吗？

不是。

MyBatisGX 确实用了 JPA 的注解（`@Entity`、`@OneToMany` 等），但只用来描述结构，不参与行为控制。

JPA 有的东西，MyBatisGX 都没有：
- EntityManager、Session 生命周期
- 持久化上下文（Persistence Context）
- 隐式 SQL 执行、脏检查、自动 Flush

MyBatisGX 的执行模型是 MyBatis：
- SQL 预生成（启动时扫描 Dao，生成 MappedStatement）
- SQL 可被 mapper.xml 完全接管
- SQL 最终形态对开发者可见、可干预

厌恶 JPA 的黑盒运行时，但认可它对象建模的成熟设计？MyBatisGX 就是这个定位。

---

## XML 兜底

框架生成的 SQL 有问题？需要写复杂的报表查询？

直接按 MyBatis 原始方式写 XML，框架立即失效。

```xml
<select id="findByNameLike" resultType="User">
    SELECT * FROM user WHERE name LIKE #{name}
</select>
```

只要 mapper.xml 里有同名方法，MyBatisGX 就不再自动生成。

优先级：mapper.xml > MyBatisGX 自动生成。

这是个渐进式框架：
- 简单场景，零 XML
- 复杂场景，随时接管
- 无缝兼容现有 MyBatis 项目

---

## 还有几个真香的点

**无缝升级现有 MyBatis 项目**  
原来的 mapper.xml 完全不用动，换个依赖、继承个 `SimpleDao` 接口，就能开始用。

**逻辑删除、乐观锁、审计字段**  
加个注解就行：
```java
@LogicDelete
private Integer deleted;

@Version
private Integer version;

@GeneratedValue(InputTimeValueProcessor.class)
private LocalDateTime createTime;
```

**复合主键、字段加密、投影 DTO**  
都支持，不需要复杂配置。

---

## 总结

MyBatisGX 不是要替代谁，它回答的是这个问题：

> **在意代码边界、架构演进和长期维护成本的时候，有没有更好的选择？**

只追求"写得快"，现在的 ORM 够用。但如果已经被重复代码、架构腐烂、SQL 黑盒、Specification 折磨过，可能不是缺框架，是缺一套正确的抽象。

---

**核心理念：** 定义查询，不实现查询。让持久层回归持久层。
