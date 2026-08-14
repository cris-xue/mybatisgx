# MyBatisGX

> 为长期演进而设计的 MyBatis 增强框架 —— 让代码不被数据库腐蚀

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub stars](https://img.shields.io/github/stars/cris-xue/mybatisgx?style=social)](https://github.com/cris-xue/mybatisgx)
[![Spring Boot 2](https://img.shields.io/badge/Spring%20Boot-2.x-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.x-green.svg)](https://spring.io/projects/spring-boot)

## 社区交流

欢迎交流 MyBatisGX 使用问题、ORM 设计与 SQL 架构实践。

微信：xcc137396549
备注：进 MyBatisGX 群

## 为什么诞生：防腐

MyBatisGX 不是为了解决"怎么操作数据库"——ORM 早就不是难题。它解决的是一个**工程化问题**：

> **代码如何不被数据库腐蚀。**

第一周，你接到需求：用户列表，按名称和状态筛选。10 分钟，测试通过，提交：

```java
public List<User> queryUsers(String name, Integer status) {
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .like(User::getName, name)
            .eq(User::getStatus, status));
}
```

第三个月，需求迭代了 5 次：加时间范围、加角色、加部门、加标签、加手机号模糊搜索。你的 Service 方法变成了 200 行 Wrapper 拼装。**数据库字段名、查询逻辑像藤蔓一样爬满了业务代码**——代码被腐蚀了。

MyBatisGX 让这一切回到它该在的位置：

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

**持久层逻辑归于 DAO，业务逻辑留在 Service**——这就是防腐。而 0.3.0 的 MGXSQL 则更进一步，把这种保护延伸到 SQL 文本本身（见下文）。

## 快速开始

### 1. 添加依赖

**Spring Boot 3.x**

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

**Spring Boot 2.x**

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

**Spring Boot 4.x**

```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot4-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

### 2. 配置扫描路径

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

## 核心能力

### 🎯 DAO 层收敛

持久层逻辑不泄露到 Service 层。框架强制分层：**Service 只表达业务，查询逻辑全部收敛到 DAO**——这是防腐的架构保障，不需要靠团队自觉。

### 📝 三种查询方式总览

MyBatisGX 提供三种由易到难的查询方式，它们最终汇入**同一条统一链路**（见下文架构）：

| 方式 | 适用场景 | 示例 |
|------|---------|------|
| **方法名查询** | 常规查询，零学习成本 | `findByNameLikeAndAgeGeAndStatus(...)` |
| **QueryEntity 动态查询** | 条件可变的查询 | `userDao.findList(query)`，null 字段自动忽略 |
| **`@Statement` 手写 MGXQL** | 复杂查询，方法名表达不了 | `@Statement("select u.* from User u left join UserDetail ud on u = ud ...")` |

**方法名查询**——像 JPA 一样，通过方法名自动生成 SQL：

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

**QueryEntity 动态查询**——类型安全的动态查询对象：

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

**`@Statement` 手写 MGXQL**——复杂查询直接用对象查询语言表达（对应实体对象，而非表名）：

```java
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    @Statement("select * from User where id = :id")
    User findById(@Param("id") Long id);

    @Statement("select u.* from User u left join UserDetail ud on u = ud where u.id = :id")
    User findUserWithDetail(@Param("id") Long id);

    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount")
    List<Map<String, Object>> findGroupByCodeHavingCount(@Param("minCount") long minCount);
}
```

### 🔧 渐进式控制

从零配置到完全掌控，Service 层始终纯净：

```
默认行为 ──────────┐
(预生成 SQL)      │
                  │
不满意？          ↓
覆盖它！    mapper.xml / MGXSQL 接管
                  │
还需要调优？      ↓
直接写 SQL   完全掌控

渐进式的控制力：
• 90% 场景：用框架生成
• 9% 场景：mapper.xml / MGXSQL 覆盖
• 1% 场景：手写复杂 SQL
```

XML 覆盖时**调用方零改动**——DAO 接口签名不变，Service 代码不变：

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

**XML 优先级最高，永远不会被覆盖。**

### 🔗 声明式关联查询

类似 JPA，但提供四种抓取模式，性能可控：

```java
@Entity
@Table(name = "org")
public class Org {
    @Id
    private Long id;
    private String name;

    // 声明式关联 + 批量抓取避免 N+1 问题
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)  // 四种模式：SIMPLE / BATCH / JOIN / NONE
    private List<User> users;
}
```

**四种抓取模式**：
- `SIMPLE`：简单抓取模式，会造成 N+1 问题
- `BATCH`：批量模式，对单张表进行批量查询再组装数据，用于解决 N+1 问题。会发出 1+m 张表的 SQL（默认模式）
- `JOIN`：连接模式，采用 1+1 模式，第一级单表查询获取 key 后，第二条 SQL 无限 JOIN 直到不存在关联实体。避免分页时的数据爆炸问题
- `NONE`：不进行关联查询抓取

### 🛠️ 工程能力

- **逻辑删除**：`@LogicDelete` 注解字段，`delete` 自动转 `UPDATE`
- **乐观锁**：`@Version` 注解字段，更新时自动校验版本号
- **字段自动填充**：`@GeneratedValue(Processor.class)` 自定义值生成（创建时间、更新时间、审计字段等）
- **分页**：`findPage(query, Pageable.of(1, 10))` 返回 `Page<T>`，也可兼容 PageHelper

```java
Pageable pageable = Pageable.of(1, 10);   // 第1页，每页10条
UserQuery query = new UserQuery();
query.setNameLike("张");
Page<User> page = userDao.findPage(query, pageable);

long total = page.getTotal();       // 总记录数
List<User> list = page.getList();   // 当前页数据
```

## MGXSQL：SQL 层防腐

如果说方法名查询保护的是 **Service 层代码**，那么 MGXSQL 保护的是 **SQL 文本本身**。

写动态 SQL 的传统方式，是用 MyBatis XML 标签把 SQL 撕成碎片：

```xml
<select id="findByConditions" resultMap="BaseResultMap">
    SELECT * FROM user
    <where>
        <if test="name != null and name != ''">
            AND name LIKE CONCAT('%', #{name}, '%')
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
    </where>
</select>
```

SQL 的完整结构一眼看不到头——**SQL 被标签腐蚀了**。用 MGXSQL 写同样的逻辑：

```java
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from user\n" +
        "where 1 = 1\n" +
        "#[and name like %:name%]\n" +
        "#[and status = :status]")
List<User> findByConditions(@Param("name") String name, @Param("status") Integer status);
```

**SQL 保持 SQL 本身的结构**，动态条件直接写在 SQL 中，一眼可读。

### 语法速览

```sql
select * from user
where 1 = 1
#[and name = :name]                              -- 自动判断：name 非空才拼接
#if(:status != null)[and status = :status]       -- 自定义 guard
#choose[                                         -- 分支选择
    #when(:type == 'vip')[and level = :level]
    #otherwise[and status = :status]
]
and id in #for(item:idList)=>$item               -- 集合参数 → foreach
```

| 语法 | 含义 |
|------|------|
| `#[body]` | 按 body 内参数自动生成 `isNotEmpty` 判断 |
| `#if(expr)[body]` | 自定义 guard 条件控制 |
| `#choose / #when / #otherwise` | 多分支互斥选择 |
| `#for(item:list)=>$item.x` | 集合迭代（生成 `<foreach>`） |
| `in :idList` | IN 子句简写 |
| `%:name%` / `:name%` / `%:name` | LIKE 模式（生成 `<bind>`） |
| `#include[sqlId]` | SQL 片段引用 |
| `#bind[name = expr]` | 变量绑定（OGNL 表达式） |

