---
sidebar_position: 2
---

# MGXQL 对象查询语言

> 基于实体对象的声明式查询，通过 `@Statement` 注解使用

## 什么是 MGXQL

MGXQL（MyBatisGX Query Language）是 MyBatisGX 的对象查询语言，对标弱化版 HQL。它基于**实体对象**（而非数据库表）进行查询，通过 `@Statement` 注解声明在 Mapper 接口方法上。

```
┌────────────────────────────────────────────────────────────┐
│                  MGXQL 统一 IR 架构                         │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  方法名派生 ──▶ mgxql 字符串 ──┐                           │
│  (findByNameAndAgeGt)          │                           │
│                                ▼                           │
│  实体字段 ──▶ mgxql 字符串 ──▶ MgxqlStatement ──▶ SQL     │
│                                ▲                           │
│  @Statement ──▶ mgxql 字符串 ──┘                           │
│  (手写)                                                    │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

所有查询条件（方法名、实体字段、手写表达式）最终都转换为 MGXQL，经语法校验和语义校验后生成 MyBatis XML。

## 快速开始

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @Statement("select * from User where id = :id")
    User findById(@Param("id") Long id);

    @Statement("select * from User where name like :name and age > :age")
    List<User> search(@Param("name") String name, @Param("age") Integer age);
}
```

:::tip
MGXQL 使用**实体类名**（`User`）和**属性名**（`name`），而非表名和列名。框架自动完成到数据库的映射。
:::

---

## 语句类型

MGXQL 支持四种语句类型：

### SELECT — 查询

```sql
select 查询项 from 实体 [where 条件] [group by 字段] [having 条件] [order by 排序] [limit 偏移, 条数]
```

最完整的语句类型，详见后续各子句章节。

### DELETE — 删除

```sql
delete User where id = :id
```

- WHERE 子句**必填**（安全要求，防止全表删除）
- 如果实体配置了 `@LogicDelete`，自动转为逻辑删除

```java
@Statement("delete User where id = :id")
int deleteById(@Param("id") Long id);
```

### UPDATE — 更新

```sql
update User where id = :id
```

- WHERE 子句**必填**
- 更新数据来自方法参数中的实体对象
- 如果实体配置了 `@Version`，自动追加乐观锁条件

```java
@Statement("update User where id = :id")
int updateById(@Param("id") Long id, User entity);
```

### INSERT — 插入

```sql
insert User
```

- 无条件、无参数，仅声明插入目标实体
- 实体数据来自方法参数

```java
@Statement("insert User")
int insert(User user);
```

---

## SELECT 子句详解

### 查询项（SELECT）

#### 全字段查询

```sql
select * from User
select u.* from User u left join Role r on u = r
```

| 写法 | 含义 |
|------|------|
| `*` | 查询所有实体的全部字段 |
| `alias.*` | 查询指定别名实体的全部字段 |

#### 指定字段

```sql
select name, age from User
select u.name, u.age from User u
```

#### 聚合函数

```sql
select count(*) from User
select count(id) from User where status = :status
select max(age), min(age), avg(age) from User
select sum(salary) from User where dept = :dept
```

| 函数 | 允许的参数 | 说明 |
|------|-----------|------|
| `count(field)` | 字段 | 统计非空行数 |
| `count(*)` | 星号 | 统计总行数 |
| `count(1)` | 数字 | 统计总行数 |
| `max(field)` | 仅字段 | 最大值 |
| `min(field)` | 仅字段 | 最小值 |
| `avg(field)` | 仅字段 | 平均值 |
| `sum(field)` | 仅字段 | 求和 |

:::caution
`max/min/avg/sum` 只接受字段参数，不支持 `*` 和数字。
:::

---

### FROM / JOIN 子句

#### 单实体查询

```sql
select * from User where name = :name
select * from User u where u.name = :name
```

- 单实体时别名可选
- 实体名必须首字母大写（`User`、`UserDetail`）

#### LEFT JOIN 关联查询

```sql
select u.*, r.* from User u left join Role r on u = r
```

**ON 条件极简写法**：只需写 `on 别名1 = 别名2`，框架根据 JPA 关系注解（`@OneToOne`、`@OneToMany`、`@ManyToMany`）自动推导外键关联。

```sql
-- 多表链式 JOIN
select u.* from User u
  left join UserDetail ud on u = ud
  left join UserDetailItem i on ud = i
```

