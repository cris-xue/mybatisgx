---
sidebar_position: 1
---

# 与 MyBatis-Plus 共存

MyBatisGX 可以与 MyBatis-Plus（MP）在同一个项目中**共存并增量升级**：
已有的 MP `BaseMapper` 代码零改动，新增的查询能力（方法名派生 SQL、QueryEntity、mgxql）用 MyBatisGX 实现。

```
┌──────────────────────────────────────────────────────┐
│                SqlSessionFactory（唯一）               │
│    Configuration = MybatisgxPlusConfiguration         │
│     (继承 MP MybatisConfiguration + MyBatisGX 扩展能力) │
│                                                      │
│   ┌──────────────────────────────────────────────┐   │
│   │ 老代码                                       │   │
│   │ @Mapper interface UserMapper                 │   │
│   │     extends BaseMapper<User>   ← MP 处理      │   │
│   └──────────────────────────────────────────────┘   │
│   ┌──────────────────────────────────────────────┐   │
│   │ 新代码                                       │   │
│   │ @Mapper interface UserDao                    │   │
│   │     extends SimpleDao<User, UserQuery, Long> │   │
│   │                        ← MyBatisGX 处理       │   │
│   └──────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────┘
```

## 适用场景

- **老 MP 项目想增量引入 MyBatisGX**，但不想一次性迁移全部 mapper
- 新模块想用 MyBatisGX 的方法名派生 SQL / QueryEntity / mgxql，老模块继续用 MP 的 `BaseMapper` + Wrapper
- 团队希望逐步过渡，最终目标可以是一边使用、一边按节奏迁移

## 实现原理

MyBatisGX 与 MP 共存的根基是**单一 SqlSessionFactory**：

1. `SqlSessionFactory` 由 MP 的自动配置创建。
2. 共存 starter 通过装配让位，将 MP 构建的 `MybatisConfiguration` 替换为
   `MybatisgxPlusConfiguration`（`extends MybatisConfiguration` 并实现 MyBatisGX 的
   `MybatisgxConfigurationAware` 接口），使两者的能力落在同一个 Configuration 上。
3. MyBatisGX 的增强逻辑（值生成、关联查询、字段填充、结果集处理）在运行时按
   `methodInfo` 有无自动分流：只对 MyBatisGX 注册过的方法生效，对 MP 的
   MappedStatement **无副作用**。

> 关键点：MyBatisGX 的所有增强都依赖启动期注册的 `methodInfo`，而 MP 的
> `BaseMapper` 语句 `methodInfo` 为 null，增强逻辑自动跳过——这是共存成立的根基。

## 接口继承边界（重要）

一个 DAO 接口**只能继承一方**：

| 接口 | 继承 | 由谁处理 |
|------|------|---------|
| 老 mapper | `BaseMapper<T>`（MP） | MyBatis-Plus 注入 CRUD 方法 |
| 新 DAO | `SimpleDao` / `CurdDao` / `SelectDao`（MyBatisGX） | MyBatisGX 生成方法名派生 SQL |

**不要**让一个接口同时 `extends BaseMapper` 和 `extends CurdDao`——两者有同名方法
（`insert` / `updateById` / `deleteById`），Java 编译期就会因参数签名冲突（name clash）
拦截双继承；`MybatisgxDoubleInheritanceChecker` 作为运行期二次防线，启动时遍历
已注册 mapper 接口，发现双继承直接报错。

MyBatisGX 侧的新 DAO **不使用** MP 的 `Wrapper` 参数，查询条件用方法名派生 /
QueryEntity / mgxql 表达。

## 快速上手

- [快速开始](./quick-start) — sb3 完整示例
- [增量迁移](./migration) — 老 MP 项目一步步引入
- [版本说明](./version) — sb2 / sb3 版本矩阵
- [分页](./pagination) — 双分页拦截器如何互不干扰
- [故障排查](./troubleshooting) — 常见陷阱与解决
