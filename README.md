# MyBatisGX

> 一个 **基于 MyBatis 的增强型 ORM 框架**，目标是：
> **减少样板代码、降低 SQL 心智负担、提升开发效率，但不牺牲可控性。**

---

## 一句话介绍

**MyBatisGX = 保留 MyBatis 的可控性 + 提供接近 JPA 的开发效率**

它不是 MyBatis-Plus 的替代品，也不是 JPA 的模仿者，而是为「**讨厌黑盒，但又不想写重复 SQL**」的开发者准备的。

---

## 解决什么问题？

* Service 层不再堆 SQL / Wrapper
* 简单 CRUD 不需要写 Mapper XML
* 方法名即可表达查询语义（但随时可被 XML 接管）
* 自动处理实体关联查询（一对一 / 一对多 / 多对多）

---

## 核心特性速览

* 基于 MyBatis（不脱离原生生态）
* CRUD 接口即用
* 方法名派生 SQL（支持复杂条件组合）
* 查询实体（QueryEntity）解耦查询语义
* 自动关联查询（simple / batch / join）
* 字段值自动生成（审计、加密、脱敏等）
* 逻辑删除 / 可重复逻辑删除
* 乐观锁 / 批量操作 / 分页
* 支持复合主键
* XML 永远拥有最高优先级

---

## 适合谁？

* 熟悉 MyBatis
* 不喜欢 JPA 黑盒
* 觉得 MyBatis-Plus Wrapper 越写越重
* 想提升效率，但仍希望完全掌控 SQL

如果你追求的是“全自动 + 无感知”，那它不适合你。

---

## 快速开始（Spring Boot）

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot-starter</artifactId>
    <version>latest</version>
</dependency>
```

```java
@MybatisgxScan(
    entityBasePackages = "com.xxx.entity",
    daoBasePackages = "com.xxx.dao"
)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

无需 XML，即可完成常规 CRUD 与查询。

---

## 文档与官网

👉 **完整文档 / 设计说明 / 使用指南，请访问官网：**

**[http://www.mybatisgx.com](http://www.mybatisgx.com)**

---

## 项目理念

> 框架应该替开发者处理重复劳动，
> 而不是替开发者做决定。

---

## License

Apache License 2.0
