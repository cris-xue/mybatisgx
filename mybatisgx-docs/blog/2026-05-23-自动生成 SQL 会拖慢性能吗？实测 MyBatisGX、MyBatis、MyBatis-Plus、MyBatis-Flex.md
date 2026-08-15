---
title: 自动生成 SQL 会拖慢性能吗？实测 MyBatisGX、MyBatis、MyBatis-Plus、MyBatis-Flex
authors: [XueChengCheng]
tags: [mybatis, mybatis-plus, jpa, 架构, orm]
description: 从 MyBatis Plus、JPA 到 MyBatis 的选择困境，以及 MyBatisGX 的解决之道
---

## 一、引言

选持久化框架时，很多开发者会担心：

- "自动生成的 SQL 肯定不如手写的快"
- "ORM 框架抽象层越高，性能损耗越大"
- "为了开发效率牺牲性能，划算吗？"

这些担忧合理，但事实如此吗？

我对主流的 MyBatis 系列框架做了一次性能测试：

- MyBatis：原生手写 SQL，性能基准
- MyBatisGX：方法名生成 SQL + 预生成机制
- MyBatis-Plus：运行时动态生成 SQL
- MyBatis-Flex：轻量级动态生成

用真实的测试数据回答：自动生成 SQL 的性能代价到底有多大？

---

## 二、测试环境

### 硬件配置
- CPU：4核8线程
- 内存：16GB

### 软件配置
- JDK 版本：21
- MySQL 版本：5.7
- Spring Boot 版本：3.x

### JVM 参数
```bash
-Xms1g
-Xmx4g
```

### 测试表结构
```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50),
  `age` INT,
  `status` TINYINT,
  `email` VARCHAR(100),
  `phone` VARCHAR(20),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

8个字段，贴近真实业务。只有主键索引，避免索引优化干扰。

---

## 三、测试方法

### 3.1 测试项目

插入操作：
- 单条插入
- 批量插入 100 条
- 批量插入 10,000 条

更新操作：
- 单条更新
- 动态更新（仅更新非空字段）
- 批量更新 100 条
- 批量更新 10,000 条

查询操作：
- 主键查询
- 简单条件查询（3个条件）
- 复杂条件查询（Like、In、Gt 等）
- 动态条件查询

### 3.2 测试策略

JVM 在首次执行时会进行类加载、JIT 编译等初始化工作。每个测试项连续执行 15 轮，第 1 轮单独记录，第 2-15 轮用于统计。

统计指标：
- 首次执行耗时
- 热身后平均值
- 热身后最快值
- 热身后最慢值

### 3.3 四个框架的实现方式

四个框架执行完全相同的业务逻辑。

#### MyBatis（手写 XML）
```xml
<insert id="insert">
  INSERT INTO user (id, username, age, status, email, phone, create_time, update_time)
  VALUES (#{id}, #{username}, #{age}, #{status}, #{email}, #{phone}, #{createTime}, #{updateTime})
</insert>

<select id="findByIdAndAgeAndStatus" resultType="User">
  SELECT * FROM user 
  WHERE id = #{id} AND age = #{age} AND status = #{status}
</select>

<select id="findDynamicByConditions" resultType="User">
  SELECT * FROM user
  <where>
    <if test="id != null">AND id = #{id}</if>
    <if test="username != null and username != ''">
      AND username LIKE CONCAT('%', #{username}, '%')
    </if>
    <if test="age != null">AND age &gt; #{age}</if>
    <if test="statusList != null and statusList.size() > 0">
      AND status IN
      <foreach item="item" collection="statusList" open="(" separator="," close=")">
        #{item}
      </foreach>
    </if>
  </where>
</select>
```

#### MyBatisGX（方法名生成）
```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    // 继承自 SimpleDao 的方法：
    // int insert(User user);
    // int insertBatch(List<User> users, int batchSize);
    // int updateById(User user);
    // int updateBatchById(List<User> users, int batchSize);
    // User findById(Long id);
    
    // 方法名生成查询 SQL
    List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status);
    
    // 复杂条件查询
    List<User> findByIdAndUsernameLikeAndAgeGtAndStatusIn(
        Long id, String username, Integer age, List<Integer> statusList
    );
    
    // 动态查询（使用 QueryEntity）
    @Dynamic
    List<User> findDynamicByIdAndUsernameLikeAndAgeGtAndStatusIn(UserQuery query);
}
```

#### MyBatis-Plus（Wrapper）
```java
public interface UserMapper extends BaseMapper<User> {
}

