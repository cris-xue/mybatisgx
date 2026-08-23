---
sidebar_position: 4
---

# 版本说明

> 共存模式各版本线的组合矩阵。以实际 pom 传递依赖为准，升级前请核对。

## 版本矩阵

| 版本线 | 共存 starter | MyBatis-Plus | Spring Boot | Java | mybatis |
|--------|-------------|--------------|-------------|------|---------|
| sb2 | `mybatisgx-spring-boot2-mp-compat-starter` | **3.5.6** | **2.7.x**（示例 2.7.18） | 8 | reactor 基线 **3.5.19** |
| sb3 | `mybatisgx-spring-boot3-mp-compat-starter` | **3.5.17** | **3.1.x**（示例 3.1.5） | 17 | reactor 基线 **3.5.19** |

> 当前版本 `0.4.0`。发布后请以 Maven Central 的实际坐标为准。

### sb3 线要点

- MP 3.5.17 的 `mybatis-plus-spring-boot3-starter` 已内置 `mybatis-plus-jsqlparser`
  （MP 分页 `PaginationInnerInterceptor` 需要的 jsqlparser），**无需额外引入**
- 示例工程用 Spring Boot **3.1.x**；MP 3.5.17 对 Spring Boot 版本的兼容范围以 MP 官方为准
- 装配走 `SqlSessionFactoryBeanCustomizer`（在 factory 层替换 Configuration）

### sb2 线要点

- MP 3.5.6 的 `mybatis-plus-boot-starter` 没有 `SqlSessionFactoryBeanCustomizer`
  （3.5.10 才引入），装配走 `MybatisPlusPropertiesCustomizer`（在配置层替换 Configuration），
  效果等价
- 示例工程用 Spring Boot **2.7.x**，Java 8；测试使用 **JUnit 4**

## mybatis 版本说明

MyBatisGX reactor 的 mybatis 基线为 **3.5.19**（core-patch 按此编译）。共存运行时
mybatis 由各 starter 的传递依赖解析，两条版本线均与 3.5.19 二进制兼容。

> 提示：不要在共存项目里手动把 mybatis 拉到低于 core-patch 编译基线的版本，
> 否则可能触发 `NoSuchMethodError`（MyBatis 曾将 `getMappedColumnNames` 返回类型
> List 改为 Set，跨越该分界线的版本需要与 core-patch 同步重新编译）。

## 两套 starter 互斥

| starter | 用途 |
|---------|------|
| `mybatisgx-spring-boot2/3-starter` | 纯 MyBatisGX（无 MP） |
| `mybatisgx-spring-boot2/3-mp-compat-starter` | MyBatisGX + MP 共存 |

**只能二选一**。两个 jar 中 `com.mybatisgx.boot.MybatisgxScan` 全限定名相同（均含
`@MapperScan` 元注解），但所属 starter 的自动装配不同（标准版装配纯 MyBatisGX，
compat 版装配 MyBatisGX + MP 共存），同时引入时类加载顺序决定行为，不可预测且无报错提示。
