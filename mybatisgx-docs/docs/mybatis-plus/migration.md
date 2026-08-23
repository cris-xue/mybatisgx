---
sidebar_position: 3
---

# 增量迁移指南

> 老 MyBatis-Plus 项目一步步引入 MyBatisGX，不打断现有功能。

## 迁移思路

共存的目的是"**老代码零改动，新代码用 MyBatisGX**"。推荐的迁移路径是自底向上：

```
第 1 步：引入共存 starter，项目跑通
第 2 步：迁移配置前缀（mybatis-plus.* → mybatisgx.*）
第 3 步：新建 MyBatisGX DAO，新功能用它开发
第 4 步：按模块逐步把查询迁到 MyBatisGX
第 5 步（可选）：完全迁移后移除 MP 依赖
```

## 第 1 步：引入共存 starter

```xml
<!-- Spring Boot 3 -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-mp-compat-starter</artifactId>
    <version>0.3.1</version>
</dependency>
```

sb2 用 `mybatisgx-spring-boot2-mp-compat-starter`。引入后先启动一次，确认：
- 只创建一个 `SqlSessionFactory`（由 MP 创建）
- 老 `BaseMapper` 接口照常工作

## 第 2 步：迁移配置前缀

共存 starter 的 `MybatisgxAutoConfiguration` 继承 MP 自动配置，**`mybatis-plus.*` 前缀不再生效**。
将原有 MP 配置迁移到 `mybatisgx.*` 下：

```yaml
# 迁移前（MP 前缀）
# mybatis-plus:
#   configuration:
#     map-underscore-to-camel-case: true
#   global-config:
#     banner: false

# 迁移后（MyBatisGX 前缀）
mybatisgx:
  configuration:
    databaseId: MySQL
    map-underscore-to-camel-case: true
    cache-enabled: false
  global-config:
    banner: false
```

`databaseId` 尤其重要——MyBatisGX 的多数据库方言适配依赖它，共存时拿不到会导致
SQL 方言判断错误。测试环境用 H2（MODE=MySQL）时显式设置 `databaseId: MySQL` 即可。

## 第 3 步：新建 MyBatisGX DAO

新增 `dao` 包（或沿用现有包），定义继承 `SimpleDao` / `CurdDao` / `SelectDao` 的接口。
实体同时标注两套注解（见 [快速开始](./quick-start) 的实体示例）：

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 方法名派生 SQL：findByNameLike → WHERE name LIKE ...
    List<User> findByNameLike(@Param("name") String name);

    // 查询实体：直接使用 SelectDao 内置的 findList(UserQuery)
}
```

将新 `dao` 包加入启动类的 `@MybatisgxScan.daoBasePackages`，与 mapper 包一起扫描。

## 第 4 步：按模块迁移查询

对每个模块，把"查询逻辑"从 Service 里的 Wrapper 迁移到 DAO 方法：

```java
// 迁移前（MP Wrapper）
List<User> list = userMapper.selectList(
        new LambdaQueryWrapper<User>()
                .like(User::getName, "张")
                .gt(User::getAge, 25));

// 迁移后（MyBatisGX 方法名派生）
List<User> list = userDao.findByNameLikeAndAgeGt("张", 25);
```

迁移节奏由你掌控：复杂查询可以先保留 Wrapper，简单的先迁。

## 第 5 步（可选）：完全迁移后移除 MP

当所有查询都迁移到 MyBatisGX 后：

1. 删除 `mybatisgx-spring-boot3-mp-compat-starter` 依赖
2. 恢复使用 `mybatisgx-spring-boot3-starter`（标准 starter）
3. MyBatisGX DAO 接口迁移回标准 starter 的扫描方式（标准 `@MybatisgxScan`）
4. 删除 MP 的 `BaseMapper` 接口与 `@TableName` 注解

core 中新增的 `MybatisgxConfigurationAware` 接口在无 MP 场景下不影响任何行为，可放心回退。

## 迁移注意事项

- **不要**同时引入标准 starter 与 mp-compat-starter（互斥）
- **不要**让一个接口同时继承 `BaseMapper` 与 MyBatisGX DAO 基类
- 逻辑删除、分页等能力两套框架独立工作，迁移时留意行为差异（见 [故障排查](./troubleshooting)）