// Service 层构建查询
List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(User::getId, id)
            .eq(User::getAge, age)
            .eq(User::getStatus, status)
    );
}

// 动态查询
List<User> findDynamicByConditions(Long id, String username, Integer age, List<Integer> statusList) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(id != null, User::getId, id)
            .like(StringUtils.isNotBlank(username), User::getUsername, username)
            .gt(age != null, User::getAge, age)
            .in(CollectionUtils.isNotEmpty(statusList), User::getStatus, statusList)
    );
}
```

#### MyBatis-Flex（QueryWrapper）
```java
public interface UserMapper extends BaseMapper<User> {
}

// Service 层构建查询
List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status) {
    return userMapper.selectListByQuery(
        QueryWrapper.create()
            .eq(User::getId, id)
            .eq(User::getAge, age)
            .eq(User::getStatus, status)
    );
}

// 动态查询
List<User> findDynamicByConditions(Long id, String username, Integer age, List<Integer> statusList) {
    QueryWrapper query = QueryWrapper.create();
    if (id != null) query.eq(User::getId, id);
    if (StringUtils.isNotBlank(username)) query.like(User::getUsername, username);
    if (age != null) query.gt(User::getAge, age);
    if (CollectionUtils.isNotEmpty(statusList)) query.in(User::getStatus, statusList);
    return userMapper.selectListByQuery(query);
}
```

---

## 四、测试结果

### 4.1 单条插入

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 57,594 μs | 1,830 μs | 1,544 μs | 2,262 μs |
| MyBatisGX    | 79,329 μs | 1,922 μs | 1,586 μs | 2,632 μs |
| MyBatis-Flex | 314,609 μs | 2,587 μs | 2,217 μs | 3,145 μs |
| MyBatis-Plus | 118,034 μs | 2,234 μs | 1,876 μs | 2,848 μs |

首次执行，MyBatis-Flex 耗时最长（314ms），MyBatis 最短（57ms）。热身后，四者都在 1.8~2.6ms 之间，最大差距 757μs。各框架波动范围在 400~900μs。

---

### 4.2 批量插入 100 条

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 41 ms | 11 ms | 6 ms | 25 ms |
| MyBatisGX    | 36 ms | 12 ms | 7 ms | 56 ms |
| MyBatis-Flex | 53 ms | 21 ms | 10 ms | 40 ms |
| MyBatis-Plus | 80 ms | 20 ms | 10 ms | 117 ms |

热身后，MyBatis 和 MyBatisGX 在 10~12ms，MyBatis-Flex 和 MyBatis-Plus 在 20~21ms。

---

### 4.3 批量插入 10,000 条

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 459 ms | 307 ms | 239 ms | 653 ms |
| MyBatisGX    | 742 ms | 323 ms | 264 ms | 569 ms |
| MyBatis-Flex | 1,025 ms | 381 ms | 293 ms | 581 ms |
| MyBatis-Plus | 690 ms | 446 ms | 350 ms | 686 ms |

热身后：MyBatis 307ms < MyBatisGX 323ms < MyBatis-Flex 381ms < MyBatis-Plus 446ms。MyBatisGX 比 MyBatis 慢 16ms（约 5%），MyBatis-Plus 慢 139ms（约 45%）。所有框架都在同一数量级。

---

### 4.4 单条更新

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 3,302 μs | 1,139 μs | 877 μs | 1,318 μs |
| MyBatisGX    | 1,987 μs | 1,323 μs | 1,035 μs | 2,523 μs |
| MyBatis-Flex | 4,109 μs | 1,308 μs | 863 μs | 1,772 μs |
| MyBatis-Plus | 15,781 μs | 1,558 μs | 1,230 μs | 2,070 μs |

热身后，四者都在 1.1~1.6ms 之间，最大差距 419μs。MyBatis-Plus 首次执行明显慢于其他框架。

---

### 4.5 动态更新（UpdateSelective）

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 21,109 μs | 1,350 μs | 1,123 μs | 1,524 μs |
| MyBatisGX    | 24,655 μs | 1,574 μs | 1,244 μs | 2,182 μs |
| MyBatis-Flex | 58,886 μs | 1,362 μs | 1,054 μs | 1,890 μs |
| MyBatis-Plus | 29,328 μs | 1,950 μs | 1,591 μs | 2,679 μs |

热身后，四者都在 1.3~2.0ms 之间，最大差距 600μs。

---

### 4.6 批量更新 100 条

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 46 ms | 17 ms | 12 ms | 45 ms |
| MyBatisGX    | 112 ms | 21 ms | 16 ms | 30 ms |
| MyBatis-Flex | 105 ms | 25 ms | 18 ms | 65 ms |
| MyBatis-Plus | 107 ms | 26 ms | 19 ms | 46 ms |

热身后，四者都在 17~26ms 之间。

---

### 4.7 批量更新 10,000 条

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 1,427 ms | 1,458 ms | 1,294 ms | 1,618 ms |
| MyBatisGX    | 1,588 ms | 1,635 ms | 1,423 ms | 1,855 ms |
| MyBatis-Flex | 1,872 ms | 1,568 ms | 1,397 ms | 1,872 ms |
| MyBatis-Plus | 1,948 ms | 1,725 ms | 1,497 ms | 1,965 ms |

热身后，四者都在 1.4~1.7 秒，最大差距 267ms。MyBatisGX 比 MyBatis 慢 177ms（约 12%）。

---

### 4.8 主键查询（FindById）

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 17,080 μs | 1,314 μs | 889 μs | 1,813 μs |
| MyBatisGX    | 14,819 μs | 1,472 μs | 1,047 μs | 2,051 μs |
| MyBatis-Flex | 26,498 μs | 1,232 μs | 777 μs | 1,512 μs |
| MyBatis-Plus | 25,419 μs | 1,333 μs | 1,026 μs | 2,101 μs |

热身后，四者都在 1.2~1.5ms 之间，最大差距 240μs。各框架波动范围在 700~1000μs。

---

### 4.9 简单条件查询（FindByIdAndAgeAndStatus）

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 2,645 μs | 1,198 μs | 952 μs | 1,789 μs |
| MyBatisGX    | 2,467 μs | 1,451 μs | 952 μs | 1,879 μs |
| MyBatis-Flex | 4,449 μs | 1,499 μs | 1,163 μs | 1,955 μs |
| MyBatis-Plus | 9,865 μs | 2,059 μs | 1,673 μs | 3,098 μs |

热身后，MyBatis-Plus 慢于其他三个框架。MyBatis、MyBatisGX、MyBatis-Flex 三者在 1.2~1.5ms 之间。

---

### 4.10 复杂条件查询（Like + In + Gt）

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 4,177 μs | 1,536 μs | 1,148 μs | 2,134 μs |
| MyBatisGX    | 11,444 μs | 1,512 μs | 1,098 μs | 1,886 μs |
| MyBatis-Flex | 2,474 μs | 1,116 μs | 919 μs | 1,227 μs |
| MyBatis-Plus | 6,863 μs | 1,687 μs | 1,352 μs | 2,187 μs |

热身后，四者都在 1.1~1.7ms 之间，最大差距 571μs。MyBatis-Flex 表现较好（1,116μs）。

---

### 4.11 动态条件查询

| 框架 | 首次执行 | 热身后平均 | 最快 | 最慢 |
|------|---------|-----------|------|------|
| MyBatis      | 14,796 μs | 1,897 μs | 1,529 μs | 2,316 μs |
| MyBatisGX    | 2,193 μs | 1,476 μs | 1,145 μs | 1,804 μs |
| MyBatis-Flex | 1,723 μs | 1,386 μs | 1,058 μs | 1,777 μs |
| MyBatis-Plus | 2,696 μs | 1,584 μs | 1,242 μs | 2,163 μs |

这是最接近真实业务的测试。条件参数可能为空，需要动态构建 WHERE 子句。

首次执行，MyBatis 耗时 14,796μs，显著高于其他三个框架（1,723μs ~ 2,696μs）。热身后，四个框架都在 1.4~1.9ms 之间，最大差距 511μs。

波动范围：
- MyBatis：787μs（1,529 ~ 2,316）
- MyBatisGX：659μs（1,145 ~ 1,804）
- MyBatis-Flex：719μs（1,058 ~ 1,777）
- MyBatis-Plus：921μs（1,242 ~ 2,163）

多轮测试结果可重复。热身后四个框架的性能在同一数量级。

---

## 五、MyBatisGX 的价值定位

既然性能都差不多，为什么选 MyBatisGX？

在不牺牲性能的前提下，提升开发效率和代码可维护性。

### 5.1 代码对比：不同框架的实现方式

#### 场景1：简单查询

MyBatis（手写 XML）：
```xml
<!-- UserMapper.xml -->
<select id="findByIdAndAgeAndStatus" resultType="User">
  SELECT id, username, age, status, email, phone, create_time, update_time
  FROM user
  WHERE id = #{id} AND age = #{age} AND status = #{status}
