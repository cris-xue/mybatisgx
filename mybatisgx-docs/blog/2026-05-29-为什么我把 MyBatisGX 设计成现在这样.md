---
title: 为什么我把 MyBatisGX 设计成现在这样
authors: [XueChengCheng]
tags: [mybatis, mybatis-plus, jpa, 架构, orm]
description: 从 MyBatis Plus、JPA 到 MyBatis 的选择困境，以及 MyBatisGX 的解决之道
---

## 一、开场:持久层的"熵增"现象

### MyBatisGX 不是为了解决 ORM 问题

在开始之前,我想先澄清一个重要的认知:

**MyBatisGX 不是为了解决"如何操作数据库"的问题。**

ORM(对象关系映射)早就不是什么难题了。无论是 JPA、MyBatis、MyBatis Plus,还是其他任何 ORM 框架,都可以很好地操作数据库。

**MyBatisGX 是为了解决工程化问题。**

具体来说,是解决:

- **查询逻辑如何组织?**(不是如何实现查询)
- **查询代码如何管理?**(不是如何写查询)
- **系统如何长期演进?**(不是如何快速开发)

这是一个**架构问题**,不是一个**技术能力问题**。

---

### 一个真实的故事

让我讲一个你可能经历过的故事。

**第一周**,你接到一个需求:实现用户列表查询,支持按名称和状态筛选。

你打开 IDE,快速写下:

```java
// Service 层
public List<User> queryUsers(String name, Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .like(StringUtils.isNotBlank(name), User::getName, name)
            .eq(status != null, User::getStatus, status)
    );
}
```

10 分钟搞定,测试通过,提交代码。很爽。

---

**第三个月**,需求迭代了 5 次:

- 增加按创建时间范围筛选
- 增加按角色筛选
- 增加按部门筛选
- 增加按标签筛选
- 增加模糊搜索手机号

你的 Service 方法变成了这样:

```java
public List<User> queryUsers(UserQueryParam param) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    
    if (StringUtils.isNotBlank(param.getName())) {
        wrapper.like(User::getName, param.getName());
    }
    if (param.getStatus() != null) {
        wrapper.eq(User::getStatus, param.getStatus());
    }
    if (param.getStartTime() != null && param.getEndTime() != null) {
        wrapper.between(User::getCreateTime, param.getStartTime(), param.getEndTime());
    }
    if (param.getRoleId() != null) {
        wrapper.eq(User::getRoleId, param.getRoleId());
    }
    if (param.getDeptId() != null) {
        wrapper.eq(User::getDeptId, param.getDeptId());
    }
    if (CollectionUtils.isNotEmpty(param.getTagIds())) {
        wrapper.in(User::getTagId, param.getTagIds());
    }
    if (StringUtils.isNotBlank(param.getPhone())) {
        wrapper.like(User::getPhone, param.getPhone());
    }
    
    return userMapper.selectList(wrapper);
}
```

方法变长了,但还能接受。

---

**第六个月**,你发现了一些问题:

1. **订单服务**也需要查询用户,但条件略有不同(不需要标签,但需要按等级筛选)
2. **统计服务**也需要查询用户,条件又不一样(只需要按时间和状态)
3. **导出服务**需要查询所有用户,但要排除某些状态

于是你发现:

- `UserService` 里有 3 个相似的查询方法
- `OrderService` 里又拼了一遍用户查询
- `StatisticsService` 里又拼了一遍
- 每个方法里都在操作 `user` 表的字段名

---

**一年后**,产品经理说:

> "我们要把 `status` 字段拆成 `account_status` 和 `verify_status` 两个字段。"

遵循开闭原则,增加字段没问题。但问题是:**谁来感知新字段、谁来决定在哪里用?**

你打开项目全局搜索,发现:

- 15 个 Service 文件
- 47 处分散的查询拼装点

每一处都需要逐一评估:这里的查询逻辑需不需要引入新字段?用哪个?条件怎么写?没有人能快速判断影响范围,改完之后也没人敢说"我确认都处理到了"。

**这就是持久层的"熵增"现象:**

> 查询逻辑从集中走向分散,  
> 从清晰走向混乱,  
> 从可控走向失控。

---

### 为什么会这样?

不是因为技术不行,也不是因为开发者不努力。

而是因为:

**大部分 ORM 框架在帮你"实现查询",但没人管"查询应该在哪里定义"。**

这是一个**工程化问题**,不是技术能力问题。

- Wrapper 可以在任何地方拼装
- Specification 可以在任何地方构造
- Criteria 可以在任何地方组合

这种"自由"在早期确实方便,但随着时间推移:

```
自由 → 分散 → 混乱 → 失控
```

**MyBatisGX 要解决的,就是这个工程化问题。**

---

## 二、核心观点:查询是业务能力,不是实现细节

### 一个思维转变

传统 ORM 框架告诉你:

> "你需要什么数据,就去拼装一个查询。"

这听起来没问题,但隐含了一个假设:

> **查询是一次性的实现细节。**

但在真实的企业级系统中,**查询不是一次性的,它是稳定的业务能力**。

**这是一个工程化视角的转变。**

我们不是在讨论"如何写查询"(这是技术问题),而是在讨论"查询应该如何组织和管理"(这是工程问题)。

---

### 什么是"业务能力"?

举个例子:

