---
sidebar_position: 6
---

# 批量操作

> @BatchOperation、@BatchSize、@BatchData 注解使用指南

## 概述

批量操作用于一次性处理大量数据，比逐条操作更高效。MyBatisGX 内置批量操作方法，并支持自定义批量方法。

## 内置批量方法

### CurdDao 内置方法

```java
public interface CurdDao<ENTITY, ID> extends Dao {

    // 批量插入
    default int insertBatch(List<ENTITY> entityList) {
        return this.insertBatch(entityList, 1000);
    }

    int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);

    // 批量删除
    default int deleteBatchById(List<ID> ids) {
        return this.deleteBatchById(ids, 1000);
    }

    int deleteBatchById(@BatchData List<ID> ids, @BatchSize int batchSize);

    // 批量更新
    default int updateBatchById(List<ENTITY> entityList) {
        return this.updateBatchById(entityList, 1000);
    }

    int updateBatchById(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);
}
```

## 使用内置批量方法

### 批量插入

```java
List<User> users = Arrays.asList(
    new User("张三", 25),
    new User("李四", 30),
    new User("王五", 28)
);

// 使用默认批次大小（1000）
userDao.insertBatch(users);

// 自定义批次大小
userDao.insertBatch(users, 500);
```

### 批量删除

```java
List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);

// 使用默认批次大小
userDao.deleteBatchById(ids);

// 自定义批次大小
userDao.deleteBatchById(ids, 100);
```

### 批量更新

```java
List<User> users = userDao.findList(query);
// 修改数据
users.forEach(u -> u.setStatus(1));

// 批量更新
userDao.updateBatchById(users);
```

## 自定义批量方法

### 注解说明

| 注解 | 说明 |
|------|------|
| `@BatchOperation` | 标记方法为批量操作 |
| `@BatchData` | 标记参数为批量数据 |
| `@BatchSize` | 标记参数为批次大小 |

### 自定义批量插入

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @BatchOperation
    int insertUsers(@BatchData List<User> users, @BatchSize int batchSize);
}

// 使用
userDao.insertUsers(users, 500);
```

### 自定义批量更新

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @BatchOperation
    int updateStatusBatch(@BatchData List<User> users, @BatchSize int batchSize);
}

// 使用
List<User> users = ...;
users.forEach(u -> u.setStatus(1));
userDao.updateStatusBatch(users, 100);
```

### 自定义批量删除

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @BatchOperation
    int deleteByNames(@BatchData List<String> names, @BatchSize int batchSize);
}

// 使用
List<String> names = Arrays.asList("张三", "李四", "王五");
userDao.deleteByNames(names, 100);
```

## 批次大小选择

| 批次大小 | 适用场景 |
|----------|----------|
| 100-500 | 数据量大、单条数据复杂 |
| 500-1000 | 一般场景（默认 1000） |
| 1000-5000 | 数据量小、单条数据简单 |

### 性能考虑

```
┌─────────────────────────────────────────────────────────────────┐
│                     批次大小与性能                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  批次太小：                                                      │
│  - 大量 SQL 执行次数                                             │
│  - 网络往返开销大                                                │
│                                                                 │
│  批次太大：                                                      │
│  - 单次 SQL 过长                                                 │
│  - 内存占用高                                                    │
│  - 可能触发数据库限制                                            │
│                                                                 │
│  建议：                                                          │
│  - 根据数据复杂度调整                                            │
│  - 参考数据库 max_allowed_packet 配置                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
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

    private Integer age;

    private Integer status;
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 自定义批量方法
    @BatchOperation
    int insertUsers(@BatchData List<User> users, @BatchSize int batchSize);

    @BatchOperation
    int updateStatusBatch(@BatchData List<User> users, @BatchSize int batchSize);
}

// Service 使用
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void importUsers(List<UserDTO> dtos) {
        List<User> users = dtos.stream()
            .map(dto -> {
                User user = new User();
                user.setName(dto.getName());
                user.setAge(dto.getAge());
                return user;
            })
            .collect(Collectors.toList());

        // 批量插入，每 500 条一批
        userDao.insertBatch(users, 500);
    }

    public void batchUpdateStatus(List<Long> ids, Integer status) {
        List<User> users = ids.stream()
            .map(id -> {
                User user = new User();
                user.setId(id);
                user.setStatus(status);
                return user;
            })
            .collect(Collectors.toList());

        userDao.updateBatchById(users, 1000);
    }
}
```

## 批量操作与其他功能配合

### 与字段值生成配合

批量操作时，字段值生成器会正常执行：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    @Column(name = "input_time")
    @GeneratedValue(InputTimeValueProcessor.class)
    private LocalDateTime inputTime;
}

// 批量插入时，id 和 inputTime 自动填充
userDao.insertBatch(users);
```

### 与乐观锁配合

批量更新时会检查版本号：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @Version
    private Integer version;
}

// 批量更新时会检查 version
userDao.updateBatchById(users);
```

### 与逻辑删除配合

批量删除时会执行逻辑删除：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @LogicDelete
    private Integer status;
}

// 批量删除会执行 UPDATE SET status = 1
userDao.deleteBatchById(ids);
```

## 注意事项

1. **内存考虑**：大批量操作注意内存占用，建议分批处理

2. **事务边界**：批量操作应在事务内执行

3. **失败处理**：批量操作中某条失败可能影响整批，需要业务处理

4. **数据库限制**：注意数据库单次 SQL 大小限制

5. **连接超时**：大批量操作可能需要调整数据库连接超时配置

## 下一步

- 查看 [常见问题](/docs/faq)
