---
sidebar_position: 6
---

# 故障排查

> 共存模式下常见陷阱与解决方案。

## 1. 逻辑删除语义不一致

MyBatisGX 与 MP 的逻辑删除是**两套独立机制**，实现位置完全不同：

| 框架 | 机制 | 读取的注解 |
|------|------|-----------|
| MyBatisGX | SQL **生成期**硬编码（where 自动追加条件、delete 改写为 UPDATE） | `@LogicDelete` |
| MyBatis-Plus | **拦截器期**动态拼装 | `@TableLogic` |

同一实体若只在一边标注，另一边不会遵循：

```java
// 实体只标了 MyBatisGX 的 @LogicDelete，没标 MP 的 @TableLogic
@TableName("t_user")
public class User {
    @LogicDelete(show = "0", hide = "1")   // MyBatisGX 逻辑删除
    private Integer deleted;
    // 未标 @TableLogic → MP 侧走真删
}

userDao.deleteById(1L);    // → UPDATE t_user SET deleted='0' WHERE id=1  ✅ 软删
userMapper.deleteById(1L); // → DELETE FROM t_user WHERE id=1           ❌ 物理删除
```

**建议**：同一实体要么两套注解都标注（语义一致），要么只在其中一边操作该实体。
混用场景需自行保证数据一致性。

## 2. `mybatis-plus.*` 配置不再生效

共存 starter 用 `MybatisgxAutoConfiguration`（继承 MP 自动配置）替换了 MP 的原自动配置，
**`mybatis-plus.*` 前缀下的配置（含 `global-config`、`db-config`）不再生效**。

```yaml
# 不生效
# mybatis-plus:
#   configuration:
#     map-underscore-to-camel-case: true

# 生效
mybatisgx:
  configuration:
    map-underscore-to-camel-case: true
```

`databaseId` 尤其要命——MyBatisGX 的方言适配依赖它，配置缺失会导致方言判断错误。
用 H2（MODE=MySQL）测试时显式设置 `databaseId: MySQL`。

## 3. 标准 starter 与 compat starter 互斥

`com.mybatisgx.boot.MybatisgxScan` 同时存在于标准 starter 与 mp-compat-starter 两个 jar
（全限定名相同、行为不同）。**同时引入两个 starter 时类加载顺序决定行为，不可预测且无报错**。

```xml
<!-- 错误：同时引入 -->
<dependency>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
</dependency>
<dependency>
    <artifactId>mybatisgx-spring-boot3-mp-compat-starter</artifactId>
</dependency>
```

**解决**：只保留 compat starter（共存场景）或只保留标准 starter（纯 MyBatisGX 场景）。

## 4. 误引入 `mybatis-spring-boot-starter` 导致冲突

共存模式下 SqlSessionFactory 由 MP 创建。若间接依赖带入了 `mybatis-spring-boot-starter`，
其 `MybatisAutoConfiguration` 会与 MP 的 `MybatisPlusAutoConfiguration` 同时注册
`sqlSessionFactory` bean，导致 `BeanDefinitionOverrideException` 或 bean 名冲突。

**解决**：排查依赖树（`mvn dependency:tree`），排除 `mybatis-spring-boot-starter`；
共存 starter 已内置 `MybatisgxExcludeAutoConfigFilter` 排除原生 `MybatisAutoConfiguration`。

## 5. 双继承检测

一个接口同时继承 `BaseMapper` 与 MyBatisGX DAO 基类：
- **编译期**：`BaseMapper` 与 `CurdDao` 的 `deleteById` 等方法参数签名冲突，Java 直接报
  name clash，无法编译
- **运行期**：`MybatisgxDoubleInheritanceChecker` 作为二次防线，启动时遍历 mapper 接口，
  发现双继承直接报错

正常接口不会被误报（仅继承 `BaseMapper` 或仅继承 `Dao` 均通过，集成测试已覆盖）。

## 6. 升级 MP / mybatis 版本后的二进制问题

`MybatisgxPlusConfiguration` 通过 `copyNonFinalFields` 反射拷贝 MP 的 `MybatisConfiguration`
字段（sb3 线）。若未来 MP 新增 final 字段，新 Configuration 里该字段为默认值，可能运行期 NPE。

**建议**：升级 MP 大版本时回归验证共存场景；不要手动把 mybatis 拉到低于 core-patch
编译基线（3.5.19）的版本，否则可能触发 `NoSuchMethodError`。

## 7. 接口扫描注解不一致（`@Repository` vs `@Mapper`）

MyBatisGX 收集 DAO 的两处逻辑注解过滤条件不同（`SqlSessionFactoryBeanPostProcessor`
认 `@Repository`，`MybatisgxContextLoader` 认 `@Mapper`）。为保证万无一失，建议
**在 DAO 接口上同时标注两个注解**，或保证 `@MybatisgxScan.annotationClass` 与接口
实际标注一致（示例 sb3 用 `@Mapper` + `annotationClass=Mapper.class`，sb2 用
`@Repository` + `annotationClass=Repository.class`）。