**"根据名称和状态查询用户"**,这是一个查询吗?

不,这是一个**业务能力**。

它应该:

1. **有明确的名字**:`findByNameAndStatus`
2. **有明确的参数**:`(String name, Integer status)`
3. **有明确的归属**:`UserDao`
4. **可以被多个 Service 复用**
5. **可以被单独测试**
6. **可以被独立优化**

但如果你用 Wrapper 在 Service 里拼装:

```java
// MyBatis Plus 做法
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.like(User::getName, name).eq(User::getStatus, status)

// 在 OrderService 里又拼一遍
wrapper.like(User::getName, name).eq(User::getStatus, status)

// 在 StatisticsService 里又拼一遍
wrapper.like(User::getName, name).eq(User::getStatus, status)
```

这时:

- 它没有名字(只是一段代码)
- 它没有归属(散落在各处)
- 它无法复用(只能复制粘贴)
- 它难以测试(嵌在业务流程里)
- 它难以优化(不知道有多少地方在用)

---

### 两种范式的本质区别

|  | 传统 ORM(实现查询) | MyBatisGX(定义查询) |
|--|---------------------|---------------------|
| **思维方式** | "我需要什么数据,就去拼一个查询" | "这个查询是稳定的业务能力,应该被定义" |
| **解决的问题** | 如何操作数据库(技术问题) | 如何组织查询代码(工程问题) |
| **代码位置** | Service 层自由拼装 | DAO 层明确定义 |
| **复用方式** | 复制粘贴 | 方法调用 |
| **演进方式** | 到处修改 | 修改一处,影响范围可控 |
| **维护难度** | 时间越长,越混乱 | 时间越长,越清晰 |

---

### MyBatisGX 的核心设计哲学

**查询应该被"定义",而不是被"实现"。**

这不是技术能力的差异,而是**工程化理念的差异**。

具体来说:

1. **查询必须在 DAO 层定义**(四种方式渐进升级,按场景选择)

   | 方式 | 解决的问题 | 适用场景 |
   |------|-----------|---------|
   | **方法名** | 快速开发,框架自动解析为 SQL | 条件 ≤ 3 个的简单查询 |
   | **QueryEntity** | 方法名太长的问题 | 多条件动态组合,不带分组的查询 |
   | **@Statement** | 方法名无法表达 OR 条件的问题(如 `name AND (age OR sex)`) | 含 OR 逻辑的中等复杂查询 |
   | **XML** | 报表、复杂查询的兜底 | 复杂 SQL、性能敏感、多表 JOIN |

2. **Service 层不允许拼装查询**
   - 不提供 Wrapper
   - 不提供 Specification
   - 不提供 Criteria

3. **强制查询有明确归属**
   - 每个查询都是一个方法
   - 每个方法都在 DAO 接口里
   - 所有查询都可枚举、可追踪

**这是为了解决工程化问题,不是为了解决技术问题。**

---

这就是 MyBatisGX 与其他 ORM 框架最本质的区别。

不是功能上的差异,而是**架构哲学上的差异**。

---

## 三、设计推导:从哲学到实现

有了"查询是业务能力"这个核心观点,MyBatisGX 的所有设计决策都是自然推导出来的。

---

### 3.1 为什么坚持 DAO 收敛

**问题:查询逻辑可以在哪里实现?**

#### 选择 A:Service 层自由拼装(JPA/MyBatis Plus/MyBatis Flex 的做法)

```java
// UserService.java
public List<User> queryUsers(String name, Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .like(User::getName, name)
            .eq(User::getStatus, status)
    );
}

// OrderService.java(又拼了一遍)
public List<User> queryUsers(String name) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .like(User::getName, name)
    );
}

// StatisticsService.java(又拼了一遍)
public List<User> queryActiveUsers(Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .eq(User::getStatus, status)
    );
}
```

**优势:**
- 灵活、快速
- 想怎么查就怎么查

**问题:**
- 查询逻辑分散在多个 Service
- 相似查询重复拼装
- 字段名散落各处
- 不知道改一个字段会影响哪些地方

---

#### 选择 B:强制 DAO 层预定义(MyBatisGX 的做法)

```java
// UserDao.java(查询集中定义)
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    
    // 方法名定义查询
    List<User> findByNameLikeAndStatus(String name, Integer status);
    
    // 或者用 QueryEntity
    @Dynamic
    List<User> findList(UserQuery query);
}

// UserService.java(只调用,不拼装)
public List<User> queryUsers(String name, Integer status) {
    return userDao.findByNameLikeAndStatus(name, status);
}

// OrderService.java(复用同一个查询)
public List<User> queryUsers(String name) {
    return userDao.findByNameLikeAndStatus(name, null);
}
```

**优势:**
- 查询有明确归属(UserDao)
- 查询可以复用(多个 Service 调用同一个方法)
- 字段名集中管理(只在 DAO 层出现)
- 易于追踪(全局搜索方法名)

**代价:**
- 需要提前定义查询
- 不能即兴拼装

---

#### MyBatisGX 的立场

**查询是稳定的业务能力,不是一次性实现细节。**

因此:

> 宁愿早期多一步(定义查询),也要保证长期可维护。

---

### 真实场景:订单查询的演变

**第一版需求:**按订单号和状态查询