> 旧语法 `?condition` 已完全废弃，请迁移为 `#[condition]` / `#if(expr)[...]`。

### 不换框架也能用

MGXSQL 基于 MyBatis `LanguageDriver` 实现，**不绑定 MyBatisGX**。只要你的项目构建在 MyBatis 之上（MyBatis、MyBatis-Plus、MyBatis-Flex……），都可以单独使用：

```java
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from user where #[name = :name]")
List<User> findByConditions(@Param("name") String name);
```

或全局配置，省去每个方法重复标注：

```yaml
mybatis:
  configuration:
    default-scripting-language: com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver
```

### 与旧 XML 混合使用，渐进迁移

MGXSQL 支持与原生 MyBatis 动态标签混写，老系统可以逐步引入，不需要一次性改造所有 Mapper：

```sql
select * from user
where 1 = 1
<if test="name != null">
    and name = #{name}
</if>
#[and status = :status]
```

## 统一查询链路

0.3.0 起，所有查询方式统一进入同一条生成链路：

```
方法名查询 ────────────┐
QueryEntity 字段       ├──▶ MGXQL（统一查询模型）──▶ MGXSQL（动态 SQL）──▶ MyBatis SQL
@Statement 手写 MGXQL ─┘
```

- **MGXQL**：对象查询语言，负责"查询什么、怎么筛选"的表达（方法名只是它的语法糖入口）
- **MGXSQL**：动态 SQL 模板语法，负责"动态条件怎么拼"
- 最终由 MyBatis 执行，**100% 兼容 MyBatis 生态**（拦截器、插件、类型处理器照常工作）

