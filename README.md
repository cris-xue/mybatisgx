# MyBatisGX 开发文档

## 1. 简介

MyBatisGX 是一个基于 MyBatis 的 ORM 框架，旨在提高开发效率并简化数据库操作。其中：
- "G" 表示加特林机枪，意味着执行速度快，开发效率高。
- "X" 是罗马数字 10，寓意十年磨一剑。

### 1.1 背景

传统 MyBatis 开发存在以下痛点：

1. 在 Service 层中编写大量数据库操作代码，导致业务逻辑与数据访问代码混杂，难以维护。
2. 即使是简单的 CRUD 操作也需要编写 XML 映射文件。
3. 对象之间的关联查询需要手动编写复杂 SQL。

MyBatisGX 解决了这些问题，并提供了丰富的功能特性。

### 1.2 特性概览

- 内置默认增删改查操作
- 支持根据方法名自动生成单表查询 SQL
- 支持基于注解的动态 SQL
- 字段值自动填充
- 逻辑删除支持
- 乐观锁机制
- 自动关联查询（一对一、一对多、多对一、多对多）
- 多租户支持
- 批量操作
- 分页查询
- Spring Boot 快速集成
- 复合主键支持

## 2. 快速开始

### 2.1 添加依赖

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot-starter</artifactId>
    <version>{最新版本}</version>
</dependency>
```

### 2.2 配置文件

在 `application.yml` 或 `application.properties` 中配置数据源：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 2.3 创建实体类

```java
@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Integer age;
    
    // getter/setter 省略
}
```

### 2.4 创建 DAO 接口

```java
public interface UserDao extends SimpleDao<User, Long> {
}
```

### 2.5 使用示例

```java
@Service
public class UserService {
    
    @Autowired
    private UserDao userDao;
    
    public void insertUser(User user) {
        userDao.insert(user);
    }
    
    public User getUserById(Long id) {
        return userDao.findById(id);
    }
    
    public List<User> findUsersByName(String name) {
        User condition = new User();
        condition.setName(name);
        return userDao.findList(condition);
    }
}
```

## 3. 核心功能详解

### 3.1 内置 CRUD 操作

通过继承 [SimpleDao](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L20-L50) 接口，可以获得以下内置方法：

| 方法 | 描述 |
|------|------|
| [insert(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L24-L24) | 插入一条记录 |
| [insertBatch(List<ENTITY> entityList, @BatchSize int batchSize)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L27-L28) | 批量插入 |
| [insertSelective(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L31-L32) | 选择性插入（只插入非空字段） |
| [deleteById(@Param("id") ID id)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L34-L34) | 根据主键删除 |
| [deleteSelective(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L37-L38) | 条件删除 |
| [updateById(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L40-L40) | 根据主键更新 |
| [updateBatchById(List<ENTITY> entityList, @BatchSize int batchSize)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L43-L44) | 批量更新 |
| [updateByIdSelective(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L47-L48) | 选择性更新 |
| [findById(@Param("id") ID id)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L50-L50) | 根据主键查询 |
| [findOne(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L53-L54) | 查询单个对象 |
| [findList(ENTITY entity)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L57-L58) | 查询列表 |
| [findPage(ENTITY entity, Pageable pageable)](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L61-L62) | 分页查询 |

### 3.2 方法名自动生成 SQL

MyBatisGX 支持通过方法名自动生成 SQL 查询语句。例如：

```java
public interface UserDao extends SimpleDao<User, Long> {
    // SELECT * FROM user WHERE name = ?
    List<User> findByName(String name);
    
    // SELECT * FROM user WHERE name LIKE ? AND age > ?
    List<User> findByNameLikeAndAgeGreaterThan(String name, Integer age);
    
    // SELECT * FROM user ORDER BY age DESC
    List<User> findAllOrderByAgeDesc();
    
    // SELECT COUNT(*) FROM user WHERE age BETWEEN ? AND ?
    Long countByAgeBetween(Integer minAge, Integer maxAge);
}
```

#### 支持的操作符

- 等值比较: `Equals`, `Is`, 或省略（如 `findByName` 等同于 `findByNameEquals`）
- 模糊匹配: `Like`, `NotLike`
- 范围比较: `LessThan`, `LessThanEqual`, `GreaterThan`, `GreaterThanEqual`, `Between`
- 包含判断: `In`, `NotIn`
- 空值判断: `IsNull`, `IsNotNull`
- 否定操作: `Not`

#### 聚合函数

- `Count`
- `Sum`
- `Avg`
- `Max`
- `Min`

### 3.3 查询条件分组

使用 [@QueryCondition](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/QueryCondition.java#L13-L21) 注解可以实现复杂的条件组合：

```java
@QueryCondition("NameLikeAnd(AgeOrSex)")
List<User> findByNameLikeAndAgeOrSex(String name, Integer age, Integer sex);
```

这将生成类似如下 SQL：
```sql
SELECT * FROM user WHERE name LIKE ? AND (age = ? OR sex = ?)
```

### 3.4 动态 SQL

通过 [@Dynamic](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Dynamic.java#L12-L20) 注解标识的方法会在运行时根据传入对象的非空字段动态生成 SQL：

```java
@Dynamic
List<User> findByConditions(User user);
```

### 3.5 查询实体

对于复杂查询条件，可以使用 [@QueryEntity](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/QueryEntity.java#L10-L13) 注解定义专门的查询实体：

```java
@QueryEntity
public class UserQuery {
    private String nameLike;
    private Integer ageGt;
    private Integer ageLt;
    // getter/setter 省略
}