```java
// MyBatis Plus 做法
orderMapper.selectList(
    new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderNo, orderNo)
        .eq(Order::getStatus, status)
)

// MyBatisGX 做法
orderDao.findByOrderNoAndStatus(orderNo, status)
```

此时两者差不多。

---

**第五版需求:**增加了 10 个查询条件

```java
// MyBatis Plus 做法(在 Service 里)
LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
if (StringUtils.isNotBlank(orderNo)) wrapper.eq(Order::getOrderNo, orderNo);
if (status != null) wrapper.eq(Order::getStatus, status);
if (startTime != null && endTime != null) wrapper.between(Order::getCreateTime, startTime, endTime);
if (userId != null) wrapper.eq(Order::getUserId, userId);
if (payType != null) wrapper.eq(Order::getPayType, payType);
// ... 还有 5 个条件
orderMapper.selectList(wrapper);

// MyBatisGX 做法(在 DAO 定义 QueryEntity)
@QueryEntity(Order.class)
public class OrderQuery extends Order {
    private String orderNoLike;
    private List<Integer> statusIn;
    private List<LocalDateTime> createTimeBetween;
    private Long userId;
    private Integer payType;
    // ... 还有 5 个条件
}

// Service 只需要:
orderDao.findList(orderQuery);
```

---

**第十版需求:**需要在多个 Service 里查询订单

```java
// MyBatis Plus 做法
// → UserService 里拼一遍
// → StatisticsService 里拼一遍
// → ReportService 里拼一遍
// 每个地方都要维护一套查询逻辑

// MyBatisGX 做法
// → 所有 Service 都调用 orderDao.findList(query)
// 只需要在 DAO 层维护一处
```

---

**第 N 版需求:**要优化这个查询的性能

```java
// MyBatis Plus 做法
// → 找到所有拼装的地方(可能有 10+ 处)
// → 不确定能不能改(不知道会影响什么)

// MyBatisGX 做法
// → 在 DAO 的 XML 里接管这个查询
// → 只改一处,影响所有调用方
// → 影响范围明确(看谁调用了这个方法)
```

---

### 3.2 为什么不走全自动 ORM 路线

**全自动 ORM 的承诺:**

> "你不需要了解 SQL,专注对象建模。"

听起来很美好。但在复杂业务系统中:

---

#### 问题 1:性能问题难以排查

**JPA 的例子:**

```java
// 你写的代码
List<Order> orders = orderRepository.findByUserId(userId);

// 实际执行的 SQL(你看不到)
SELECT * FROM t_order WHERE user_id = ?
SELECT * FROM t_order_item WHERE order_id = ?  -- N+1 问题!
SELECT * FROM t_order_item WHERE order_id = ?
SELECT * FROM t_order_item WHERE order_id = ?
...
```

问题:

- 你不知道发了多少条 SQL
- 你不知道为什么慢
- 你要去学 `@EntityGraph`、`JOIN FETCH` 这些"魔法"

---

#### 问题 2:复杂查询最终还是要写 SQL

**真实场景:订单统计报表**

需求:按日期统计每个用户的订单数量、总金额、平均金额,按金额倒序。

```java
// JPA 的 Specification?几十行代码,难以阅读
// Criteria API?更复杂

// 最后还是:
@Query(value = "SELECT user_id, COUNT(*), SUM(amount), AVG(amount) " +
               "FROM t_order " +
               "WHERE create_time BETWEEN ?1 AND ?2 " +
               "GROUP BY user_id " +
               "ORDER BY SUM(amount) DESC", nativeQuery = true)
List<Object[]> statisticsOrderByUser(LocalDateTime start, LocalDateTime end);
```

**结论:复杂查询绕不开 SQL。**

---

#### 问题 3:数据库特性无法使用

**场景:MySQL 的 `INSERT ... ON DUPLICATE KEY UPDATE`**

```sql
-- 高效的批量 upsert
INSERT INTO user (id, name, status) 
VALUES (1, 'Alice', 1), (2, 'Bob', 1)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name), 
    status = VALUES(status);
```

JPA:无法表达,只能循环 `saveOrUpdate`,性能差。

---

#### MyBatisGX 的立场

**SQL 不是负担,而是资产。**

不试图隐藏 SQL,而是让 SQL 有稳定归属:

```
简单查询 → 自动生成(方法名、QueryEntity)
复杂查询 → 显式定义(XML)
性能优化 → 直接接管(XML 覆盖)
```

---

### 对比:什么样的查询适合对象化?

| 查询类型 | 适合对象化 | 适合 SQL |
|---------|-----------|---------|
| 单表 CRUD | ✓ | - |
| 简单条件查询 | ✓ | - |
| 多表 JOIN(2-3 张表) | △ | ✓ |
| 聚合统计 | ✗ | ✓ |
| 复杂报表 | ✗ | ✓ |
| 批量操作 | ✗ | ✓ |
| 数据库特定优化 | ✗ | ✓ |

**MyBatisGX 的做法:**

- 对象化能解决的 → 自动生成
- 对象化不适合的 → 直接写 SQL

不强求"完全对象化",也不放弃"自动化"。

---

### 3.3 为什么保留 XML

很多现代 ORM 的方向是:**彻底消灭 XML 和 SQL**。

但 MyBatisGX 反其道而行之:**拥抱 XML,让 SQL 有稳定归属。**

---

#### 哪些场景必须写 SQL?

