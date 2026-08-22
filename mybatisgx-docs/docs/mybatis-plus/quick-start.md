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
    <version>0.3.1</version>
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
- `annotationClass`：`@MapperScan` 按此过滤接口。两个示例分别用了 `Mapper.class`（sb3，
  接口标 `@Mapper`）与 `Repository.class`（sb2，接口标 `@Repository`）；`annotationClass`
  需与接口上实际标注的注解匹配。建议接口**同时标注 `@Mapper` 与 `@Repository`**（双注解可
  覆盖 MyBatisGX 两处收集路径，见 [故障排查](./troubleshooting)）

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

sb2 使用 `mybatisgx-spring-boot2-mp-compat-starter`（MP 3.5.3，Spring Boot 2.7.x），
用法一致。注意：

- sb2 的 MP starter 没有 `SqlSessionFactoryBeanCustomizer`（3.5.10 才引入），
  装配改为在 `MybatisPlusPropertiesCustomizer` 里替换 Configuration，效果等价
- 示例启动类用 `annotationClass = Repository.class`，接口标注 `@Repository`
  （`@MapperScan` 按 `annotationClass` 过滤，`@Repository` 即可被注册为 mapper bean）。
  建议**同时标注 `@Mapper` 与 `@Repository`**：MyBatisGX core 的 `MybatisgxContextLoader`
  自扫路径按 `@Mapper` 过滤，双注解可保证两条收集路径都不漏（见 [故障排查](./troubleshooting)）

完整版本组合见 [版本说明](./version)。
