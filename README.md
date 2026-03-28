# MyBatisGX

> 为长期演进而设计的 MyBatis 增强框架

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 概述

MyBatisGX 是一个基于 MyBatis 的增强框架，致力于解决一个核心问题：**让代码不被数据库腐蚀**。

在传统的 MyBatis Plus/Flex 开发模式中，Service 层充斥着大量的 Wrapper 构建代码和持久层逻辑。随着项目规模增长，这些数据库代码像藤蔓一样侵入业务逻辑，严重影响可读性和可维护性。

MyBatisGX 通过 **DAO 层收敛**、**方法名语义化**、**SQL 预生成**等特性，在保持快速开发的同时，维持清晰的架构边界：**持久层逻辑归于 DAO，业务逻辑留在 Service**。

### 核心价值

```
快速开发 × 架构清晰 × 渐进式控制

从零配置到完全掌控，Service 层始终纯净
```

## 核心特性

### 🎯 DAO 层收敛
持久层逻辑不泄露到 Service 层，业务代码更纯粹、更易理解

```java
// ❌ 传统方式 - Service 层混杂持久层逻辑
public List<User> queryUsers(String name, Integer minAge) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(User::getName, name)
           .ge(User::getAge, minAge)
           .eq(User::getStatus, 1);
    return userMapper.selectList(wrapper);
}

// ✅ MyBatisGX 方式 - Service 层只表达业务意图
public List<User> queryUsers(String name, Integer minAge) {
    return userDao.findByNameLikeAndAgeGeAndStatus(name, minAge, 1);
}
```

### 📝 方法名语义化查询
像 JPA 一样，通过方法名自动生成 SQL，无需编写 XML

```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    // 自动生成 SQL
    List<User> findByNameLike(String name);

    // 支持复杂条件组合
    List<User> findByNameLikeAndAgeGtAndStatusIn(String name, Integer age, List<Integer> statusList);

    // 支持排序和分页
    List<User> findByStatusOrderByCreateTimeDesc(Integer status);
}
```

### 🔧 SQL 预生成 + 低接管成本
启动时生成 SQL，可审查、可覆盖，接管时调用方零改动

```java
// Step 1: 使用方法名查询（默认行为）
List<User> users = userDao.findByOrgIdAndNameLike(orgId, name);

// Step 2: 性能需要优化？直接在 XML 覆盖（Service 代码不变！）
```

```xml
<select id="findByOrgIdAndNameLike" resultMap="BaseResultMap">
    SELECT u.*, o.name as org_name
    FROM user u
    LEFT JOIN org o ON u.org_id = o.id
    WHERE u.org_id = #{orgId}
      AND u.name LIKE CONCAT('%', #{name}, '%')
</select>
```

**关键优势**：
- ✅ Service 层代码 0 改动
- ✅ DAO 接口签名不变
- ✅ 只需在 XML 添加或覆盖
- ✅ XML 优先级最高，永远不会被覆盖

### 🔗 声明式关联查询
类似 JPA，但提供三种抓取模式，性能可控

```java
@Entity
@Table(name = "org")
public class Org {
    @Id
    private Long id;
    private String name;

    // 声明式关联 + 批量抓取避免 N+1 问题
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)  // 三种模式：SIMPLE / BATCH / JOIN
    private List<User> users;
}
```

**三种抓取模式**：
- `SIMPLE`：简单模式（可能产生 N+1）
- `BATCH`：批量模式（使用 IN 查询避免 N+1）
- `JOIN`：连接模式（一次性 JOIN 查询）

### 🎨 动态查询
使用 QueryEntity 实现类型安全的动态查询

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;       // 自动转换为 LIKE 查询
    private List<Long> idIn;       // 自动转换为 IN 查询
    private Integer ageGt;         // 自动转换为 > 查询
    private Date createTimeLt;     // 自动转换为 < 查询
}

// DAO 层
@Dynamic
List<User> findList(UserQuery query);  // query 中 null 字段自动忽略

// Service 层
UserQuery query = new UserQuery();
query.setNameLike("张");
query.setAgeGt(18);
List<User> users = userDao.findList(query);  // 自动生成动态 SQL
```

### 🛡️ 100% 兼容 MyBatis
渐进式升级，保留所有现有 XML，零风险引入

```xml
<!-- 原有的 MyBatis XML 完全保留 -->
<select id="complexQuery" resultMap="BaseResultMap">
    SELECT * FROM user WHERE ...