**场景 1:复杂报表**

```sql
SELECT 
    u.dept_id,
    d.dept_name,
    COUNT(DISTINCT u.id) as user_count,
    COUNT(o.id) as order_count,
    SUM(o.amount) as total_amount,
    AVG(o.amount) as avg_amount
FROM t_user u
LEFT JOIN t_dept d ON u.dept_id = d.id
LEFT JOIN t_order o ON u.id = o.user_id
WHERE o.create_time BETWEEN ? AND ?
GROUP BY u.dept_id, d.dept_name
HAVING SUM(o.amount) > 10000
ORDER BY total_amount DESC
LIMIT 100
```

你能用对象化表达这个查询吗?能,但为什么要这么折磨自己?

---

**场景 2:性能优化**

```sql
-- MySQL 的 Index Hint
SELECT * FROM t_order USE INDEX (idx_user_time) 
WHERE user_id = ? AND create_time > ?

-- PostgreSQL 的 CTE
WITH recent_orders AS (
    SELECT * FROM t_order WHERE create_time > NOW() - INTERVAL '7 days'
)
SELECT u.*, o.* FROM t_user u
JOIN recent_orders o ON u.id = o.user_id
```

这些数据库特定的优化,对象化无法表达。

---

**场景 3:批量操作**

```sql
-- 批量更新(一条 SQL 搞定)
UPDATE t_order 
SET status = CASE 
    WHEN amount > 1000 THEN 2
    WHEN amount > 500 THEN 1
    ELSE 0
END
WHERE create_time < ?

-- 如果用 ORM:要么循环更新(性能差),要么写 Native SQL
```

---

#### MyBatisGX 的 XML 优先级策略

```
如果 mapper.xml 存在对应方法 → 优先使用 XML
否则 → 使用自动生成的 SQL
```

**这意味着:**

1. **简单查询不用写 XML**:自动生成足够了
2. **需要优化时,可以接管**:写 XML 覆盖自动生成的 SQL
3. **复杂查询直接写 XML**:不强求对象化

---

#### 举例:查询的演变过程

**阶段 1:简单查询**

```java
// DAO 定义
List<Order> findByUserIdAndStatus(Long userId, Integer status);

// 不用写 XML,MyBatisGX 自动生成 SQL
```

---

**阶段 2:需要优化**

```xml
<!-- 在 OrderMapper.xml 里接管 -->
<select id="findByUserIdAndStatus" resultType="Order">
    SELECT * FROM t_order USE INDEX (idx_user_status)
    WHERE user_id = #{userId} AND status = #{status}
    ORDER BY create_time DESC
    LIMIT 1000
</select>
```

DAO 接口不变,只是 SQL 实现被接管了。

---

**阶段 3:变成复杂查询**

```xml
<select id="findByUserIdAndStatus" resultType="Order">
    SELECT o.*, u.name as user_name, u.phone as user_phone
    FROM t_order o
    JOIN t_user u ON o.user_id = u.id
    WHERE o.user_id = #{userId} 
      AND o.status = #{status}
      AND o.create_time > DATE_SUB(NOW(), INTERVAL 30 DAY)
    ORDER BY o.create_time DESC
</select>
```

---

#### 对比其他框架

| 框架 | 简单查询 | 复杂查询 | 优化策略 |
|------|---------|---------|---------|
| **JPA** | 自动 | @Query + Native SQL | 跳出 JPA 体系 |
| **MyBatis Plus** | 自动 | 自定义 XML | 需要手动写 |
| **MyBatisGX** | 自动 | XML 接管 | 平滑过渡 |

---

### 3.4 为什么支持方法名但不鼓励无限动态化

MyBatisGX 支持方法名查询:

```java
List<User> findByNameLikeAndStatusAndCreateTimeBetween(
    String name, 
    Integer status, 
    LocalDateTime start, 
    LocalDateTime end
);
```

但**不提供** Wrapper/Specification/Criteria。

为什么?

---

#### 方法名查询的定位:快速开发,不是稳定业务

**重要说明:**

方法名查询是为了**条件在 3 个左右的快速开发场景**,不是为了稳定的业务查询。

- ✓ 适合:简单的、临时的、条件少的查询
- ✗ 不适合:复杂的、稳定的、长期维护的业务查询

**对于稳定的业务查询,应该用:**

1. **QueryEntity**:条件较多且需要动态组合
2. **XML**:复杂 SQL、性能敏感的查询

---

#### @Dynamic 注解的定位:让条件自动动态化

`@Dynamic` 的作用是让方法名、QueryEntity、`@Statement` 中的所有查询条件具备**动态化能力**——参数为 `null` 时自动跳过,不生成对应的 SQL 条件。

```java
// 不加 @Dynamic:name 和 status 都会拼入 WHERE,传 null 可能产生预期外的结果
List<User> findByNameAndStatus(String name, Integer status);

// 加了 @Dynamic:name 为 null 时跳过,status 为 null 时跳过,自动变成动态查询
@Dynamic
List<User> findByNameAndStatus(String name, Integer status);
```

`@Dynamic` 与 XML 是两套**独立**的机制:XML 里的动态化(`<if>`、`<choose>` 等)完全由你精确控制,不需要 `@Dynamic`;`@Dynamic` 也不依赖 XML,它只作用在自动生成的查询条件上。