:::info
- 只支持 `left join`（不支持 inner/right/cross join）
- JOIN 场景下所有实体**必须**使用别名
- 别名在查询内必须唯一
:::

#### 多对多自动推导

对于 `@ManyToMany` 关系，框架自动插入中间表：

```sql
-- 你写的 MGXQL
select u.*, m.* from User u
  left join Role r on u = r
  left join Menu m on r = m

-- 框架生成的 SQL（自动展开中间表）
-- user → user_role → role → role_menu → menu
```

---

### WHERE 子句

#### 比较运算符

| 运算符 | 示例 |
|--------|------|
| `=` | `name = :name` |
| `!=` | `status != :status` |
| `<` / `<=` | `age < :age` |
| `>` / `>=` | `age >= :minAge` |

#### 匹配运算符

| 运算符 | 示例 | 说明 |
|--------|------|------|
| `like` | `name like :name` | 模糊匹配 |
| `left like` | `name left like :name` | 左模糊（`%value`） |
| `right like` | `name right like :name` | 右模糊（`value%`） |
| `in` | `id in :ids` | 包含 |
| `not in` | `id not in :ids` | 不包含 |
| `between` | `age between :ageRange` | 区间 |
| `not between` | `age not between :ageRange` | 不在区间 |
| `not like` | `name not like :name` | 不模糊匹配 |

#### NULL 判断

```sql
where name is null
where name is not null
```

#### 逻辑组合

```sql
where name = :name and (age < :age or status = :status)
```

- `and` 优先级高于 `or`
- 括号 `()` 可覆盖优先级

#### 参数引用

```sql
where name = :name              -- 简单参数（对应 @Param）
where user.name = :user.name    -- 嵌套属性路径
where age > 18                  -- 数字字面量
```

---

### 动态条件块

MGXQL 支持在 WHERE 子句中使用动态条件，当参数为空时自动跳过：

#### `#[body]` — 自动守卫

```sql
select * from User where #[name = :name] #[and age > :age]
```

- 自动从 body 中提取参数，生成 `isNotEmpty()` 判断
- 参数为 null/空字符串/空集合时，该条件不生成

```java
@Statement("select * from User where #[name = :name] #[and age > :age]")
List<User> search(@Param("name") String name, @Param("age") Integer age);
// name=null, age=25 → WHERE age > 25
// name="张", age=null → WHERE name like '%张%'（如果用了 like）
```

#### `#if(expr)[body]` — 自定义守卫

```sql
select * from User where #if(:age != null)[age >= :age]
```

- guard 中使用 `:param` 引用参数（渲染时自动去冒号）
- 支持 `&&`、`||`、`==`、`!=`、`>`、`<` 等 OGNL 表达式

```java
@Statement("select * from User where #if(:minAge != null && :maxAge != null)[age between :ageRange]")
List<User> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge, ...);
```

#### `#choose/#when/#otherwise` — 多分支互斥

```sql
select * from User where #choose[
  #when(:type == 'vip')[level = :level]
  #when(:type == 'normal')[status = :status]
  #otherwise[code = :code]
]
```

- 对标 MyBatis `<choose>/<when>/<otherwise>` 标签
- `#when` 必须带 guard 表达式
- `#otherwise` 可选

:::caution
动态条件块 body 内**不支持再嵌套**动态门。
:::

---

### IN 简写

```sql
-- 简单集合
select * from User where id in :idList

-- 复杂对象集合（属性访问）
select * from User where id in (item:userList)=>$item.id
```

框架自动生成 `<foreach>` 标签。

---

### LIKE 简写

```sql
-- 双侧模糊 → LIKE '%keyword%'
select * from User where #[name like %:name%]

-- 右侧模糊 → LIKE 'keyword%'
select * from User where #[name like :name%]

-- 左侧模糊 → LIKE '%keyword'
select * from User where #[name like %:name]
```

框架自动生成 `<bind>` 标签拼接模糊值。

---

### GROUP BY 子句

```sql
select count(u.id) from User u group by u.code
select count(u.id) from User u group by u.code, u.dept
```

---

### HAVING 子句

```sql
having count(u.id) > :minCount
having count(u.id) > :minCount and max(u.age) > :maxAge
having (count(u.id) > :minCount or max(u.id) > :minId) and avg(u.age) > :avgAge
```

