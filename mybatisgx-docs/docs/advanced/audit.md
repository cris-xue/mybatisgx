---
sidebar_position: 4
---

# 数据审计

> 录入人/时间、修改人/时间自动填充

## 概述

数据审计用于自动记录数据的创建和修改信息，包括录入人、录入时间、修改人、修改时间等。

## 实现方式

数据审计基于字段值生成机制实现，需要定义相应的 ValueProcessor。

## 基本用法

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // 录入人
    @Column(name = "input_user_id")
    @GeneratedValue(InputUserValueProcessor.class)
    private Long inputUserId;

    // 录入时间
    @Column(name = "input_time")
    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;

    // 修改人
    @Column(name = "update_user_id")
    @GeneratedValue(UpdateUserValueProcessor.class)
    private Long updateUserId;

    // 修改时间
    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;
}
```

### 数据库结构

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    input_user_id BIGINT,      -- 录入人
    input_time DATETIME,       -- 录入时间
    update_user_id BIGINT,     -- 修改人
    update_time DATETIME       -- 修改时间
);
```

## ValueProcessor 实现

### 录入人处理器

```java
public class InputUserValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "inputUserId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        // 只在新增时触发
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // 获取当前登录用户 ID
        return UserContext.getCurrentUserId();
    }
}
```

### 录入时间处理器

```java
public class InputTimeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "inputTime".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        // 只在新增时触发
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return LocalDateTime.now();
    }
}
```

### 修改人处理器

```java
public class UpdateUserValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "updateUserId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        // 在新增和更新时都触发
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return UserContext.getCurrentUserId();
    }
}
```

### 修改时间处理器

```java
public class UpdateTimeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "updateTime".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        // 在新增和更新时都触发
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return LocalDateTime.now();
    }
}
```

## 用户上下文

### 上下文接口

```java
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getCurrentUserId() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
```

### 拦截器配置

```java
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        // 从请求中获取用户信息
        Long userId = getUserIdFromToken(request);
        UserContext.setCurrentUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.clear();
    }
}
```

## 完整示例

```java
// 用户实体
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @Column(name = "input_user_id")
    @GeneratedValue(InputUserValueProcessor.class)
    private Long inputUserId;

    @Column(name = "input_time")
    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;

    @Column(name = "update_user_id")
    @GeneratedValue(UpdateUserValueProcessor.class)
    private Long updateUserId;

    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// Service 使用
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void createUser(String name) {
        User user = new User();
        user.setName(name);
        // inputUserId, inputTime, updateUserId, updateTime 自动填充
        userDao.insert(user);
    }

    public void updateUserName(Long id, String newName) {
        User user = userDao.findById(id);
        user.setName(newName);
        // updateUserId, updateTime 自动填充
        userDao.updateById(user);
    }
}
```

## 扩展场景

### 审计字段基类

```java
@MappedSuperclass
public abstract class AuditEntity {

    @Column(name = "input_user_id")
    @GeneratedValue(InputUserValueProcessor.class)
    private Long inputUserId;

    @Column(name = "input_time")
    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;

    @Column(name = "update_user_id")
    @GeneratedValue(UpdateUserValueProcessor.class)
    private Long updateUserId;

    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;
}

// 继承基类
@Entity
@Table(name = "user")
public class User extends AuditEntity {

    @Id
    private Long id;

    private String name;
}
```

### 租户审计

```java
@Column(name = "tenant_id")
@GeneratedValue(TenantValueProcessor.class)
private Long tenantId;

public class TenantValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "tenantId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return TenantContext.getCurrentTenantId();
    }
}
```

## 注意事项

1. **用户上下文必需**：需要确保 UserContext 在操作前已设置用户信息

2. **线程安全**：使用 ThreadLocal 保证线程安全

3. **上下文清理**：请求结束后清理 ThreadLocal，避免内存泄漏

4. **批量操作**：批量操作时所有记录使用同一用户信息

## 下一步

- 学习 [复合主键](/docs/advanced/composite-key)
- 了解 [批量操作](/docs/advanced/batch-operation)
