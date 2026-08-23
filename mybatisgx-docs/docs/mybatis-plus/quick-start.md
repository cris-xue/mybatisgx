---
sidebar_position: 2
---

# 快速开始

> 在已有 MyBatis-Plus 的 Spring Boot 3 项目中引入 MyBatisGX，老代码零改动。

完整的可运行示例见仓库示例工程 `mybatisgx-example-spring-boot3-mp-compat`（sb3）与
`mybatisgx-example-spring-boot2-mp-compat`（sb2）。以下以 sb3 为例。

## 1. 添加依赖

用**共存 starter** 替代 MyBatisGX 标准 starter：

```xml
<!-- Spring Boot 3 + MyBatis-Plus 共存 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-mp-compat-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

> **注意**：共存 starter 与标准 starter（`mybatisgx-spring-boot3-starter`）**互斥，只能二选一**。
> 共存 starter 内部已传递依赖 `mybatisgx-core` / `mybatisgx-spring` / `mybatisgx-mybatis-plus-compat`
> 以及 `mybatis-plus-spring-boot3-starter`，SqlSessionFactory 由 MP 自动配置创建。

## 2. 配置 application.yml

共存模式统一使用 `mybatisgx.*` 前缀：

```yaml
spring:
  main:
    allow-bean-definition-overriding: true   # 共存必需
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_db?characterEncoding=utf8&useUnicode=true&useSSL=true&autoReconnect=true
    username: root
    password: your_password

mybatisgx:
  mapper-locations: classpath*:mapper/*Mapper.xml
  configuration:
    databaseId: MySQL                          # 方言标识（H2 MODE=MySQL 测试时显式指定）
    map-underscore-to-camel-case: true
    cache-enabled: false
  global-config:
    banner: false
```

> 共存 starter 已用 `MybatisgxAutoConfiguration`（继承 MP 自动配置）替换 MP 的原自动配置，
> 因此 **`mybatis-plus.*` 前缀下的配置不再生效**，老项目迁移时需把相关配置迁移到 `mybatisgx.*` 下。

## 3. 定义实体

实体同时标注 MyBatisGX 与 MP 注解，指向同一张表：

```java
@Data
@Entity
@Table(name = "t_user")        // MyBatisGX
@TableName("t_user")           // MyBatis-Plus
public class User {

    @Id
    private Long id;

    private String name;

    private String code;

    private Integer status;

    private Integer age;

    private BigDecimal salary;
}
```

> 示例用 `t_user` 作表名，避免 H2 保留字 `user`。

## 4. 定义老 mapper（保持不变）

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

## 5. 定义新 DAO

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 方法名派生 SQL
    List<User> findByNameLike(@Param("name") String name);

    // mgxql 手写语句
    @Statement("select * from User where age > :age order by id asc")
    List<User> findAgeGreaterThan(@Param("age") Integer age);
}
```

## 6. 启动类配置

单个 `@MybatisgxScan` 同时覆盖 MyBatisGX DAO 与 MP mapper 两个包：

```java
@MybatisgxScan(
        entityBasePackages = "com.example.model.entity",
        daoBasePackages = {"com.example.dao", "com.example.mapper"},
        annotationClass = Mapper.class
)
@SpringBootApplication(scanBasePackages = {"com.example"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- `entityBasePackages`：MyBatisGX 实体扫描信息
- `daoBasePackages`：**同时**覆盖 MyBatisGX DAO 包与 MP mapper 包（compat 版 `@MybatisgxScan`
  自带 `@MapperScan` 元注解，按 `annotationClass` 过滤注册 mapper bean）
- `annotationClass`：compat 版 `@MybatisgxScan` 自带的 `@MapperScan` 按此单一条件过滤，
  工程内所有 DAO / mapper 接口必须**统一使用同一种注解**（`@Mapper` 或 `@Repository`
  **二选一**），并让 `annotationClass` 与之一致：
  - 老工程用 `@Mapper` → 新模块也用 `@Mapper`，`annotationClass = Mapper.class`（示例 sb3）
  - 若想用 `@Repository` → 需把老代码的 `@Mapper` 全部换成 `@Repository`，
    `annotationClass = Repository.class`（示例 sb2）
  不能部分接口 `@Mapper`、部分 `@Repository`，`annotationClass` 只认其中一种，混用会导致
  另一种不被注册。`@MapperScan` 注解已由 `@MybatisgxScan` 承载，工程中**无需**单独声明

## 7. 使用

```java
// MP 老代码，照常工作
User user = userMapper.selectById(1L);
List<User> list = userMapper.selectList(
        new QueryWrapper<User>().eq("status", 1));

// MyBatisGX 新代码
List<User> users = userDao.findByNameLike("张");
User byId = userDao.findById(1L);
Page<User> page = userDao.findPage(query, Pageable.of(1, 10));
```

## Spring Boot 2

sb2 使用 `mybatisgx-spring-boot2-mp-compat-starter`（MP 3.5.6，Spring Boot 2.7.x），
用法一致。注意：

- sb2 的 MP starter 没有 `SqlSessionFactoryBeanCustomizer`（3.5.10 才引入），
  装配改为在 `MybatisPlusPropertiesCustomizer` 里替换 Configuration，效果等价
- 示例启动类用 `annotationClass = Repository.class`，工程内接口统一标注 `@Repository`
  （`@Mapper` / `@Repository` 二选一，与 `annotationClass` 保持一致；`@MapperScan`
  由 `@MybatisgxScan` 承载，无需单独声明）

完整版本组合见 [版本说明](./version)。