</select>
```

## 快速开始

### 1. 添加依赖

**Spring Boot 3.x**
```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```

**Spring Boot 2.x**
```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```

### 2. 配置扫描路径

```yaml
mybatisgx:
  entity-package: com.example.entity    # 实体类包路径
  dao-package: com.example.dao          # DAO 接口包路径
```

### 3. 创建实体类

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;
    private Integer age;
    private Integer status;

    @LogicDelete
    private Integer deleted;

    @Version
    private Integer version;
}
```

### 4. 创建 DAO 接口

```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    // 继承 SimpleDao 自动获得基础 CRUD 方法

    // 自定义方法名查询
    List<User> findByNameLike(String name);
    List<User> findByAgeGtAndStatusIn(Integer age, List<Integer> statusList);
}
```

### 5. 在 Service 中使用

```java
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public List<User> queryActiveUsers(String name, Integer minAge) {
        return userDao.findByNameLikeAndAgeGeAndStatus(name, minAge, 1);
    }
}
```

## 框架对比

### 设计对比

| 框架 | 设计 | 适用场景 |
|------|-------|---------|
| MyBatis | SQL 完全掌控，显式映射 | 复杂 SQL、报表、对 SQL 有精确要求的场景 |
| MP/Flex | 快速开发，链式 API | 快速原型、简单 CRUD 为主的项目 |
| JPA/Hibernate | 对象思维，数据库无关性 | 标准化场景、多数据库支持、DDD 建模 |
| **MyBatisGX** | **DAO 层收敛 + SQL 可控** | **需要长期维护、业务复杂、团队规模化的项目** |

### 查询方式对比

| 框架 | 简单查询 | 复杂查询 | 查询位置 | 类型安全 |
|------|---------|---------|---------|---------|
| MyBatis | ❌ 需写 XML | ✅ SQL 完全自由 | DAO (XML) | ⚠️ 部分 |
| MP/Flex | ✅ Wrapper 快速 | ⚠️ 复杂逻辑冗长 | **Service 层** | ✅ Lambda |
| JPA | ✅ 方法名/JPQL | ⚠️ 原生 SQL 映射笨重 | Repository | ✅ Criteria API |
| **MyBatisGX** | ✅ 方法名自动生成 | ✅ XML 覆盖 | **DAO 层** | ✅ 方法签名 + QueryEntity |

### SQL 控制力与接管成本

| 框架 | 默认 SQL 可见性 | 接管方式 | 接管成本 | 调用方是否需改动 |
|------|---------------|---------|---------|---------------|
| MyBatis | ✅ 完全可见（XML） | - | - | - |
| MP/Flex | ❌ 运行时生成 | 自定义 Mapper + XML | ⚠️ 中等（需迁移 Wrapper 逻辑） | ✅ 需要改 |
| JPA | ❌ 黑盒（日志可看） | @Query 或 Native SQL | ⚠️ 高（结果映射复杂） | ✅ 需要改 |
| **MyBatisGX** | ✅ 预生成可审查 | XML 同名覆盖 | ✅ **低（直接写 XML）** | ❌ **不需要改** |

**关键差异**：
- MP/Flex：从 Wrapper 到 XML，需要"翻译"逻辑，Service 层代码要改
- JPA：从 JPQL 到 Native SQL，结果映射变复杂
- **MyBatisGX：DAO 接口不变，XML 直接覆盖，Service 零改动**

### 分层架构对比

| 框架 | 持久层逻辑位置 | Service 层代码 | 架构约束力 |
|------|--------------|--------------|-----------|
| MyBatis | DAO (XML) | ✅ 纯业务 | ⚠️ 需要手动约束 |
| MP/Flex | **Service (Wrapper)** | ❌ **混杂数据库逻辑** | ❌ 无约束 |
| JPA | Repository | ✅ 纯业务（理想情况） | ⚠️ Specification 可能泄露 |
| **MyBatisGX** | **DAO (方法名 + XML)** | ✅ **纯业务** | ✅ **框架强制** |

### 代码示例对比