**但要注意:`@Dynamic` 不能滥用。** 如果方法上加了 `@Dynamic`,而调用方把所有条件都传了 `null`,会生成没有任何 WHERE 条件的 SQL——查询时是全表扫描,更新和删除时则是全表操作,后果极其严重。这正是为什么 MyBatisGX 支持动态化但不鼓励无节制地动态化。

---

#### 方法名查询 vs Wrapper 的本质区别

**方法名查询的本质:把查询"定义"为方法签名**

```java
// 这是一个"查询定义"
List<User> findByNameAndStatus(String name, Integer status);
```

- 它有明确的名字:`findByNameAndStatus`
- 它有明确的参数:`(String name, Integer status)`
- 它是稳定的、可枚举的
- 它可以被 XML 接管

---

**Wrapper 的本质:在运行时"实现"查询**

```java
// 这是一个"查询实现"
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.like(User::getName, name);
wrapper.eq(User::getStatus, status);
userMapper.selectList(wrapper);
```

- 它没有名字(只是一段代码)
- 它是动态的、临时的
- 它无法被 XML 接管
- 它散落在各处

---

#### 更深层的问题:Wrapper 在 DAO 的 default 方法中

有些讲究的开发者会把 Wrapper 写在 DAO 接口的 default 方法中:

```java
public interface UserDao extends BaseMapper<User> {
    
    default List<User> queryByConditions(String name, Integer status, 
                                         LocalDateTime startTime, LocalDateTime endTime,
                                         Long roleId, Long deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) wrapper.like(User::getName, name);
        if (status != null) wrapper.eq(User::getStatus, status);
        if (startTime != null && endTime != null) wrapper.between(User::getCreateTime, startTime, endTime);
        if (roleId != null) wrapper.eq(User::getRoleId, roleId);
        if (deptId != null) wrapper.eq(User::getDeptId, deptId);
        return this.selectList(wrapper);
    }
}
```

这看起来解决了"查询分散"的问题,但引入了两个新问题:

---

**问题 1:通用查询方法缺失业务表达**

为了让方法通用,你会枚举出所有可能的字段作为参数:

```java
default List<User> queryByConditions(
    String name, Integer status, LocalDateTime startTime, LocalDateTime endTime,
    Long roleId, Long deptId, List<Long> tagIds, String phone, Integer level
) {
    // 动态拼装所有条件
}
```

**问题是:**

- 方法名 `queryByConditions` 天生缺失业务表达
- 谁都可以调用,但到底表达什么业务含义?
- 是"查询活跃用户"?还是"查询待审核用户"?还是"导出用户列表"?
- 方法签名无法传达业务意图

---

**问题 2:隐性的架构膨胀**

把 Wrapper 写在 DAO 的 default 方法里,从层次上说仍然是三层架构——DAO 只是一层里多了一种写法,并没有多出一层。

但真正的问题出在 Service 层。当 Service 里充满了各种 Wrapper 拼装逻辑,业务代码和数据库代码混在一起,有些团队为了维持 Service 的"业务纯净度",会在上层再加一层:

```
Controller → Compose → Service → DAO
```

这个 Compose 层专门负责编排业务流程,Service 层专注于调用 DAO 做数据操作。

**问题是:这一层为什么会存在?**

因为 Service 里被数据库代码占据,业务逻辑和查询逻辑的边界模糊了,不得不加一层来分离关注点。

这一层是**为了解决持久层无序而衍生出来的**,不是业务真的需要一个编排层。你为了解决一个工程化问题,膨胀了整个系统的架构复杂度。性价比高吗?

---

#### 更根本的问题:Wrapper 是"无名查询",无论放在哪里

有一点需要说清楚:把 Wrapper 移到 DAO 的 default 方法里,**解决的只是"物理位置",没有解决"查询本质"**。

Wrapper 在任何地方都有一个根本缺陷——它是无名的:

- **无法被 XML 接管**:你无法在 XML 里优化一段 Wrapper 拼装逻辑,因为它没有固定的方法签名
- **无法追踪调用方**:grep 方法名可以找到所有调用者,但 Wrapper 逻辑散落在代码里,无从追踪
- **无法表达业务意图**:`queryByConditions(name, status, startTime, ...)` 这样的通用方法,谁调用它都不知道这次调用代表什么业务含义

**结论:这是 Wrapper 机制本身的问题,不是"在哪里写"的问题。**

---

#### MyBatisGX 的做法:不增加层次,而是改变表达方式

MyBatisGX 不需要 default 方法这一层,而是通过 **QueryEntity** 和 **XML** 来表达:

```java
// 方式 1:QueryEntity(类型安全、业务清晰)
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private List<Integer> statusIn;
    private List<LocalDateTime> createTimeBetween;
}

@Dynamic
List<User> findList(UserQuery query);

// 方式 2:业务明确的方法(语义清晰)
List<User> findActiveUsers(UserQuery query);
List<User> findPendingUsers(UserQuery query);
```

**优势:**

- 不增加层次,仍然是三层架构
- 方法名可以表达业务含义
- QueryEntity 提供类型安全
- XML 可以接管和优化

---

#### 为什么方法名更像"业务能力"?

**对比:**

```java
// 方法名(定义)
userDao.findByNameAndStatus(name, status);

// Wrapper(实现)
userMapper.selectList(
    new LambdaQueryWrapper<User>()
        .like(User::getName, name)
        .eq(User::getStatus, status)
);
```

