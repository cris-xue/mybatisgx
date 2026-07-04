---
sidebar_position: 3
---

# 字段值生成

> @GeneratedValue 注解与 ValueProcessor 接口

## 概述

字段值生成允许在特定生命周期阶段自动生成字段值，用于主键生成、审计、加密、脱敏等场景。

## 基本用法

### 实体定义

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    @Column(name = "input_user_id")
    @GeneratedValue(InputUserValueProcessor.class)
    private Long inputUserId;

    @Column(name = "input_time")
    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;
}
```

## @GeneratedValue 属性

```java
@GeneratedValue({
    IdValueProcessor.class,
    InputUserValueProcessor.class
})
private Long id;
```

| 属性 | 说明 |
|------|------|
| `value` | ValueProcessor 实现类数组，按顺序执行 |

## ValueProcessor 接口

```java
public interface ValueProcessor {

    /**
     * 是否支持当前字段
     */
    boolean supports(FieldMeta fieldMeta);

    /**
     * 支持的生命周期阶段
     */
    EnumSet<ValueProcessPhase> phases();

    /**
     * 执行值处理
     */
    Object process(ValueProcessContext context);
}
```

## ValueProcessPhase 生命周期

| 阶段 | 说明 | 触发时机 |
|------|------|----------|
| `INSERT` | 新增阶段 | insert / insertSelective |
| `UPDATE` | 更新阶段 | updateById / updateByIdSelective |
| `DELETE` | 删除阶段 | deleteById / deleteByXxx |

## 自定义 ValueProcessor

### 主键生成器

```java
public class IdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "id".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // 返回雪花 ID
        return SnowflakeIdGenerator.nextId();
    }
}
```

### 录入时间生成器

```java
public class InputTimeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "inputTime".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return LocalDateTime.now();
    }
}
```

### 录入人生成器

```java
public class InputUserValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "inputUserId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // 从上下文获取当前用户
        return UserContext.getCurrentUserId();
    }
}
```

### 字段加密生成器

```java
public class EncryptValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return fieldMeta.getJavaColumnName().endsWith("Encrypt");
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        Object value = context.getValue();
        if (value == null) {
            return null;
        }
        // 加密处理
        return EncryptUtils.encrypt(value.toString());
    }
}
```

### 字段脱敏生成器

```java
public class DesensitizeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "phone".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        Object value = context.getValue();
        if (value == null) {
            return null;
        }
        String phone = value.toString();
        // 脱敏处理：138****8888
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
```

## 多阶段处理

一个 ValueProcessor 可以支持多个阶段：

```java
public class AuditValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return true;  // 支持所有字段
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        // 同时支持新增和更新
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        ValueProcessPhase phase = context.getPhase();

        if (phase == ValueProcessPhase.INSERT) {
            // 新增时的处理
            return generateInsertValue(context);
        } else if (phase == ValueProcessPhase.UPDATE) {
            // 更新时的处理
            return generateUpdateValue(context);
        }

        return context.getValue();
    }
}
```

## 多处理器组合

一个字段可以配置多个处理器，按顺序执行：

```java
@GeneratedValue({
    ValidateValueProcessor.class,   // 先校验
    TransformValueProcessor.class,  // 再转换
    EncryptValueProcessor.class     // 最后加密
})
private String phone;
```

## 完整示例

```java
// 用户实体
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    private String phone;

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

    @Version
    private Integer version;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}

// 使用
User user = new User();
user.setName("张三");
user.setPhone("13800138000");

userDao.insert(user);
// id, inputUserId, inputTime 自动填充

user.setName("李四");
userDao.updateById(user);
// updateUserId, updateTime 自动填充
```

## 注意事项

1. **处理器顺序**：多个处理器按数组顺序执行，前一个的输出是后一个的输入

2. **null 值处理**：处理器需要处理 null 值的情况

3. **性能考虑**：避免在处理器中执行耗时操作

4. **supports 方法**：用于判断处理器是否适用于当前字段

## 下一步

- 学习 [数据审计](/docs/advanced/audit)
- 了解 [批量操作](/docs/advanced/batch-operation)
