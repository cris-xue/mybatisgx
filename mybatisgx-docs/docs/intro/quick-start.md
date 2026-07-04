---
sidebar_position: 2
---

# 快速开始

> 5 分钟上手 MyBatisGX

## 环境要求

- JDK 8+
- Spring Boot 2.x / 3.x
- Maven

## 添加依赖

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
    <version>0.0.2</version>
</dependency>
```

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
        Pageable pageable = new Pageable(pageNo, pageSize);
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

## 下一步

- 了解 [核心特性](./features)
- 学习 [实体定义](../basic/entity)
- 掌握 [DAO 接口](../basic/dao)