**问题:**

- 半年后,哪个更容易理解?
- 如果要优化,改哪个更安全?
- 如果要复用,哪个更方便?

---

#### 对象表达 SQL 真的好吗?

MyBatis Plus、MyBatis Flex 都在用 Java 对象去表达 SQL。但我们需要先问一个更根本的问题:

**SQL 是声明式语言,Wrapper 是命令式拼装**

SQL 说的是"我要什么":

```sql
SELECT * FROM user WHERE name LIKE ? AND status = ?
```

Wrapper 说的是"我怎么构建一个查询对象":

```java
wrapper.like(User::getName, name).eq(User::getStatus, status)
```

用对象拼装 SQL,本质上是把一个**声明式问题变成了命令式问题**。SQL 本来就能清晰地表达意图,对象化之后反而多了一层翻译。简单查询时感觉不出区别,一旦查询复杂,你就要在脑子里把对象链式调用"翻译"回 SQL 才能理解逻辑,调试更是如此。

---

**问题 1:对象化后,SQL 无法调试**

```java
// 用对象表达
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.like(User::getName, name)
       .eq(User::getStatus, status)
       .between(User::getCreateTime, startTime, endTime)
       .in(User::getRoleId, roleIds);
```

**问题是:**

- 你看不到最终生成的 SQL 是什么
- 你无法在 SQL 客户端里调试这个查询
- 你无法直接复制 SQL 去优化
- 出了性能问题,你要先"翻译"成 SQL 才能分析

**对比 MyBatisGX 的 XML:**

```xml
<select id="findList" resultType="User">
    SELECT * FROM t_user
    <where>
        <if test="name != null">AND name LIKE #{name}</if>
        <if test="status != null">AND status = #{status}</if>
        <if test="createTimeBetween != null">
            AND create_time BETWEEN #{createTimeBetween[0]} AND #{createTimeBetween[1]}
        </if>
        <if test="roleIds != null">AND role_id IN 
            <foreach collection="roleIds" item="id" open="(" close=")" separator=",">
                #{id}
            </foreach>
        </if>
    </where>
</select>
```

- SQL 清晰可见
- 可以直接复制到 SQL 客户端调试
- 可以直接看到执行计划
- 可以直接优化

---

**问题 2:对象真的比 SQL 更适合表达数据操作吗?**

SQL 是专门为数据操作设计的语言,它有:

- 声明式语义(说"要什么",不说"怎么做")
- 集合操作(JOIN、GROUP BY、HAVING)
- 丰富的函数(聚合、窗口、字符串、日期)
- 数据库特定优化(索引提示、执行计划)

**用对象表达 SQL,本质上是在用一种不适合的工具去做一件事。**

简单的 CRUD 可以对象化,但复杂的数据操作,SQL 才是最自然的表达方式。

---

#### MyBatisGX 的立场:不强求对象化

```
简单查询 → 对象化(方法名、QueryEntity)
复杂查询 → SQL 化(XML)
```

不试图用对象表达一切,而是让每种工具做它最擅长的事。

---

#### QueryEntity:参数化查询,但查询本身仍然稳定

**MyBatisGX 的 QueryEntity:**

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private List<Integer> statusIn;
    private List<LocalDateTime> createTimeBetween;
}

// 使用
UserQuery query = new UserQuery();
query.setNameLike("Alice");
query.setStatusIn(Arrays.asList(1, 2));
userDao.findList(query);
```

**对比 Wrapper:**

```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.like(User::getName, "Alice");
wrapper.in(User::getStatus, Arrays.asList(1, 2));
userMapper.selectList(wrapper);
```

**区别:**

| | QueryEntity | Wrapper |
|--|-------------|---------|
| **查询定义** | `findList(UserQuery)` 方法 | 没有,只是一段代码 |
| **参数类型安全** | ✓(Java 类) | ✓(Lambda 表达式) |
| **可复用** | ✓(传递 QueryEntity 对象) | △(需要封装成方法) |
| **可接管** | ✓(XML 可以接管 `findList`) | ✗(无法接管) |
| **可调试** | ✓(XML 中 SQL 可见) | ✗(SQL 隐藏在对象中) |

---

#### 需要动态条件怎么办?

**两种路径,取决于查询的复杂程度:**

**路径 1:`@Dynamic` 自动动态化**

适合方法名、QueryEntity、`@Statement` 场景——加一个注解,所有参数为 `null` 时自动跳过:

```java
@Dynamic
List<User> findByNameAndStatus(String name, Integer status);

@Dynamic
List<User> findList(UserQuery query);
```

**路径 2:XML 精确控制**

适合复杂动态逻辑,由你完全掌控每个条件的生成规则:

```xml
<select id="findList" resultType="User">
    SELECT * FROM t_user
    <where>
        <if test="name != null">AND name LIKE #{name}</if>
        <if test="status != null">AND status = #{status}</if>
        <if test="createTimeBetween != null">
            AND create_time BETWEEN #{createTimeBetween[0]} AND #{createTimeBetween[1]}
        </if>
    </where>
</select>
```

**关键点:**

- `@Dynamic` 和 XML 是两套独立机制,各自解决不同场景
- 无论哪种方式,动态 SQL 都在 DAO 层**显式定义**,而不是在 Service 里临时拼装

---

### 小结:设计决策的一致性

```
核心哲学:查询是业务能力,应该被定义和管理
              ↓
