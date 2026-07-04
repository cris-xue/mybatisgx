---
title: 为什么我不喜欢用 MyBatis Plus、JPA 框架，更愿意使用 MyBatis
authors: [XueChengCheng]
tags: [mybatis, mybatis-plus, jpa, 架构, orm]
description: 从 MyBatis Plus、JPA 到 MyBatis 的选择困境，以及 MyBatisGX 的解决之道
---

快速开发 vs 架构清晰，这是持久层框架选择中最常见的两难。

大多数团队选择 MyBatis Plus：
- 第 1 个月：开发速度快 ✅
- 第 6 个月：代码难维护 ❌

少数团队选择 MyBatis：
- 第 1 个月：写 XML 繁琐 ❌
- 第 6 个月：架构清晰 ✅

这就是技术选型的困境：**前期效率 vs 长期质量**

但真的必须在两者之间做选择吗？

<!-- truncate -->

## 一、MyBatis Plus 的问题：持久层逻辑泄露

MyBatis Plus 最大的问题，不是功能不够强大，而是**侵入了不该侵入的地方**。

### 问题的本质：Service 层被污染

看看这段代码：

```java
// ═══════════════════════════════════════════
// MyBatis Plus 方式（Service 层 50 行）
// ═══════════════════════════════════════════
@Service
public class OrderService {

    public List<Order> searchOrders(OrderQueryDTO query) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }

        // 时间范围
        if (query.getStartTime() != null) {
            wrapper.ge(Order::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(Order::getCreateTime, query.getEndTime());
        }

        // 金额范围
        if (query.getMinAmount() != null) {
            wrapper.ge(Order::getTotalAmount, query.getMinAmount());
        }
        if (query.getMaxAmount() != null) {
            wrapper.le(Order::getTotalAmount, query.getMaxAmount());
        }

        // 支付方式
        if (query.getPayType() != null) {
            wrapper.eq(Order::getPayType, query.getPayType());
        }

        // 配送方式
        if (query.getDeliveryType() != null) {
            wrapper.eq(Order::getDeliveryType, query.getDeliveryType());
        }

        // 关键词搜索（多字段）
        if (StringUtils.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w
                .like(Order::getOrderNo, query.getKeyword())
                .or()
                .like(Order::getUserName, query.getKeyword())
                .or()
                .like(Order::getUserPhone, query.getKeyword())
            );
        }

        // ...（省略类似的 20 行条件判断）

        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
}
```

这段代码有什么问题？

**1. Service 层混杂大量数据库逻辑**
- 数据库字段名硬编码：`Order::getStatus`、`Order::getCreateTime`
- 查询条件构建：`wrapper.eq()`、`wrapper.ge()`、`wrapper.like()`
- 这些都是持久层的关注点，不应该出现在业务层

**2. 代码难以复用和测试**
- 相似的 Wrapper 构建代码散落在各个 Service 方法中
- 单元测试要 mock Mapper，还要验证 Wrapper 的正确性
- 查询逻辑和业务逻辑耦合在一起

**3. 维护成本快速上升**
- 随着查询条件增加，Service 方法越来越长
- 业务逻辑被淹没在查询构建代码中
- 新人看代码要花很长时间才能找到核心逻辑

### 架构边界被破坏

传统的三层架构应该是这样的：

```
┌─────────────────────────────────────────┐
│  Controller 层                          │
│  ├─ 接收 HTTP 请求                      │
│  └─ 调用 Service                        │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Service 层（业务逻辑层）                │
│  ├─ 业务流程编排                        │
│  ├─ 事务控制                            │
│  └─ 调用 DAO                            │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  DAO 层（数据访问层）                    │
│  ├─ 封装数据库访问                      │
│  └─ SQL 执行                            │
└─────────────────────────────────────────┘
```

但 MyBatis Plus 的实际情况是：

```
┌─────────────────────────────────────────┐
│  Service 层（混乱层）                    │
│  ├─ 业务流程编排                        │
│  ├─ Wrapper 构建 ◄── 数据库逻辑！       │
│  ├─ 字段名硬编码 ◄── 数据库逻辑！       │
│  ├─ 条件拼装     ◄── 数据库逻辑！       │
│  └─ null 判断    ◄── 数据库逻辑！       │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Mapper 接口（几乎是摆设）               │
│  └─ 只提供基础 CRUD 方法                │
└─────────────────────────────────────────┘
```

**持久层逻辑泄露到了 Service 层！**

### 更致命的问题：接管成本高

当你需要优化 SQL（比如加 JOIN）时，会发现：

```
MyBatis Plus 的接管路径：

Step 1: Service 层有 50 行 Wrapper 代码
Step 2: 决定写 XML 优化 SQL
Step 3: 改 Service 代码（！）
        从 selectList(wrapper)
        改为 selectByCustomQuery(...)
Step 4: 调用方可能也要改

接管成本：高 ❌
风险：大 ❌
```

