---
sidebar_position: 5
---

# 复合主键

> @EmbeddedId 与 @IdClass 注解使用指南

## 概述

复合主键用于表中有多个字段联合作为主键的场景。

## @EmbeddedId 方式

### 定义复合主键类

```java
@IdClass
public class UserId {

    private Long userId;

    private Long orgId;

    // 必须实现 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(this.userId, userId.userId) &&
               Objects.equals(this.orgId, userId.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, orgId);
    }
}
```

### 实体定义

```java
@Entity
@Table(name = "user_org")
public class UserOrg {

    @EmbeddedId
    private UserId id;

    private String role;

    private LocalDateTime joinTime;
}
```

### 数据库结构

```sql
CREATE TABLE user_org (
    user_id BIGINT,
    org_id BIGINT,
    role VARCHAR(50),
    join_time DATETIME,
    PRIMARY KEY (user_id, org_id)
);
```

### 使用方式

```java
// 创建复合主键
UserId id = new UserId();
id.setUserId(1L);
id.setOrgId(100L);

// 查询
UserOrg userOrg = userOrgDao.findById(id);

// 创建实体
UserOrg entity = new UserOrg();
entity.setId(id);
entity.setRole("admin");
userOrgDao.insert(entity);
```

## @IdClass 方式

### 定义复合主键类

```java
@IdClass
public class OrderId {

    private Long orderId;

    private String orderType;

    // 必须实现 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(this.orderId, orderId.orderId) &&
               Objects.equals(this.orderType, orderId.orderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderType);
    }
}
```

### 实体定义

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private Long orderId;

    @Id
    private String orderType;

    private Long userId;

    private BigDecimal amount;
}
```

### 使用方式

```java
// 直接设置各个主键字段
Order order = new Order();
order.setOrderId(1L);
order.setOrderType("NORMAL");
order.setUserId(100L);
order.setAmount(new BigDecimal("100.00"));

// 插入
orderDao.insert(order);

// 查询需要使用复合主键类
OrderId id = new OrderId();
id.setOrderId(1L);
id.setOrderType("NORMAL");
Order result = orderDao.findById(id);
```

## 复合主键自动生成

如果复合主键字段需要自动生成值，需要在复合主键类中标注：

### 定义复合主键类

```java
@IdClass
public class UserId {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long userId;

    @Id
    private Long orgId;
}
```

### 实体定义

```java
@Entity
@Table(name = "user_org")
public class UserOrg {

    @EmbeddedId
    @GeneratedValue(CompositeIdValueProcessor.class)
    private UserId id;
}
```

## 两种方式对比

| 特性 | @EmbeddedId | @IdClass |
|------|-------------|----------|
| 主键访问 | `entity.getId().getUserId()` | `entity.getUserId()` |
| 字段位置 | 复合主键类中 | 实体类中 |
| 语义清晰度 | 更清晰 | 更直观 |
| 推荐场景 | 复杂复合主键 | 简单复合主键 |

## 完整示例

```java
// 复合主键类
@IdClass
public class UserId implements Serializable {

    private Long userId;

    private Long orgId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId that = (UserId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(orgId, that.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, orgId);
    }
}

// 实体类
@Entity
@Table(name = "user_org")
public class UserOrg {

    @EmbeddedId
    private UserId id;

    private String role;

    private LocalDateTime joinTime;

    // 便捷方法
    public Long getUserId() {
        return id != null ? id.getUserId() : null;
    }

    public Long getOrgId() {
        return id != null ? id.getOrgId() : null;
    }
}

// DAO 接口
@Repository
public interface UserOrgDao extends SimpleDao<UserOrg, UserOrgQuery, UserId> {
}

// Service 使用
@Service
public class UserOrgService {

    @Autowired
    private UserOrgDao userOrgDao;

    public UserOrg getUserOrg(Long userId, Long orgId) {
        UserId id = new UserId();
        id.setUserId(userId);
        id.setOrgId(orgId);
        return userOrgDao.findById(id);
    }

    public void addUserToOrg(Long userId, Long orgId, String role) {
        UserId id = new UserId();
        id.setUserId(userId);
        id.setOrgId(orgId);

        UserOrg userOrg = new UserOrg();
        userOrg.setId(id);
        userOrg.setRole(role);
        userOrg.setJoinTime(LocalDateTime.now());

        userOrgDao.insert(userOrg);
    }
}
```

## 注意事项

1. **必须实现 Serializable**：复合主键类需要实现 Serializable 接口

2. **必须实现 equals 和 hashCode**：复合主键类必须正确实现这两个方法

3. **主键类型不能是基础类型**：复合主键必须是类类型，不能是 int、long 等

4. **无参构造函数**：复合主键类需要有无参构造函数

5. **DAO 泛型参数**：SimpleDao 的第三个泛型参数是复合主键类型

## 下一步

- 学习 [批量操作](/docs/advanced/batch-operation)
- 了解 [常见问题](/docs/faq)