决策 1:强制 DAO 收敛(查询有明确归属)
              ↓
决策 2:不走全自动 ORM(SQL 是资产)
              ↓
决策 3:保留 XML(复杂查询有稳定归属)
              ↓
决策 4:方法名但不动态化(定义 > 实现)
```

所有设计决策都在强化同一个理念:

**让查询成为稳定的、可管理的业务能力。**

---

## 四、MyBatisGX 与 JPA/MyBatis Plus/MyBatis Flex 的本质区别

### 不是能力的差异,而是约束的差异

这几个框架在基础技术能力上都能操作数据库:

- 都支持基础 CRUD
- 都支持自定义 SQL

**真正的区别在于:设计约束。**

---

### 对比:在不同场景下的表现

#### 场景 1:单表 CRUD

```java
// JPA
userRepository.save(user);
userRepository.findById(id);

// MyBatis Plus / MyBatis Flex
userMapper.insert(user);
userMapper.selectById(id);

// MyBatisGX
userDao.insert(user);
userDao.findById(id);
```

**结论:基础 CRUD 上,几个框架没有区别。**

---

#### 场景 2:简单条件查询

```java
// JPA(Spring Data 方法名自动解析为 SQL)
List<User> findByNameLikeAndStatus(String name, Integer status);

// MyBatis Plus / MyBatis Flex(不支持方法名解析,必须手动实现)
// 方式 1:Wrapper 在调用方拼装
userMapper.selectList(
    new LambdaQueryWrapper<User>()
        .like(User::getName, name)
        .eq(User::getStatus, status)
);
// 方式 2:写 XML

// MyBatisGX(方法名自动解析为 SQL,与 JPA 一样)
List<User> findByNameLikeAndStatus(String name, Integer status);
```

**结论:** 方法名解析只有 JPA 和 MyBatisGX 支持。MyBatis Plus 和 MyBatis Flex 不具备这个能力,简单查询也需要手动写 Wrapper 或 XML。

---

#### 场景 3:复杂动态查询

**需求:根据不同条件组合查询用户**

```java
// JPA(Specification,可以在 Service 拼装)
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    if (name != null) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    }
    if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
    }
    return cb.and(predicates.toArray(new Predicate[0]));
};
userRepository.findAll(spec);

// MyBatis Plus / MyBatis Flex(LambdaQueryWrapper,可以在 Service 拼装)
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
if (name != null) wrapper.like(User::getName, name);
if (status != null) wrapper.eq(User::getStatus, status);
userMapper.selectList(wrapper);

// MyBatisGX(必须在 DAO 定义)
// 方式 1:QueryEntity
@QueryEntity(User.class)
class UserQuery {
    private String nameLike;
    private Integer status;
}
@Dynamic
userDao.findList(query);

// 方式 2:自定义方法 + XML
List<User> findByConditions(UserQuery query);
```

**区别:**

| | JPA / MyBatis Plus / MyBatis Flex | MyBatisGX |
|--|-----------------------------------|-----------|
| **灵活性** | ✓ 可以在任何地方拼装 | △ 必须在 DAO 定义 |
| **查询收敛** | ✗ 容易分散到 Service | ✓ 框架强制 |

---

#### 场景 4:需求变更,查询条件增加

**需求:从 2 个条件增加到 10 个条件**

```java
// JPA / MyBatis Plus / MyBatis Flex
// → Service 里的拼装代码越来越长
// → 多个 Service 各自拼装,逻辑分散

// MyBatisGX
// → 扩展 QueryEntity,添加新字段
// → 或者在 XML 里添加新条件
// → 所有 Service 自动享受新条件(调用同一个 DAO 方法)
```

---

#### 场景 5:关联查询

**需求:查询用户及其订单列表**

```java
// JPA
@Entity
class User {
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
// JPA 也可以批量抓取(@BatchSize、@Fetch(FetchMode.SUBSELECT))
// 在抓取策略上,JPA 和 MyBatisGX 都能解决 N+1 问题

// MyBatis Plus
// 完全手动写 ResultMap + JOIN SQL

// MyBatis Flex
// 支持关联查询(@RelationOneToMany 等注解)

// MyBatisGX
@Entity
class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)  // 自动解决 N+1
    private List<Order> orders;
}
```

**结论:关联查询上,JPA 和 MyBatisGX 都能自动处理,MyBatis Flex 也提供了注解支持,MyBatis Plus 则需要完全手动。这不是各框架的核心差异点,JPA 真正的软肋在动态 SQL,见场景 6。**

---

#### 场景 6:动态 SQL

**需求:根据不同条件组合构建复杂查询**

```java
// JPA
// Specification 可以动态组合,但这只是"条件过滤"的动态化
// 一旦涉及多表 JOIN + 动态条件、子查询、CASE WHEN、GROUP BY + HAVING
// Criteria API 写出来极其冗长,几乎没有可读性
// 最终的解决方案几乎都是退回到 @Query 写原生 SQL——彻底跳出 JPA 体系

// MyBatis Plus / MyBatis Flex
// LambdaQueryWrapper 可以动态拼装,但本质上只支持单表 WHERE 条件的动态化
// 涉及窗口函数、递归查询、复杂子查询时,仍然要回到 XML