这就是 MyBatis Plus 的核心问题：**快是快了，但把代码写脏了。**

## 二、JPA 的问题：灵活性不够

你可能会说："那 JPA 呢？它不用写 Wrapper，应该更好吧？"

JPA 确实避免了 Wrapper 的问题，但它有自己的痛点。

### 痛点 1：动态查询很繁琐

JPA 处理动态查询，只能用 Specification：

```java
public Specification<User> buildSpec(UserQueryDTO query) {
    return (root, criteriaQuery, cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (query.getName() != null) {
            predicates.add(cb.like(root.get("name"), "%" + query.getName() + "%"));
        }

        if (query.getMinAge() != null) {
            predicates.add(cb.gt(root.get("age"), query.getMinAge()));
        }

        if (query.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), query.getStatus()));
        }

        // 字段名是字符串，没有类型安全
        // 写起来很啰嗦

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}

// 使用
List<User> users = userRepository.findAll(buildSpec(query));
```

问题：
- 字段名是字符串（`root.get("name")`），没有类型安全
- 代码啰嗦，远不如 SQL 直观
- Specification 不易复用

### 痛点 2：黑盒运行时

JPA 最被诟病的是它的"魔法"：

```java
@Transactional
public void updateUser(Long userId, String newName) {
    User user = userRepository.findById(userId).get();
    user.setName(newName);
    // 没有显式 save()，但数据库已经更新了？！
    // 什么时候执行的 SQL？
    // 执行了哪些 SQL？
    // 你完全不知道...
}
```

这段代码背后可能发生了：
1. SELECT 查询
2. 脏检查
3. 事务提交时自动 UPDATE
4. 可能还触发了懒加载...

**你无法精确控制 SQL 的执行时机。**

### 痛点 3：N+1 问题

```java
List<User> users = userRepository.findAll();

for (User user : users) {
    // 看起来只是取个属性，实际上...
    List<Order> orders = user.getOrders(); // ← 触发 SQL！
    // 如果有 100 个用户，这里会执行 100 次 SQL！
}
```

### 痛点 4：接管成本更高

当需要手写 SQL 优化时：

```java
// Step 1: 写 Native SQL
@Query(value = "SELECT u.*, o.* FROM user u " +
               "LEFT JOIN orders o ON u.id = o.user_id " +
               "WHERE u.status = ?1",
       nativeQuery = true)
List<Object[]> findUsersWithOrders(Integer status);

// Step 2: 结果映射变复杂
// 返回的是 Object[]，需要手动映射...
// 或者配置 @SqlResultSetMapping...
// 总之，很麻烦！

接管成本：极高 ❌
```

JPA 的问题总结：**看起来很美好，但失去了对 SQL 的控制。**

## 三、MyBatis：两难的选择

经过对比，你会发现 MyBatis 其实很不错：

### MyBatis 的优点

**1. 架构清晰**
- 持久层逻辑在 DAO + XML
- Service 层纯净，只有业务逻辑
- 边界清晰，职责明确

**2. SQL 完全可控**
- SQL 写在 XML 里，一目了然
- 可以精确优化每一个查询
- 没有黑盒，没有魔法

**3. 接管成本为零**
- 因为一开始就是手写 SQL
- 不存在"接管"的问题

### 对比表格

| 维度 | MyBatis Plus | JPA | MyBatis |
|------|-------------|-----|---------|
| **Service 层代码** | 混杂 Wrapper | 理想情况纯净 | 纯业务逻辑 ✅ |
| **持久层逻辑位置** | Service 层 ❌ | Repository | DAO + XML ✅ |
| **SQL 可见性** | 运行时生成 | 黑盒 ❌ | XML 可见 ✅ |
| **SQL 可控性** | 中等 | 差 ❌ | 完全可控 ✅ |
| **动态查询** | Wrapper（易写） | Specification（繁琐） | `<if>` 标签（清晰）✅ |
| **接管成本** | 高 ❌ | 极高 ❌ | 无需接管 ✅ |
| **学习曲线** | 低 | 高 | 中等 |

**MyBatis 几乎完胜！**

### 但是...

MyBatis 有一个明显的痛点：

```xml
<!-- 就查个用户列表，也要写 XML... -->
<select id="findByNameLike" resultMap="BaseResultMap">
  SELECT * FROM user WHERE name LIKE CONCAT('%', #{name}, '%')
</select>

<select id="findByAgeGt" resultMap="BaseResultMap">
  SELECT * FROM user WHERE age > #{age}
</select>

<select id="findByStatus" resultMap="BaseResultMap">
  SELECT * FROM user WHERE status = #{status}
</select>

<!-- 类似的 SQL 要写几十个... -->
```

