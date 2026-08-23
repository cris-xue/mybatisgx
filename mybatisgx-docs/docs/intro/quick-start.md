---
sidebar_position: 2
---

# 快速开始

> 5 分钟上手 MyBatisGX

## 环境要求

- JDK 8+
- Spring Boot 2.x / 3.x / 4.x
- Maven

## 添加依赖

按 Spring Boot 版本选择对应的 starter：

```xml
<!-- Spring Boot 2 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

```xml
<!-- Spring Boot 3 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

```xml
<!-- Spring Boot 4 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot4-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

> 最新版本请以 Maven Central 为准。starter 版本需与 Spring Boot 主版本号匹配。

## 配置文件

```yaml
mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
```

## 定义实体

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    // getter/setter 省略
}
```

## 定义查询实体

```java
@QueryEntity(User.class)
public class UserQuery extends User {

    private String nameLike;  // 模糊查询

    private Integer ageGt;    // 大于查询
}
```

## 定义 DAO 接口

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

> `SimpleDao<ENTITY, QUERY_ENTITY, ID>` 继承 `CurdDao`（增删改）与 `SelectDao`（查询），内置常用方法。

## 启动类配置

```java
@MybatisgxScan(
    entityBasePackages = "com.example.entity",
    daoBasePackages = "com.example.dao"
)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 开始使用

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    // 新增
    public void addUser() {
        User user = new User();
        user.setName("张三");
        user.setAge(25);
        userDao.insert(user);
    }

    // 根据ID查询
    public User getById(Long id) {
        return userDao.findById(id);
    }

    // 条件查询
    public List<User> findByName(String name) {
        UserQuery query = new UserQuery();
        query.setNameLike(name);
        return userDao.findList(query);
    }

    // 分页查询
    public Page<User> findPage(int pageNo, int pageSize) {
        UserQuery query = new UserQuery();
        Pageable pageable = Pageable.of(pageNo, pageSize);
        return userDao.findPage(query, pageable);
    }

    // 更新
    public void updateUser(User user) {
        userDao.updateById(user);
    }

    // 删除
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}
```

## 已有 MyBatis-Plus 项目？

如果项目已经在使用 MyBatis-Plus，可以通过**共存 starter** 增量引入 MyBatisGX：
老 `BaseMapper` 代码零改动，新查询能力用 MyBatisGX 实现。

```xml
<!-- Spring Boot 3 + MyBatis-Plus 共存 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-mp-compat-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

使用 compat 版的 `@MybatisgxScan`（自带 `@MapperScan` 元注解，单个注解即可同时覆盖
MyBatisGX DAO 与 MP mapper 两个包），老 mapper 与新 DAO 共存于同一 SqlSessionFactory。
详见 [与 MyBatis-Plus 兼容](../mybatis-plus/quick-start)。

> 注意：标准 starter 与 mp-compat-starter 互斥，只能二选一。
> 版本组合：sb2 配 MP 3.5.6 / sb3 配 MP 3.5.17，mybatis 基线 3.5.19（详见[版本说明](../mybatis-plus/version)）。

## 下一步

- 了解 [核心特性](./features)
- 学习 [实体定义](../basic/entity)
- 掌握 [DAO 接口](../basic/dao)
- 探索 [查询语言总览](../query-language/overview)