```java
// ═══════════════════════════════════════════════════════
// MP/Flex - Service 层
// ═══════════════════════════════════════════════════════
public List<User> queryUsers(String name, Integer minAge) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(User::getName, name)
           .ge(User::getAge, minAge)
           .eq(User::getStatus, 1);
    return userMapper.selectList(wrapper);
}
// 问题：数据库字段、查询逻辑在 Service 层

// ═══════════════════════════════════════════════════════
// MyBatisGX - Service 层
// ═══════════════════════════════════════════════════════
public List<User> queryUsers(String name, Integer minAge) {
    return userDao.findByNameLikeAndAgeGeAndStatus(name, minAge, 1);
}
// 持久层逻辑收敛到 DAO，Service 只表达业务意图
```

### 关联查询对比

| 框架 | 声明方式 | N+1 问题 | 懒加载 | JOIN 控制 |
|------|---------|---------|-------|----------|
| MyBatis | 手写 ResultMap + JOIN | ⚠️ 需手动优化 | ⚠️ 需配置 | ✅ 完全控制 |
| MP/Flex | ❌ 不支持（需手动） | - | - | 手动 JOIN |
| JPA | ✅ @OneToMany 等 | ⚠️ 常见 | ✅ 支持 | ⚠️ 有限（Fetch Join） |
| **MyBatisGX** | ✅ @OneToMany + @Fetch | ✅ 可选 BATCH/JOIN 避免 | ✅ 支持 | ✅ 三种模式可选 |

**MyBatisGX 优势**：
- 声明式关联（学习 JPA）
- 三种抓取模式（SIMPLE/BATCH/JOIN）精确控制性能
- 不引入 JPA 的持久化上下文和隐式行为

## 技术优势

### 📊 信噪比优化
在 AI 时代，代码的可读性比代码量更重要。MyBatisGX 通过 DAO 层收敛和方法名语义化，显著提高代码信噪比。

```
传统项目中的"噪音"：
  Service 层：
  ├─ Wrapper 构建代码 ◄── 噪音！
  ├─ 字段名硬编码      ◄── 噪音！
  ├─ Criteria 拼装     ◄── 噪音！
  └─ 真正的业务逻辑    ◄── 信号

MyBatisGX 项目中：
  Service 层：
  ├─ 方法名语义化      ◄── 信号
  ├─ DAO 层收敛       ◄── 边界清晰
  └─ Service 纯业务   ◄── 100% 信号
```

### 🛡️ 架构护栏
框架强制执行分层原则，防止持久层逻辑泄露到 Service 层，保持架构清晰。

> 在 AI 时代，框架不再是效率工具，而是架构护栏

### 🔍 SQL 可观测性
- ✅ 启动时生成 SQL，可通过日志查看
- ✅ 可导出所有方法对应的 SQL 到文件
- ✅ 支持 MyBatis 原生的 SQL 日志
- ✅ 预生成机制：问题在启动时暴露，不在运行时

### 🎯 渐进式控制
提供从零配置到完全掌控的完整路径：

```
默认行为 ──────────┐
(预生成 SQL)      │
                  │
不满意？          ↓
覆盖它！    mapper.xml 接管
                  │
还需要调优？      ↓
直接写 SQL   完全掌控

渐进式的控制力：
• 90% 场景：用框架生成
• 9% 场景：mapper.xml 覆盖
• 1% 场景：手写复杂 SQL
```

## 适合什么样的开发者？

MyBatisGX 不是为所有人设计的。如果你：

- ✅ 经历过项目从清晰到屎山的演变
- ✅ 对代码有一定的"洁癖"
- ✅ 认真思考过分层架构的价值
- ✅ 宁愿多学一个框架，也不愿代码被污染

那 MyBatisGX 很适合你。

## 适用场景

### ✅ 推荐使用 MyBatisGX

- ✅ 关心代码可维护性和架构清晰度
- ✅ 不想 Service 层变成 Wrapper 地狱
- ✅ 需要保留 SQL 完全控制权
- ✅ 从 MyBatis 项目升级（100% 兼容）

### 🔄 其他框架适用场景

| 场景               | 推荐框架 | 原因                      | 代价 |
|------------------|---------|-------------------------|------|
| 小项目、demo、项目能运行就行 | MP/Flex | 入门快、代码少、简单场景够用 | Service 层会被持久层逻辑污染 |
| 多数据库兼容、标准化       | JPA | 数据库无关性最好                | 黑盒运行、接管成本高 |
| 极致 SQL 优化、复杂报表   | MyBatis | SQL 控制力最强，无抽象损耗         | 简单 CRUD 也要写 XML |
| **代码不想被数据库腐蚀**   | **MyBatisGX** | **架构清晰、接管成本低**          | **需要学习新框架** |