**简单 CRUD 也要写 XML，确实很繁琐。**

这也是为什么很多团队选择 MyBatis Plus 的原因——它解决了这个痛点。

但问题是：**它在解决繁琐的同时，破坏了架构。**

能否有一种方式：
- ✅ 像 MyBatis 一样架构清晰
- ✅ 像 MyBatis Plus 一样简单 CRUD 不用写 XML
- ✅ 保留 MyBatis 的 SQL 可控性

**能否两全其美？**

## 四、MyBatisGX：保留优点，解决痛点

这就是 MyBatisGX 的设计初衷。

### 核心设计思想

```
MyBatisGX = MyBatis 的架构清晰 + JPA 的开发便利 - JPA 的黑盒

目标：
1. 保留 MyBatis 的 SQL 可控性
2. 解决简单 CRUD 的繁琐性
3. 避免持久层逻辑泄露到 Service
4. 接管成本保持为零
```

### 关键特性 1：持久层逻辑不泄露

这是 MyBatisGX 最核心的差异化优势。

```java
// ═══════════════════════════════════════════
// MyBatis Plus 方式（Service 层 50 行）
// ═══════════════════════════════════════════
@Service
public class OrderService {
    public List<Order> searchOrders(OrderQueryDTO query) {
        // 50 行 Wrapper 构建代码...
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        // ...（省略 40 行）

        return orderMapper.selectList(wrapper);
    }
}

// ═══════════════════════════════════════════
// MyBatisGX 方式（Service 层 5 行）
// ═══════════════════════════════════════════
@Service
public class OrderService {
    public List<Order> searchOrders(OrderQuery query) {
        return orderDao.findList(query);
    }
}

// 查询逻辑在 DAO 层
public interface OrderDao extends SimpleDao<Order, OrderQuery, Long> {
    @Dynamic
    List<Order> findList(OrderQuery query);
}

// QueryEntity 定义（类型安全）
@QueryEntity
public class OrderQuery extends Order {
    private Integer statusEq;
    private Date createTimeGe;
    private Date createTimeLe;
    private BigDecimal totalAmountGe;
    private BigDecimal totalAmountLe;
    private String orderNoLike;
    private String userNameLike;
    // ...null 字段自动忽略
}
```

**从 50 行缩减到 5 行，Service 层纯净！**

优势：
- ✅ Service 层只有业务逻辑
- ✅ 持久层逻辑收敛到 DAO
- ✅ 类型安全，易于测试
- ✅ 查询逻辑可复用

### 关键特性 2：简单 SQL 自动生成

解决 MyBatis 的繁琐问题。

```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 简单查询（不需要写 XML）
    List<User> findByNameLike(String name);

    // 复杂条件组合
    List<User> findByNameLikeAndAgeGtAndStatusIn(
        String name,
        Integer age,
        List<Integer> statusList
    );

    // 排序
    List<User> findByStatusOrderByCreateTimeDesc(Integer status);

    // 分页
    Page<User> findByAgeGt(Integer age, Pageable pageable);
}

// 框架自动生成 SQL，无需写 XML ✅
// 但如果需要优化，XML 随时可以接管 ✅
```

支持的查询关键字：
- 比较：`Lt`、`Lteq`、`Gt`、`Gteq`、`Eq`
- 模糊：`Like`、`StartingWith`、`EndingWith`
- 范围：`Between`、`In`
- 空值：`IsNull`、`IsNotNull`
- 逻辑：`And`、`Or`

### 关键特性 3：接管成本为零

保留 MyBatis 的核心优势。

```
场景：性能需要优化，需要写 JOIN

┌──────────────────────────────────────┐
│ MyBatis Plus 的接管路径              │
├──────────────────────────────────────┤
│ Step 1: Service 层有 50 行 Wrapper   │
│ Step 2: 写 XML                       │
│ Step 3: 改 Service 代码（！）        │
│         orderMapper.selectList()     │
│         改为 selectByCustomQuery()   │
│ Step 4: 调用方可能也要改             │
│                                      │
│ 接管成本：高 ❌                      │
│ 风险：大 ❌                          │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ MyBatisGX 的接管路径                 │
├──────────────────────────────────────┤
│ Step 1: DAO 方法                     │
│   findByShopIdAndStatus(...)         │
│                                      │
│ Step 2: 写 XML（Service 不动！）     │
│   <select id="findByShopIdAndStatus">│
│     SELECT o.*, s.name, u.name       │
│     FROM order o                     │
│     LEFT JOIN shop s ON ...          │
│     LEFT JOIN user u ON ...          │
│     WHERE o.shop_id = #{shopId}      │
│       AND o.status = #{status}       │
│   </select>                          │
│                                      │
│ 接管成本：零 ✅                      │
│ 风险：低 ✅                          │
│ Service 代码：一行都不用改 ✅        │
└──────────────────────────────────────┘
```

