---
sidebar_position: 7
---

# 分页查询

> Pageable + Page 分页查询指南

## 概述

MyBatisGX 提供内置的分页查询支持，使用 `Pageable` 作为分页参数，`Page` 作为返回结果。

## 基本用法

### 内置分页方法

```java
// SelectDao 内置的分页方法
Page<User> findPage(UserQuery query, Pageable pageable);
```

### 使用示例

```java
// 构建分页参数
Pageable pageable = new Pageable(1, 10);  // 第1页，每页10条

// 构建查询条件
UserQuery query = new UserQuery();
query.setNameLike("张");

// 执行分页查询
Page<User> page = userDao.findPage(query, pageable);

// 获取结果
long total = page.getTotal();       // 总记录数
List<User> list = page.getList();   // 当前页数据
```

## Pageable 分页参数

### 构造方法

```java
// 基本构造
Pageable pageable = new Pageable(pageNo, pageSize);

// 示例
Pageable pageable = new Pageable(1, 10);   // 第1页，每页10条
Pageable pageable = new Pageable(2, 20);   // 第2页，每页20条
```

### 排序设置

```java
Pageable pageable = new Pageable(1, 10);

// 添加排序
pageable.addSort("create_time", "DESC");
pageable.addSort("name", "ASC");
```

### 属性说明

| 属性 | 类型 | 说明 |
|------|------|------|
| `pageNo` | Integer | 页码（从1开始） |
| `pageSize` | Integer | 每页条数 |
| `sorts` | List\<Sort\> | 排序条件列表 |

## Page 返回结果

### 属性说明

| 属性 | 类型 | 说明 |
|------|------|------|
| `total` | long | 总记录数 |
| `list` | List\<T\> | 当前页数据列表 |

### 使用示例

```java
Page<User> page = userDao.findPage(query, pageable);

// 总记录数
long total = page.getTotal();

// 当前页数据
List<User> users = page.getList();

// 总页数（需计算）
int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());
```

## 自定义分页查询

在 DAO 接口中定义自定义分页方法：

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 方法名派生分页
    Page<User> findByNameLike(String name, Pageable pageable);

    // 带条件的分页
    Page<User> findByAgeGtOrderByCreateTimeDesc(Integer age, Pageable pageable);
}
```

## PageHelper 集成

MyBatisGX 分页使用了 PageHelper 分页插件，可以直接使用 PageHelper 进行分页：

```java
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

// 使用 PageHelper
PageHelper.startPage(1, 10);
List<User> users = userDao.findList(query);
PageInfo<User> pageInfo = new PageInfo<>(users);
```

### PageHelper vs 内置分页

| 方式 | 优点 | 缺点 |
|------|------|------|
| 内置分页 | 类型安全，返回 Page 对象 | 需要定义 Pageable 参数 |
| PageHelper | 灵活，不改变方法签名 | 运行时设置，需手动包装结果 |

## 完整示例

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Page<User> searchUsers(String name, Integer minAge, int pageNo, int pageSize) {
        // 构建查询条件
        UserQuery query = new UserQuery();
        if (StringUtils.isNotBlank(name)) {
            query.setNameLike(name);
        }
        if (minAge != null) {
            query.setAgeGteq(minAge);
        }

        // 构建分页参数
        Pageable pageable = new Pageable(pageNo, pageSize);
        pageable.addSort("create_time", "DESC");

        // 执行查询
        return userDao.findPage(query, pageable);
    }
}
```

## 注意事项

1. **页码从1开始**：Pageable 的 pageNo 从 1 开始计数

2. **排序字段为数据库列名**：`addSort` 的第一个参数是数据库列名，不是 Java 属性名

3. **Page 对象不可复用**：每次查询返回新的 Page 对象

4. **COUNT 查询自动执行**：框架会自动执行 COUNT 查询获取总数

## 下一步

- 学习 [动态 SQL](./dynamic-sql)
- 了解 [投影 DTO](./projection)
