---
sidebar_position: 9
---

# 投影 DTO

> 部分字段返回，减少数据传输

## 概述

投影 DTO 用于解决某些接口不需要返回实体所有字段，或者不需要关联查询的场景。通过定义 DTO 类，只查询和返回需要的字段。

## 基本用法

### 定义投影 DTO

```java
public class UserNameDTO {

    private Long id;

    private String name;

    // getter/setter
}
```

### 使用投影 DTO

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 返回投影 DTO
    List<UserNameDTO> findNameByAgeGt(Integer age);
}
```

### 查询结果

```java
List<UserNameDTO> users = userDao.findNameByAgeGt(20);
// 只包含 id 和 name 字段
```

## 使用场景

### 1. 列表页面只显示部分字段

```java
// 列表 DTO
public class UserListDTO {

    private Long id;

    private String name;

    private String dept;

    // 不包含详细信息字段
}

// DAO 方法
List<UserListDTO> findListByDept(String dept);
```

### 2. 下拉选项只需要 ID 和名称

```java
// 选项 DTO
public class UserOptionDTO {

    private Long id;

    private String name;
}

// DAO 方法
List<UserOptionDTO> findOptionByStatus(Integer status);
```

### 3. 统计报表

```java
// 统计 DTO
public class UserStatDTO {

    private String dept;

    private Integer count;

    private Double avgAge;
}

// DAO 方法
List<UserStatDTO> statByDept();
```

## 字段校验规则

投影字段校验为**软校验**：

- 投影中的字段在实体中：放在 SQL 查询字段和结果集中
- 投影中的字段不在实体中：不放在 SQL 查询字段和结果集中

```java
public class UserDTO {

    private Long id;          // 实体中有，会查询

    private String name;      // 实体中有，会查询

    private String extra;     // 实体中没有，会被忽略
}
```

## 与关联查询的关系

使用投影 DTO 时，不会触发关联查询：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @OneToOne(mappedBy = "user")
    private UserDetail userDetail;  // 关联字段
}

// 使用投影 DTO
public class UserNameDTO {
    private Long id;
    private String name;
    // 不包含 userDetail，不会触发关联查询
}

List<UserNameDTO> users = userDao.findNameByAgeGt(20);
// userDetail 不会被查询
```

## 自定义查询投影

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 条件查询投影
    List<UserNameDTO> findNameByAgeGt(Integer age);

    // 分页查询投影
    Page<UserListDTO> findPageByDept(String dept, Pageable pageable);

    // 排序查询投影
    List<UserOptionDTO> findOptionByStatusOrderByCreateTimeDesc(Integer status);
}
```

## 完整示例

```java
// 投影 DTO 定义
public class UserListDTO {

    private Long id;

    private String name;

    private Integer age;

    private String deptName;  // 关联部门名称
}

// DAO 接口
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    List<UserListDTO> findListDTOByAgeGt(Integer age);
}

// Service 使用
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<UserListDTO> getUserList(Integer minAge) {
        return userDao.findListDTOByAgeGt(minAge);
    }
}
```

## 注意事项

1. **DTO 字段名需与实体一致**：字段名应与实体类字段名相同，框架才能正确映射

2. **软校验不报错**：DTO 中定义了实体没有的字段，不会报错，只是被忽略

3. **不触发关联查询**：使用投影 DTO 时，实体的关联字段不会被查询

4. **类型需要兼容**：DTO 字段类型应与实体字段类型兼容

## 与 QueryEntity 的区别

| 特性 | 投影 DTO | QueryEntity |
|------|----------|-------------|
| 用途 | 定义返回字段 | 定义查询条件 |
| 继承实体 | 不需要 | 通常继承实体 |
| 字段定义 | 需要返回的字段 | 查询条件字段 |
| 关联查询 | 不触发 | 正常触发 |

## 下一步

- 了解 [关联查询](../relation/overview)
- 学习 [高级功能](../advanced/logic-delete)