public interface UserDao extends SimpleDao<User, Long> {
    List<User> findByQueryEntity(UserQuery query);
}
```

### 3.6 逻辑删除

使用 [@LogicDelete](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/LogicDelete.java#L14-L21) 注解可以实现逻辑删除：

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @LogicDelete
    private Integer deleted;
    
    // getter/setter 省略
}
```

默认显示值为 "1"，隐藏值为 "0"。可以通过 `show()` 和 `hide()` 方法自定义。

### 3.7 乐观锁

使用 [@Version](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Version.java#L11-L16) 注解实现乐观锁：

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @Version
    private Integer version;
    
    // getter/setter 省略
}
```

### 3.8 关联查询

MyBatisGX 支持自动处理对象间的关联关系：

#### 一对一关系

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @OneToOne
    @JoinColumn(name = "profile_id")
    @Fetch(FetchMode.JOIN)
    private UserProfile profile;
    
    // getter/setter 省略
}
```

#### 一对多关系

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @OneToMany
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.BATCH)
    private List<Order> orders;
    
    // getter/setter 省略
}
```

#### 多对一关系

```java
@Entity
@Table(name = "order")
public class Order {
    @Id
    private Long id;
    
    private String orderNo;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // getter/setter 省略
}
```

#### 多对多关系

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<Role> roles;
    
    // getter/setter 省略
}
```

#### 抓取模式

支持三种抓取模式，通过 [@Fetch](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Fetch.java#L12-L25) 注解指定：

1. [SIMPLE](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/FetchMode.java#L14-L16): 简单抓取模式，可能导致 N+1 问题
2. [JOIN](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/FetchMode.java#L21-L22): 联表查询，适用于结果集较小的场景
3. [BATCH](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/FetchMode.java#L27-L28): 批量查询，适用于结果集较大的场景（默认）

### 3.9 复合主键

使用 [@EmbeddedId](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/EmbeddedId.java#L12-L15) 注解支持复合主键：

```java
@Embeddable
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
    
    // getter/setter 省略
}

@Entity
@Table(name = "user_role")
public class UserRole {
    @EmbeddedId
    private UserRoleId id;
    
    private Date createTime;
    
    // getter/setter 省略
}
```

## 4. 高级功能

### 4.1 字段填充

通过实现字段处理器，可以在插入或更新时自动填充特定字段：

```java
public interface MetaObjectHandler {
    void insertFill(Object obj);
    void updateFill(Object obj);
}
```

### 4.2 批量操作

支持批量插入和批量更新操作：

```java
// 批量插入
int count = userDao.insertBatch(userList, 1000);

// 批量更新
int count = userDao.updateBatchById(userList, 1000);
```

### 4.3 分页查询

使用 [Pageable](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/Pageable.java#L8-L36) 和 [Page](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/Page.java#L7-L35) 进行分页查询：

```java
Pageable pageable = Pageable.of(0, 10); // 第一页，每页10条
Page<User> page = userDao.findPage(new User(), pageable);
List<User> users = page.getContent(); // 当前页数据
long total = page.getTotalElements(); // 总记录数
```

### 4.4 多租户

MyBatisGX 通过 JSqlParser 实现多租户支持，在 SQL 执行过程中自动添加租户条件。

## 5. 与其他框架对比

### 5.1 相比 MyBatis Plus

- 更简洁的方法名规则
- 更强大的关联查询能力
- 更灵活的条件分组
- 更完善的复合主键支持

### 5.2 相比 Spring Data JPA

- 不需要编写 JPQL
- 更好的性能优化
- 更符合国内开发习惯
- 更低的学习成本

## 6. 最佳实践

### 6.1 实体设计

1. 实体类应使用 [@Entity](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Entity.java#L6-L8) 注解标识
2. 表名映射使用 [@Table](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Table.java#L15-L52) 注解
3. 主键字段使用 [@Id](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Id.java#L10-L12) 注解
4. 字段映射使用 [@Column](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Column.java#L17-L72) 注解

### 6.2 DAO 设计

1. 优先继承 [SimpleDao](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L20-L50) 获取基础 CRUD 操作
2. 自定义查询方法遵循命名规范
3. 复杂查询使用 [@QueryCondition](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/QueryCondition.java#L13-L21) 注解
4. 动态查询使用 [@Dynamic](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-annotation/src/main/java/com/mybatisgx/annotation/Dynamic.java#L12-L20) 注解

### 6.3 查询优化

1. 合理使用抓取模式
2. 避免 N+1 查询问题
3. 适当使用分页
4. 复杂查询考虑使用原生 SQL

## 7. 常见问题

### 7.1 如何自定义 SQL？

当框架提供的功能无法满足需求时，可以在对应的 XML 文件中编写自定义 SQL，框架会优先使用 XML 中的 SQL。

### 7.2 如何处理大数据量查询？

对于大数据量查询，建议：
1. 使用分页查询
2. 添加合适的索引
3. 考虑读写分离
4. 使用缓存机制

### 7.3 如何升级现有 MyBatis 项目？

MyBatisGX 兼容原生 MyBatis，可以无缝升级：
1. 引入 MyBatisGX 依赖
2. 将原有 Mapper 接口改为继承 [SimpleDao](file:///F:/devops/mybatisgx/mybatisgx/mybatisgx-core/src/main/java/com/mybatisgx/dao/SimpleDao.java#L20-L50)
3. 使用框架提供的功能逐步替换原有代码

## 8. API 参考

详细 API 文档请参考源码及示例。