</select>
```
```java
// UserMapper.java
List<User> findByIdAndAgeAndStatus(
    @Param("id") Long id, 
    @Param("age") Integer age, 
    @Param("status") Integer status
);
```

MyBatisGX（方法名生成）：
```java
// UserDao.java - 无需 XML
List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status);
```

MyBatis-Plus（Wrapper）：
```java
// Service 层
List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(User::getId, id)
            .eq(User::getAge, age)
            .eq(User::getStatus, status)
    );
}
```

MyBatis-Flex（QueryWrapper）：
```java
// Service 层
List<User> findByIdAndAgeAndStatus(Long id, Integer age, Integer status) {
    return userMapper.selectListByQuery(
        QueryWrapper.create()
            .eq(User::getId, id)
            .eq(User::getAge, age)
            .eq(User::getStatus, status)
    );
}
```

代码量对比：
- MyBatis：XML + 接口定义，约 8 行
- MyBatisGX：仅 1 行方法声明
- MyBatis-Plus：5 行 Wrapper 构建
- MyBatis-Flex：5 行 QueryWrapper 构建

---

#### 场景2：动态查询

MyBatis（XML + if 标签）：
```xml
<select id="findByConditions" resultType="User">
  SELECT * FROM user
  <where>
    <if test="id != null">AND id = #{id}</if>
    <if test="username != null and username != ''">
      AND username LIKE CONCAT('%', #{username}, '%')
    </if>
    <if test="age != null">AND age &gt; #{age}</if>
    <if test="statusList != null and statusList.size() > 0">
      AND status IN
      <foreach item="item" collection="statusList" open="(" separator="," close=")">
        #{item}
      </foreach>
    </if>
  </where>
</select>
```

MyBatisGX（QueryEntity + 注解）：
```java
// 定义查询实体
@QueryEntity(User.class)
public class UserQuery extends User {
    private String usernameLike;      // 自动识别为 LIKE 条件
    private Integer ageGt;            // 自动识别为 > 条件
    private List<Integer> statusIn;   // 自动识别为 IN 条件
}

// DAO 层 - 一行搞定
@Dynamic
List<User> findByConditions(UserQuery query);
```

MyBatis-Plus（Wrapper）：
```java
// Service 层
public List<User> searchUsers(Long id, String username, Integer minAge, List<Integer> statusList) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(id != null, User::getId, id)
            .like(StringUtils.isNotBlank(username), User::getUsername, username)
            .gt(minAge != null, User::getAge, minAge)
            .in(CollectionUtils.isNotEmpty(statusList), User::getStatus, statusList)
    );
}
```

MyBatis-Flex（QueryWrapper）：
```java
// Service 层
public List<User> searchUsers(Long id, String username, Integer minAge, List<Integer> statusList) {
    QueryWrapper query = QueryWrapper.create();
    if (id != null) query.eq(User::getId, id);
    if (StringUtils.isNotBlank(username)) query.like(User::getUsername, username);
    if (minAge != null) query.gt(User::getAge, minAge);
    if (CollectionUtils.isNotEmpty(statusList)) query.in(User::getStatus, statusList);
    return userMapper.selectListByQuery(query);
}
```

代码量对比：
- MyBatis：XML 约 15 行
- MyBatisGX：QueryEntity 类 + 1 行方法声明
- MyBatis-Plus：6 行 Wrapper 构建
- MyBatis-Flex：6 行 QueryWrapper 构建

---

#### 场景3：层次边界的差异

MyBatis-Plus / MyBatis-Flex（持久化逻辑泄露到 Service 层）：
```java
// Service 层代码
public List<User> searchUsers(Long id, String username, Integer minAge, List<Integer> statusList) {
    // 持久化逻辑直接写在 Service 层
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(id != null, User::getId, id)
            .like(StringUtils.isNotBlank(username), User::getUsername, username)
            .gt(minAge != null, User::getAge, minAge)
            .in(CollectionUtils.isNotEmpty(statusList), User::getStatus, statusList)
    );
}
```

问题：数据库查询逻辑泄露到 Service 层，字段名直接暴露在业务代码中，难以复用和测试。

MyBatisGX（DAO 层收敛）：
```java
// DAO 层
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    @Dynamic
    List<User> searchUsers(UserQuery query);
}