**如果说**：
- 你的项目小、能运行就好，MP/Flex 更快
- 你需要跨 10 种数据库，JPA 更合适
- 你是 SQL 大师且喜欢手写一切，MyBatis 原生更纯粹

**但如果你**：
- 关心代码可维护性
- 不想 Service 层变成 Wrapper 地狱
- 需要保留 SQL 完全控制权

那 MyBatisGX 可能是最优解。

## 稳定性保证

### 技术可靠性
- ✅ 基于 MyBatis 核心，而非重新实现 ORM
- ✅ SQL 预生成（启动时），无运行时动态拼接风险
- ✅ 所有生成的 SQL 可导出审查
- ✅ 兼容 MyBatis 所有特性（拦截器、插件、类型处理器）

### 零风险引入
- ✅ 100% 兼容现有 MyBatis 项目
- ✅ XML 优先级最高，自动生成永远不会覆盖手写 SQL
- ✅ 可以只在新功能中使用，老代码不动
- ✅ 出问题随时可以退回 XML 手写

### 生产验证
- ✅ 场景单元测试覆盖率接近90%
- ✅ 支持 MySQL、Oracle、PostgreSQL 主流数据库
- ✅ 提供完整示例项目

## 进阶特性

### 逻辑删除

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    @LogicDelete
    private Integer deleted;  // 0: 正常，1: 已删除
}

// 调用 delete 方法时自动转换为 UPDATE
userDao.delete(userId);  // UPDATE user SET deleted = 1 WHERE id = ? and deleted = 0
```

### 乐观锁

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    @Version
    private Integer version;
}

// 更新时自动检查版本号
userDao.update(user);  // UPDATE user SET ..., version = version + 1 WHERE id = ? AND version = ?
```

### 字段自动填充

```java
public class CreateTimeProcessor implements ValueProcessor {
    @Override
    public Date process() {
        return new Date();
    }
}

@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    @GeneratedValue(CreateTimeProcessor.class)
    private Date createTime;

    @GeneratedValue(UpdateTimeProcessor.class)
    private Date updateTime;
}
```

### 分页查询

- 基于 PageHelper

```java
PageHelper.startPage(1, 10);
List<User> users = userDao.findByStatus(1);
PageInfo<User> pageInfo = new PageInfo<>(users);
```

- 基于 MyBatisGX

```java
// 构建分页参数
Pageable pageable = new Pageable(1, 10);  // 第1页，每页10条

// 构建查询条件
UserQuery query = new UserQuery();
query.setNameLike("张");

// 执行分页查询
Page<User> page = userDao.findPage(query, pageable);

// 获取结果
long total = page.getTotal();       // 总记录数
List<User> list = page.getList();   // 当前页数据
```

## 核心定位总结

MyBatisGX 解决的不是"怎么快速开发"，而是：

> **怎么在保持快速开发的同时，让项目具备长期可演进性**

在四种主流框架中的定位：

```
MyBatis:      手动控制一切 → 灵活但繁琐
MP/Flex:      快速但破坏分层 → Service 层混杂
JPA:          高度抽象但黑盒 → 接管成本高

MyBatisGX:    自动化 + 分层清晰 + 可控
              ├─ 像 JPA：方法名查询、声明式关联
              ├─ 像 MyBatis：SQL 可控、显式执行
              └─ 比 MP 好：持久层逻辑不泄露
```

### 独特价值

MyBatisGX 是唯一一个同时满足：
- ✅ 方法名查询（不写 XML）
- ✅ DAO 层收敛（不在 Service 写 Wrapper）
- ✅ 低接管成本（XML 覆盖，调用方不改）
- ✅ SQL 完全透明（预生成，可审查）

## 文档与支持

- 📖 [完整文档](http://www.mybatisgx.com)
- 🚀 [快速开始示例](../mybatisgx-example)
- 💬 [问题反馈](https://gitee.com/freakchicken/mybatisgx/issues)
- 📚 [在线文档](../docs/官网文档/mybatisgx_说明.md)

## 项目理念

> 框架应该替开发者处理重复劳动，而不是替开发者做决定。
>
> 在 AI 时代，好的架构不是少写代码，而是少读代码。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

Apache License 2.0

---

**MyBatisGX：为长期演进而设计的持久层框架**

快速开发 × 架构清晰 × 渐进式控制 | 从零配置到完全掌控，Service 层始终纯净