## 数据库支持

MySQL、MariaDB、OceanBase MySQL、SinoDB、Oracle、Dameng、UXDB、OceanBase、PostgreSQL、GaussDB、Vastbase、Kingbase、GBase（13 种）。

## 与其他框架的对比

### 查询方式 × 架构收敛

| 维度 | MyBatis | MP/Flex | JPA | **MyBatisGX** |
|------|---------|---------|-----|---------------|
| 简单查询 | 手写 XML | Wrapper 链式 | 方法名 | 方法名自动生成 |
| 复杂查询 | XML 自由 | Wrapper 冗长 | JPQL / Criteria | `@Statement`(MGXQL) / XML 覆盖 |
| 查询逻辑位置 | DAO (XML) | **Service (Wrapper)** | Repository | **DAO** |
| Service 纯净度 | ✅ | ❌ 混杂 | ⚠️ 易泄露 | ✅ 框架强制 |
| 复杂查询接管成本 | - | 需翻译 Wrapper | 映射复杂 | 直接覆盖，调用方零改动 |
| SQL 可见性 | ✅ XML | ❌ 运行时生成 | ❌ 黑盒 | ✅ 预生成可审查 |
| 类型安全 | ⚠️ | ✅ | ⚠️ | ✅ |

一句话：**MP/Flex 把查询写进 Service，代码被腐蚀；MyBatisGX 把查询收敛到 DAO，Service 只表达业务。**

### 性能实测

针对"自动生成 SQL 会不会拖慢性能"的疑虑，我们做了真实基准测试（JVM 预热 15 轮，四框架执行相同业务逻辑）：

| 场景（热身后均值） | MyBatis | **MyBatisGX** | MyBatis-Flex | MyBatis-Plus |
|------|---------|----------|---------|---------|
| 单条插入 (μs) | 1,830 | **1,922** | 2,587 | 2,234 |
| 批量插入 100 条 (ms) | 11 | **12** | 21 | 20 |
| 简单条件查询 (μs) | 1,198 | **1,451** | 1,499 | 2,059 |
| 复杂查询 Like+In+Gt (μs) | 1,536 | **1,512** | 1,116 | 1,687 |
| 动态条件查询 (μs) | 1,897 | **1,476** | 1,386 | 1,584 |

**结论**：
- 热身后四个框架处于**同一数量级**（微秒~毫秒级），自动生成 SQL 的性能代价几乎可以忽略
- MyBatisGX 与手写 MyBatis 的差距仅在批量场景约 5%~12%；简单查询场景差距约 21%（约 253μs，可忽略）
- 复杂查询、动态条件查询（最接近真实业务）场景，MyBatisGX 反而快于 MyBatis

完整测试数据与说明见仓库 `docs/` 目录：[自动生成 SQL 会拖慢性能吗？](https://github.com/cris-xue/mybatisgx)

## 适合什么样的开发者？

MyBatisGX 不是为所有人设计的。如果你：

- ✅ 经历过项目从清晰到屎山的演变
- ✅ 对代码有一定的"洁癖"
- ✅ 认真思考过分层架构的价值
- ✅ 宁愿多学一个框架，也不愿代码被污染

那 MyBatisGX 很适合你。

## 文档与支持

- 📚 [在线文档](http://www.mybatisgx.com)
- 🚀 [快速开始示例](https://github.com/cris-xue/mybatisgx-example)
- 💬 [问题反馈](https://github.com/cris-xue/mybatisgx/issues)

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