// Service 层
public List<User> searchUsers(Long id, String username, Integer minAge, List<Integer> statusList) {
    UserQuery query = new UserQuery();
    query.setId(id);
    query.setUsernameLike(username);
    query.setAgeGt(minAge);
    query.setStatusIn(statusList);
    
    return userDao.searchUsers(query);
}
```

持久化逻辑不泄露，Service 层只知道 DAO 接口。易于测试，可以 Mock UserDao。字段名变更只需修改 Entity，不影响 Service 层。

---

### 5.2 MyBatisGX 的独特优势总结

| 维度 | MyBatis | MyBatis-Plus | MyBatis-Flex | MyBatisGX |
|------|---------|--------------|--------------|-----------|
| 性能 | ★★★★★ (基准) | ★★★★☆ | ★★★★☆ | ★★★★★ |
| 简单查询代码量 | XML + 接口 | Wrapper 构建 | QueryWrapper | 仅方法名 |
| 动态查询 | XML `<if>` 标签 | Wrapper 动态构建 | QueryWrapper | QueryEntity + @Dynamic |
| 层次边界 | 清晰 | 持久化逻辑泄露 | 持久化逻辑泄露 | 清晰（DAO层收敛） |
| 学习成本 | 需学 XML 语法 | 需学 Wrapper API | 需学 QueryWrapper API | 遵循命名约定 |
| SQL 可控性 | 完全可控 | Wrapper 生成 | QueryWrapper 生成 | 预生成可查看 + XML 可覆盖 |
| 适用场景 | 复杂 SQL / 性能敏感 | 快速开发 / 动态查询 | 灵活查询 | 企业级应用 / 长期维护 |

---

### 5.3 为什么 MyBatisGX 不提供 Wrapper？

这是架构设计立场的问题。

MyBatisGX 的观点：查询本身是稳定的业务能力，而非一次性的实现细节。Service 层应该只表达业务流程，而不承担任何数据库查询语义。

MyBatisGX 通过方法语义 + QueryEntity 的方式，将所有查询能力收敛在 DAO 层，避免持久化逻辑对业务层的侵入。

对比：

```
Wrapper 方式（MyBatis-Plus / Flex）：
Service 层 ──────┐
                 ↓（构建 Wrapper）
             Mapper 层 ──> 数据库
             
