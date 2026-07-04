---
sidebar_position: 1
---

# 实体定义

> 使用 JPA 注解定义实体与表的映射

## 基本用法

### 实体注解

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    // getter/setter
}
```

| 注解 | 说明 |
|------|------|
| `@Entity` | 标记为实体类 |
| `@Table` | 指定表名 |
| `@Id` | 标记主键字段 |

### 字段注解

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_age")
    private Integer age;

    @Transient
    private String extra;  // 不映射到数据库

    // getter/setter
}
```

| 注解 | 说明 |
|------|------|
| `@Column` | 指定列名 |
| `@Transient` | 标记非持久化字段 |

## 主键策略

### 自增主键

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;
}
```

### 复合主键

```java
// 复合主键类
@IdClass
public class UserId {
    private Long userId;
    private Long orgId;
}

// 实体类
@Entity
@Table(name = "user")
public class User {

    @EmbeddedId
    private UserId id;
}
```

## 字段命名约定

### 支持的字段类型

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    // 基本类型
    private String name;
    private Integer age;
    private Long score;
    private Double salary;
    private Boolean active;

    // 时间类型
    private LocalDateTime createTime;
    private LocalDate birthDate;

    // 枚举类型
    private UserStatus status;

    // getter/setter
}
```

### 字段命名规则

- 使用驼峰命名法
- 框架自动转换为下划线命名（如 `userName` → `user_name`）
- 可通过配置关闭自动转换

```yaml
mybatisgx:
  configuration:
    map-underscore-to-camel-case: true
```

## 特殊字段

### 逻辑删除字段

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @LogicDelete
    private Integer status;  // 0: 正常, 1: 删除
}
```

### 乐观锁字段

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @Version
    private Integer version;
}
```

### 自动生成字段

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;

    @GeneratedValue(InputUserValueProcessor.class)
    private Long inputUserId;
}
```

## 注意事项

1. **@Entity 和 @Table 必须同时使用**：`@Entity` 标记实体，`@Table` 指定表名

2. **主键必须标记 @Id**：框架依赖主键进行 CRUD 操作

3. **字段类型建议使用包装类**：如 `Integer` 而非 `int`，避免默认值问题

4. **关联字段单独配置**：一对一、一对多等关联使用专门注解，详见 [关联查询](../relation/overview)

## 下一步

- 了解 [DAO 接口](./dao)
- 学习 [增删改查](./crud)
