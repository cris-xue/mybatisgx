---
sidebar_position: 1
---

# 常见问题

> 技术选型与使用常见问题解答

## 技术选型

### MyBatisGX 与 MyBatis-Plus 如何选择？

| 对比项 | MyBatisGX | MyBatis-Plus |
|--------|-----------|--------------|
| **SQL 可控性** | 完全可控，XML 优先 | 部分可控 |
| **查询方式** | 方法名派生 + QueryEntity | Wrapper 链式调用 |
| **Service 层** | 查询逻辑在 DAO 层 | Wrapper 可能在 Service 层 |
| **关联查询** | 声明式，自动抓取 | 需要手动配置 |
| **学习曲线** | 较低，类似 JPA 注解 | 中等，Wrapper 语法 |
| **MyBatis 兼容** | 完全兼容 | 完全兼容 |

**选择建议**：

- 选择 **MyBatisGX**：需要 SQL 完全可控、希望查询逻辑收敛在 DAO 层、需要声明式关联查询
- 选择 **MyBatis-Plus**：已熟悉 Wrapper 语法、项目已有 MyBatis-Plus 生态

### MyBatisGX 与 JPA 如何选择？

| 对比项 | MyBatisGX | JPA |
|--------|-----------|-----|
| **运行时机制** | 无隐式行为 | 有持久化上下文 |
| **SQL 控制** | 完全可控 | 较难控制 |
| **性能** | 无额外开销 | 有脏检查开销 |
| **复杂查询** | 支持良好 | 需要复杂 Specification |
| **对象建模** | 复用 JPA 注解 | 完整 JPA 生态 |

**选择建议**：

- 选择 **MyBatisGX**：需要 SQL 可控、厌恶 JPA 黑盒运行时、需要 MyBatis 生态
- 选择 **JPA**：完全面向对象建模、可以接受隐式 SQL 执行

### MyBatis 项目可以无缝升级吗？

**可以**。MyBatisGX 完全兼容原生 MyBatis：

1. 添加 MyBatisGX 依赖
2. 配置 `@MybatisgxScan` 注解
3. 原有 mapper.xml 继续使用
4. 逐步将 DAO 接口继承 SimpleDao

## 使用问题

### 如何覆盖自动生成的 SQL？

在 mapper.xml 中定义同名方法，XML 中的 SQL 拥有最高优先级：

```xml
<!-- UserMapper.xml -->
<select id="findByName" resultType="User">
    SELECT * FROM user WHERE name = #{name} AND status = 1
</select>
```

### 查询条件为空时查询全部？

使用 `@Dynamic` 注解，条件为空时自动跳过：

```java
@Dynamic
List<User> findByNameAndAge(String name, Integer age);

// name 和 age 都为 null 时，查询全部
List<User> users = userDao.findByNameAndAge(null, null);
```

### 如何处理 N+1 问题？

使用 BATCH 抓取模式：

```java
@OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)  // 批量查询，解决 N+1
private List<User> userList;
```

### 懒加载异常如何解决？

懒加载需要在事务内或 Session 上下文访问：

```java
@Service
@Transactional
public class UserService {

    public Org getOrgWithUsers(Long id) {
        Org org = orgDao.findById(id);
        org.getUserList().size();  // 在事务内访问
        return org;
    }
}
```

### 如何自定义主键生成策略？

实现 `ValueProcessor` 接口：

```java
public class CustomIdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "id".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // 自定义生成逻辑
        return generateId();
    }
}

// 使用
@Id
@GeneratedValue(CustomIdValueProcessor.class)
private Long id;
```

### 关联查询不生效？

检查以下条件：

1. 是否在 mapper.xml 中定义了同名方法（XML 优先）
2. 关联字段是否配置了正确的 `@JoinColumn`
3. FetchType 是否设置为 EAGER（默认 LAZY）

### 如何实现复杂查询？

对于复杂查询，建议：

1. **使用 QueryEntity**：封装复杂查询条件
2. **使用 @Statement**：支持条件分组
3. **使用 mapper.xml**：手写 SQL，最灵活

```java
// 方式1：QueryEntity
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
    private List<Long> idIn;
}

// 方式2：@Statement
@Statement("findBy(NameLikeAndAgeGt)Or(DeptAndStatus)")
List<User> findComplex(String name, Integer age, String dept, Integer status);

// 方式3：mapper.xml
<select id="findComplex" resultType="User">
    SELECT * FROM user WHERE ...
</select>
```

### 批量操作性能如何优化？

1. **调整批次大小**：根据数据复杂度调整
2. **分批处理**：大数据量分多次处理
3. **关闭索引**：导入前关闭索引，导入后重建

```java
// 分批处理
List<User> allUsers = ...;
int batchSize = 1000;
for (int i = 0; i < allUsers.size(); i += batchSize) {
    List<User> batch = allUsers.subList(i, Math.min(i + batchSize, allUsers.size()));
    userDao.insertBatch(batch);
}
```

### 如何处理唯一约束冲突？

配合逻辑删除 ID：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @LogicDelete
    private Integer status;

    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;
}

-- 数据库联合唯一约束
CREATE TABLE user (
    ...
    UNIQUE (name, logic_delete_id)
);
```

## 其他问题

### 支持哪些数据库？

- MySQL
- Oracle
- PostgreSQL

### 需要修改现有 MyBatis 配置吗？

不需要。MyBatisGX 兼容现有 MyBatis 配置，只需添加：

```yaml
mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
```

### 如何查看生成的 SQL？

1. 启动时查看生成的 MyBatis XML（预生成）
2. 开启 MyBatis SQL 日志：

```yaml
logging:
  level:
    com.example.dao: DEBUG
```

### 可以和 MyBatis-Plus 共存吗？

不建议。两个框架都是 MyBatis 增强，可能产生冲突。建议选择其一。

## 更多问题

如有其他问题，请访问官网：http://www.mybatisgx.com