// MyBatisGX
// 简单动态条件:QueryEntity/@Statement + @Dynamic,参数为 null 自动跳过
// 复杂动态 SQL:XML 的 <if>、<choose>、<foreach>,用户精确控制
// 两种机制独立,按场景选择,SQL 始终可见、可调试、可接管
```

**结论:动态 SQL 是 JPA 最大的软肋。** 你可以忍受 JPA 的 N+1(配置一下 `@BatchSize` 就解决了),但你无法优雅地解决 JPA 的动态 SQL 问题——要么接受冗长的 Criteria API,要么放弃 JPA 直接写原生 SQL,两者都是妥协。

---

### 完整对比表

| 对比维度 | JPA | MyBatis Plus / Flex | MyBatisGX |
|---------|-----|---------------------|-----------|
| **基础 CRUD** | ✓ | ✓ | ✓ |
| **方法名查询** | ✓ Spring Data 解析 | ✗ 不支持,需手写 Wrapper 或 XML | ✓ 自动解析 |
| **动态查询** | ✓ Specification<br/>可在 Service 拼装 | ✓ Wrapper<br/>可在 Service 拼装 | △ 必须在 DAO 方法定义@Dynamic<br/>用 方法名/QueryEntity/@Statement |
| **查询逻辑收敛** | ✗ 依赖开发者自觉 | ✗ 依赖开发者自觉 | ✓ 框架强制 |
| **SQL 透明度** | ✗ 完全黑盒 | △ 可以看到但不直观 | ✓ 可预生成/可接管 |
| **复杂动态 SQL** | ✗ Criteria API 极其冗长<br/>最终退回原生 SQL | ✓ XML | ✓ XML |
| **关联查询** | ✓ 自动且可控 | MP ✗ 完全手动<br/>Flex △ 注解支持 | ✓ 自动且可控 |

---

### MyBatisGX 适合谁?

如果你认同下面这些:

- 查询应该被定义,而不是随手实现
- 代码要见名知意,数据库代码也不例外
- 架构约束比无限自由更利于长期演进

那 MyBatisGX 就是为你设计的——无论项目大小。

写得干净不是企业级系统的专利,是每个有点追求的工程师都可以做到的选择。

---

## 五、收尾:我的技术价值观

### 为什么我对"长期维护"有执念

我做了十年企业级项目开发,见过太多系统从清晰走向混乱。

我发现一个规律:

> **早期"方便"的设计,往往会在后期变成技术债。**

---

### 一个真实的案例

**某系统,第一年:**

- 用 MyBatis Plus 的 Wrapper
- 开发很快,大家都很爽
- 代码很灵活,想怎么查就怎么查

**第三年:**

- 查询逻辑散落在 30+ 个 Service
- 要优化一个慢查询,不知道影响范围
- 要重构数据库表结构,不敢动

**最后:**

- 技术负责人说:我们重构吧
- 评估后发现:改不动了
- 只能加新表,旧表标记为 `@Deprecated`
- 系统越来越重

---

### 我的思考

**为什么会这样?**

不是因为 MyBatis Plus 不好,也不是因为开发者不努力。

而是因为:

> **框架给了太多自由,但没有给足够的约束。**

当每个开发者都可以在 Service 里拼装查询时:

- 没有人会去复用已有的查询(因为不知道在哪)
- 没有人会去思考查询的归属(因为到处都能查)
- 没有人会去管理查询的生命周期(因为只是一段临时代码)

---

### MyBatisGX 的设计立场

**我不追求"最快的框架",而是追求"最适合做长久的框架"。**

MyBatisGX 的设计约束:

1. **强制 DAO 收敛** → 查询有明确归属
2. **不提供 Wrapper** → 查询不能随意拼装
3. **保留 XML 优先级** → 查询可被接管和优化
4. **支持但不鼓励动态化** → 查询应该是稳定的

这些约束在早期可能不太方便,但在长期会让系统:

- 更清晰(查询在哪里一目了然)
- 更可控(影响范围可预测)
- 更可维护(修改一处,到处生效)

---

### 我的技术价值观

我相信:

> **好的架构不是给你无限的自由,而是给你恰当的约束。**

- 自由让你快速开始
- 约束让你持续演进

MyBatisGX 选择了后者。

---

### 写在最后

从命名到约束,从定义到归属,这些追求背后有一个更简单的信念:

**Coding 是一种艺术。**

好的代码有结构、有表达、有边界。持久层尤其如此——它离数据最近,也最容易成为系统混乱的根源。MyBatisGX 的每一个设计决策,都是在试图让这层代码更像一门手艺,而不是临时凑合。

如果你认同"查询应该被定义,而不是被随手实现",如果你也在追求代码的清晰与可控——

欢迎来试试 MyBatisGX。

也欢迎来挑战我的设计理念。

因为:

> **技术的进步,来自于不断的质疑和对话。**

---

## 社区交流

欢迎交流 MyBatisGX 使用问题、ORM 设计与 SQL 架构实践。

微信:xcc137396549
备注:进 MyBatisGX 群

## 相关链接

- 官网:http://www.mybatisgx.com
- GitHub:https://github.com/cris-xue/mybatisgx
- 文档:http://www.mybatisgx.com/docs/intro/what-is-mybatisgx