- 只支持聚合函数比较，不支持普通字段
- 比较值：参数引用（`:param`）或数字字面量
- 支持 `and`/`or` 和括号分组

---

### ORDER BY 子句

```sql
select * from User order by name desc
select * from User order by name desc, age asc
```

- 默认升序（`asc`）
- 多字段用逗号分隔

---

### LIMIT 子句

```sql
select * from User where name = :name limit 0, 10
```

- 格式：`limit 偏移量, 条数`
- 只支持固定数字（动态分页请用 `Pageable` 参数）

---

## 从 `?` 前缀迁移

旧版 MGXQL 使用 `?` 前缀表示可选条件，现已废弃，请迁移为动态条件块：

| 旧写法 | 新写法 | 说明 |
|--------|--------|------|
| `?name = :name` | `#[name = :name]` | 自动 isNotEmpty 守卫 |
| `and ?age >= :age` | `#[and age >= :age]` | 连接词写在 body 开头 |
| `or ?code like :code` | `#[or code like %:code%]` | LIKE 模式可直接写 |
| 自定义非空规则 | `#if(:age != null)[age >= :age]` | 使用 #if 自定义 guard |
| 多分支条件 | `#choose[#when(...)[...] #otherwise[...]]` | 使用 choose 多分支 |

---

## 命名规则

| 类型 | 格式 | 示例 |
|------|------|------|
| 实体名 | 大写字母开头 | `User`、`UserDetail`、`Role` |
| 别名 | 小写字母开头 或 反引号包裹 | `u`、`ud`、`` `user` `` |
| 字段名 | 小写字母开头 或 反引号包裹 | `name`、`id`、`` `inputTime` `` |
| 参数名 | `:` 前缀 | `:id`、`:user.name` |

:::tip
字段名对应 Java 实体**属性名**，而非数据库列名。框架自动完成属性→列的映射。
:::

---

## 校验规则

MGXQL 在启动时进行两轮校验：

### 语法校验

| 规则 | 说明 |
|------|------|
| DELETE/UPDATE 必须有 WHERE | 防止全表操作 |
| JOIN 场景字段必须带别名 | `u.name` 而非 `name` |
| 聚合函数参数限制 | max/min/avg/sum 只接受字段 |
| 别名必须已声明 | 字段引用中的别名必须在 FROM/JOIN 中出现 |

### 语义校验

| 规则 | 说明 |
|------|------|
| 实体名必须已注册 | 对应 Spring 容器中的实体类 |
| 字段名必须存在 | 对应实体的属性 |
| JOIN 必须有关系注解 | `@OneToOne`/`@OneToMany`/`@ManyToMany` 支撑 |
| SELECT/WHERE 字段归属 | 字段必须属于 FROM/JOIN 中的实体 |

---

## 完整示例

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 简单查询
    @Statement("select * from User where id = :id")
    User findUser(@Param("id") Long id);

    // 关联查询
    @Statement("select u.* from User u left join UserDetail ud on u = ud where u.id = :id")
    User findUserWithDetail(@Param("id") Long id);

    // 聚合 + 分组 + HAVING
    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount")
    List<Map<String, Object>> groupByCode(@Param("minCount") long minCount);

    // 动态条件
    @Statement("select * from User where #[name like %:name%] #[and age > :age] #[and id in :idList]")
    List<User> search(@Param("name") String name, @Param("age") Integer age, @Param("idList") List<Long> idList);

    // 多分支
    @Statement("select * from User where #choose[#when(:type == 'vip')[level >= :level] #otherwise[status = :status]]")
    List<User> findByType(@Param("type") String type, @Param("level") Integer level, @Param("status") Integer status);

    // 删除
    @Statement("delete User where id = :id")
    int deleteById(@Param("id") Long id);

    // 更新
    @Statement("update User where code = :code")
    int updateByCode(@Param("code") String code, User entity);

    // 插入
    @Statement("insert User")
    int insert(User user);

    // 排序 + 分页
    @Statement("select * from User order by id desc limit 0, 10")
    List<User> findTop10();
}
```

## 下一步

- 了解 [MGXSQL 动态 SQL 语法](./mgxsql) — 基于真实表名的动态条件
- 回到 [总览决策指南](./overview) — 选择合适的查询方式