问题：持久化逻辑向上泄露

MyBatisGX 的方式：
Service 层 ──────┐
                 ↓（调用明确的方法）
             DAO 层 ──> 数据库
             
优势：层次边界清晰
```

---

## 六、一个有趣的现象：JVM 预热的重要性

测试数据显示，第一次执行通常明显更慢。

MyBatisGX 的单条插入：
```
[Insert][1] 首次执行：79,329 μs  (79ms)
[Insert][1] 第2次：1,675 μs       (1.6ms)
[Insert][1] 第3次：1,625 μs       (1.6ms)
```

首次执行是后续的 47 倍。

MyBatis-Flex 的单条插入：
```
[Insert][1] 首次执行：314,609 μs  (314ms)
[Insert][1] 第2次：3,124 μs       (3.1ms)
```

首次执行是后续的 100 倍。

所有框架都有这个现象。原因包括 JVM JIT 编译、类加载、Mapper 初始化、SQL 缓存建立、连接池预热。

启示：不要用首次执行结果评估性能。生产环境建议预热，应用启动后执行几轮空查询。性能测试必须热身，否则结果没有参考价值。

---

## 七、结论

这轮性能测试的结论：

### 1. 原生 MyBatis 依然是性能基准

大部分场景中，手写 SQL 的 MyBatis 性能都处于第一梯队。

### 2. MyBatisGX 在提供高抽象能力的同时，保持了接近原生的性能

热身后的数据：
- 单条插入：仅慢 5%（92μs）
- 批量插入 1 万条：仅慢 5%（16ms）
- 动态更新：仅慢 16%（224μs）
- 复杂查询：持平或更快

高抽象不等于高损耗。

### 3. MyBatis-Plus 和 MyBatis-Flex 的性能同样优秀

某些场景下比 MyBatis 慢 20%~50%，但绝对值依然很小（微秒级或毫秒级），绝大部分业务系统感知不到差异。

### 4. 真正决定性能的不是 ORM 框架

性能瓶颈通常在：
- SQL 设计：JOIN 策略、子查询 vs 多次查询
- 索引设计：WHERE 条件是否命中索引
- 数据模型：表结构是否合理
- 批量策略：单条 vs 批量操作

框架层的开销在总耗时中占比极小（通常 < 10%）。

### 5. 选择 ORM 时，更应该关注

可维护性、SQL 管理方式、团队开发体验、架构边界，而不是执着于几个百分点的框架开销。

---

## 八、写在最后

性能优化是系统工程，不是简单的框架选型问题。

如果系统遇到性能瓶颈，优先检查：慢 SQL（通过慢查询日志）、索引、N+1 查询、批量操作。解决这些问题后，再考虑是否需要"换一个更快的 ORM 框架"。

MyBatisGX 的设计理念：让代码不被数据库腐蚀。

持久层逻辑归于 DAO，业务逻辑留在 Service。这不仅是编码规范，更是架构立场。

大型项目和长期维护中，这种清晰的边界会带来价值。重构数据库时，Service 层代码无需修改。新人接手时，不需要理解 Wrapper 的构建细节。测试时，可以轻松 Mock DAO 接口。

测试结果显示，这个设计理念并没有以牺牲性能为代价。

---

项目地址：
- 官网：http://www.mybatisgx.com
- GitHub：https://github.com/cris-xue/mybatisgx
- 性能测试：https://github.com/cris-xue/mybatisgx-benchmark

欢迎下载源码自行测试，或在 GitHub 上提出你的问题和建议。