**XML 优先级最高，随时可以覆盖自动生成的 SQL。**

### 综合对比表格

| 维度 | MP | JPA | MyBatis | MyBatisGX   |
|------|----|----|---------|-------------|
| **架构清晰** | ❌ 泄露 | ✅ | ✅ | ✅           |
| **简单 CRUD** | ✅ 不用写 XML | ✅ | ❌ 要写 XML | ✅ 不用写       |
| **SQL 可控** | ⚠️ 中等 | ❌ 黑盒 | ✅ 完全可控 | ✅ 完全可控      |
| **动态查询** | ✅ Wrapper 易写 | ⚠️ Specification 繁琐 | ✅ `<if>` 清晰 | ✅ Dynamic注解 |
| **接管成本** | ❌ 高 | ❌ 极高 | ✅ 无需接管 | ✅ 零成本       |
| **类型安全** | ✅ Lambda | ✅ | ⚠️ 部分 | ✅           |
| **学习曲线** | 低 | 高 | 中 | 低           |

**MyBatisGX 在各个维度都有竞争力！**

### 其他特性

**1. 声明式关联查询**

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    // 声明式关联 + 批量抓取避免 N+1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.BATCH)  // 批量查询避免 N+1
    private Org org;
}

// 四种抓取模式：SIMPLE / BATCH / JOIN / NONE
// 精确控制性能，不会有 JPA 的 N+1 问题

// NONE 模式：完全不查询，手动控制
@ManyToOne
@Fetch(FetchMode.NONE)
private Avatar avatar;  // 从缓存/CDN 加载，不走数据库
```

**2. 逻辑删除、乐观锁**

```java
@Entity
public class User {
    @LogicDelete
    private Integer deleted;  // 0: 正常，1: 已删除

    @Version
    private Integer version;  // 乐观锁
}

// 删除时自动转换为 UPDATE
userDao.deleteById(userId);
// UPDATE user SET deleted = 1 WHERE id = ? AND deleted = 0
```

**3. 字段自动填充**

```java
@Entity
public class User {
    @GeneratedValue(CreateTimeProcessor.class)
    private Date createTime;

    @GeneratedValue(UpdateTimeProcessor.class)
    private Date updateTime;
}

// 插入/更新时自动填充
```

### 适用场景

MyBatisGX 特别适合：

- ✅ 需要长期维护的项目（> 6 个月）
- ✅ 关心代码可维护性和架构清晰度
- ✅ 不想 Service 层被数据库逻辑污染
- ✅ 需要保留 SQL 完全控制权
- ✅ 从 MyBatis 项目升级（100% 兼容）

**如果你的项目：**
- 只活 3 个月（能跑就行）→ 用 MyBatis Plus 没问题
- 需要跨 10 种数据库 → 用 JPA 更合适
- 都是复杂 SQL 和报表 → 用 MyBatis 最合适

**但如果你的项目需要长期维护，关心架构质量，那 MyBatisGX 可能是最优解。**

## 五、总结：如何选择

回到文章开头的问题：**快速开发 vs 架构清晰，能否两全其美？**

答案是：**可以。**

但前提是：**不要把持久层逻辑写到业务层。**

### 选择框架 = 选择技术债务

```
MyBatis Plus:
  前期：快速开发 ✅
  后期：重构困难 ❌
  原因：持久层逻辑泄露

JPA:
  前期：开发便利 ✅
  后期：SQL 失控 ❌
  原因：黑盒运行时

MyBatis:
  前期：写 XML 繁琐 ❌
  后期：架构清晰 ✅
  原因：强制分层

MyBatisGX:
  前期：快速开发 ✅
  后期：架构清晰 ✅
  原因：DAO 层收敛 + 自动生成
```

### 核心观点

> **不要用短期效率换长期债务。**
>
> **好的框架不应该破坏架构，而应该保护架构。**

### 给技术选型者的建议

如果你正在选型，问自己三个问题：

1. **这个项目会活多久？**
    - 如果只活 3 个月，怎么快怎么来
    - 如果要活 1 年以上，认真考虑架构

2. **Service 层会被污染吗？**
    - 如果会，6 个月后你会后悔
    - 如果不会，那就放心用

3. **遇到性能问题时，能轻松优化吗？**
    - 如果要改 Service 代码，风险很大
    - 如果只改 XML，风险很小

**选择框架，就是选择项目的未来。**

---

**MyBatisGX 项目地址**：[https://github.com/cris-xue/mybatisgx](https://github.com/cris-xue/mybatisgx)

**在线文档**：[http://www.mybatisgx.com](http://www.mybatisgx.com)

你觉得MyBatisGX可以帮助到你，欢迎 Star 和试用 MyBatisGX。

如果你有不同的看法，欢迎在评论区讨论。
