---
sidebar_position: 3
---

# MGXSQL 动态 SQL 语法

> 用简洁语法替代 MyBatis XML 动态标签，通过 `@Lang` 注解使用

## 什么是 MGXSQL

MGXSQL 是 MyBatisGX 的简化动态 SQL 语法。它通过状态机扫描器（`MgxsqlScanner`）将简洁语法转换为标准 MyBatis XML 动态标签（`<where>`、`<set>`、`<if>`、`<foreach>`、`<bind>`）。

```
┌──────────────────────────────────────────────────────────────┐
│                      MGXSQL 文本                              │
│  "select * from t_user where #[name = :name] and age = :age" │
└──────────────────────┬───────────────────────────────────────┘
                       │ MgxsqlScanner 处理
                       ▼
┌──────────────────────────────────────────────────────────────┐
│                  标准 MyBatis XML 动态标签                     │
│  <where><if test="isNotEmpty(name)">name = #{name}</if>      │
│   and age = #{age}</where>                                    │
└──────────────────────────────────────────────────────────────┘
```

:::info MGXSQL vs MGXQL
- **MGXSQL**：基于真实表名/列名，用于 `@Lang` + `@Select/@Update/@Delete`。它是透传式扫描器，只翻译 `#` 动态指令，其余 SQL（JOIN、聚合、子查询等）手写透传，能力与 MyBatis 手写 SQL 对齐
- **MGXQL**：基于实体名/属性名，用于 `@Statement`，对象级声明 JOIN 和聚合（受限 DSL 子集）

详见 [总览决策指南](./overview)。
:::

## 快速开始

```java
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[name = :name] #[and age > :age]")
List<User> search(@Param("name") String name, @Param("age") Integer age);
```

等价于手写 MyBatis XML：

```xml
<select id="search" resultType="User">
  select * from t_user
  <where>
    <if test="isNotEmpty(name)">name = #{name}</if>
    <if test="isNotEmpty(age)">and age > #{age}</if>
  </where>
</select>
```

---

## 核心概念：范围块 vs 节点块

MGXSQL 有两种"块"，对 `#{}`/`${}`/`<xml>` 的处理规则不同：

| | 条件范围块（Scope Block） | 条件节点块（Condition Node Block） |
|---|---|---|
| **代表** | `where` / `where[]` / `set` / `set[]` | `#[body]` / `#if(expr)[body]` / `#condition` |
| **本质** | 界定 SQL 子句的作用范围 | 生成 `<if>` 动态条件 |
| `#{param}` | 原样保留 | 语法错误（请用 `:param`） |
| `${param}` | 原样保留 | 语法错误 |
| `<xml-tag>` | 原样透传 | 语法错误 |
| `:param` | 转为 `#{param}` | 转为 `#{param}` |

:::caution 最重要的规则
条件节点块（`#[...]`、`#if(...)[...]`）内**禁止**使用 `#{param}`、`${param}`、`<xml>` 标签。统一使用 `:param`。
:::

---

## 域标签

### where — 自动生成 `<where>`

```sql
-- 无边界（到子句关键字或末尾自动关闭）
select * from t_user where id = :id
→ select * from t_user <where>id = #{id}</where>

-- 到 order by 自动关闭
select * from t_user where #[id = :id] order by id
→ <where><if test="isNotEmpty(id)">id = #{id}</if></where> order by id
```

**自动关闭触发关键字**：`order by`、`group by`、`having`、`limit`、`union`、`union all`、`intersect`、`except`、`for update`

```sql
-- 有边界（] 关闭）
select * from t_user where[id = :id] order by id
→ <where>id = #{id}</where> order by id

-- 有边界 + 条件标签
select * from t_user where[id = :id #[and name = :name]] order by id
→ <where>id = #{id} <if test="isNotEmpty(name)">and name = #{name}</if></where> order by id
```

:::tip
`where[` 和 `where [` 等价（空格容忍）。当自动识别范围有问题时，使用有边界形式 `where[...]`。
:::

### set — 自动生成 `<set>`

```sql
-- set 到 where 自动关闭
update t_user set name = :name where id = :id
→ update t_user <set>name = #{name}</set><where>id = #{id}</where>

-- set 有边界 + 条件
update t_user set[#[name = :name], #[age = :age]] where id = :id
→ <set><if test="isNotEmpty(name)">name = #{name}</if>, <if test="isNotEmpty(age)">age = #{age}</if></set><where>id = #{id}</where>
```

---

## 条件标签

### `#[body]` — 自动守卫条件

自动从 body 中提取 `:param`，生成 `isNotEmpty()` 判断：

```sql
where #[name = :name]
→ <where><if test="isNotEmpty(name)">name = #{name}</if></where>

-- 带 and/or 前缀
where id = :id #[and status = :status]
→ <where>id = #{id} <if test="isNotEmpty(status)">and status = #{status}</if></where>

-- 多参数（and 连接）
where #[name = :name and age = :age]
→ <if test="isNotEmpty(name) and isNotEmpty(age)">name = #{name} and age = #{age}</if>

-- 嵌套（内层参数不冒泡）
where #[id = :id #[and name = :name]]
→ <if test="isNotEmpty(id)">id = #{id} <if test="isNotEmpty(name)">and name = #{name}</if></if>
```

### `#if(expr)[body]` — 自定义守卫

```sql
-- 自定义 OGNL 表达式
where #if(:age > 2 && :age < 18)[name = :name]
→ <if test="age > 2 && age < 18">name = #{name}</if>

-- guard 中 :param 自动去冒号
where #if(:status != null)[status = :status]
→ <if test="status != null">status = #{status}</if>

-- 空 guard（等价于 #[body]）
where #if()[and status = :status]
→ <if test="isNotEmpty(status)">and status = #{status}</if>
```

### `#condition` — 行首简写（必须独占一行）

```sql
where
  #id = :id
  #and name = :name
→ <where>
    <if test="isNotEmpty(id)">id = #{id}</if>
    <if test="isNotEmpty(name)">and name = #{name}</if>
  </where>
```

:::caution
- `#condition` 形式**必须独占一行**（`#` 前只允许空白字符）
- `#and`/`#or` 也**必须独占一行**
- 条件节点块 `#[...]` 内**禁止**使用 `#and`/`#or`，请用嵌套 `#[and ...]`
:::

### SET 域逗号前缀

```sql
update t_user set
  #name = :name
  #,age = :age
where id = :id
→ <set>
    <if test="isNotEmpty(name)">name = #{name}</if>
    <if test="isNotEmpty(age)">, age = #{age}</if>
  </set><where>id = #{id}</where>
```

### `#choose/#when/#otherwise` — 多分支互斥

```sql
where #choose[
  #when(:type == 'vip')[salary > :minSalary]
  #when(:type == 'svip')[salary > :minSalary and level = :level]
  #otherwise[status = :status]
]
→ <where><choose>
    <when test="type == 'vip'">salary &gt; #{minSalary}</when>
    <when test="type == 'svip'">salary &gt; #{minSalary} and level = #{level}</when>
    <otherwise>status = #{status}</otherwise>
  </choose></where>
```

- `#when(expr)` 必须带 guard
- `#otherwise` 可选
- 支持嵌套

---

## IN 子句

### 简单集合

```sql
where id in :idList
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item}</foreach>

-- 括号包裹（等价）
where id in (:idList)
```

### 复杂对象集合

```sql
where id in (item:idList)=>$item.id
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item.id}</foreach>
```

:::caution
`=>` 右边只接受 `$variable` 形式，禁止 `#{}` 和 `${}`。
:::

### 复合键 IN（元组）

```sql
where (id, status) in (item:list)=>[$item.id,$item.status]
→ (id, status) in <foreach ... separator="),(">#{item.id},#{item.status}</foreach>
```

### MyBatis 原生（不翻译）

```sql
where id in #{idList}
→ 原样保留，不生成 <foreach>
```

---

## LIKE 模式

| 语法 | 效果 | 生成 bind value |
|------|------|-----------------|
| `%:name%` | 双侧模糊 `%keyword%` | `'%' + name + '%'` |
| `:name%` | 右侧模糊 `keyword%` | `name + '%'` |
| `%:name` | 左侧模糊 `%keyword` | `'%' + name` |

```sql
where #[name like %:name%]
→ <if test="isNotEmpty(name)">name like <bind name="_like_name" value="'%' + name + '%'"/>#{_like_name}</if>
```

---

## `#for` — 独立迭代指令

脱离 `in` 关键字，直接生成 `<foreach>`：

```sql
-- 简单迭代
status in #for(item:statusList)=>$item
→ status in <foreach item="item" collection="statusList" ...>#{item}</foreach>

-- 属性访问
id in #for(item:ObjectList)=>$item.id
→ id in <foreach ...>#{item.id}</foreach>

-- 复合键（元组 IN）
(id, type) in #for(item:list)=>[$item.id,$item.type]
→ (id, type) in <foreach ... separator="),(">#{item.id},#{item.type}</foreach>
```

:::tip
`#for` 与 `in :list` 糖语法并存。简单场景用 `in :list`，需要复合键或更灵活控制时用 `#for`。
:::

---

## `#include` — 片段引用

```sql
select * from t_user where id = :id
#include[commonConditions]
```

- 生成 `<include refid="sqlId"/>`
- refid 只接受静态标识符
- 可在顶层、where/set 域、`#[...]`/`#if(...)[...]` 体内使用

---

## `#bind` — 计算变量

```sql
-- 声明
#bind[age2 = :age + :age]

-- 引用（$name → #{name}）
#if(:age != null)[id = $age2]
```

- 生成 `<bind name="age2" value="age + age"/>`
- value 中 `:param` 自动去冒号变为 OGNL
- **声明必须先于引用**（parser 校验）
- 同一 select/update 作用域内 name 唯一
- value 仅接受 `:param` + 运算符 + 字面量（禁止 `$var`、裸标识符、`#{}`、`${}`）

:::caution
`$var` 不参与 auto-guard 收集：`#[id = $age2]` → `<if test="true">`。需要守卫时请用 `#if`。
:::

---

## 参数绑定规则

| 语法 | 转换结果 | 范围块 | 节点块 |
|------|----------|--------|--------|
| `:param` | `#{param}` | 可用 | 可用 |
| `:user.name` | `#{user.name}` | 可用 | 可用 |
| `#{param}` | 原样保留 | 可用 | 禁止 |
| `${param}` | 原样保留 | 可用 | 禁止 |
| `$variable` | `#{variable}` | 仅 `=>` 右侧 | 仅 `=>` 右侧 |

---

## 原生 XML 标签处理

### 三标签下沉（`<where>`/`<set>`/`<trim>`）

标签原样透传，**内部文本**走 MGXSQL 翻译：

```sql
select * from user <where> #[name = :name] <if test="status != null">and status = #{status}</if> </where>
→ <where><if test="isNotEmpty(name)">name = #{name}</if> <if test="status != null">and status = #{status}</if></where>
```

### 其余标签整块透传（`<if>`/`<foreach>`/`<choose>` 等）

作为整体原样输出，内部不翻译：

```sql
where <if test="id != null">id = #{id}</if> and #[name = :name]
→ <if test="id != null">id = #{id}</if>（原样）+ <if test="isNotEmpty(name)">name = #{name}</if>（翻译）
```

:::caution
原生标签内部**禁止**出现 `#[`/`#if(`（原子性规则）。
:::

---

## 字符串字面量

单引号内的所有 MGXSQL 语法均不触发解析：

```sql
where name = 'where something' and #[code = :code]
→ 'where something' 内的 where 不触发域标签
```

---

## 使用方式

### 方式一：@Lang 注解（按方法）

```java
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where\n  #id = :id\n  #and name like %:name%")
List<User> findByIdAndName(@Param("id") Long id, @Param("name") String name);
```

### 方式二：全局配置

```yaml
mybatis:
  configuration:
    default-scripting-language: com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver
```

配置后所有 `@Select`/`@Update`/`@Delete` 默认使用 MGXSQL 解析。

---

## 语法错误速查

| 错误信息 | 原因 | 修复 |
|----------|------|------|
| 条件节点块内不允许使用 `#{param}` | `#[id = #{id}]` | 改用 `:param`：`#[id = :id]` |
| 条件节点块内不允许使用 `${param}` | `#[id = ${id}]` | 改用 `:param` |
| 条件节点块内不允许使用 XML 标签 | `#[<if>...</if>]` | 改用 `#if(expr)[body]` |
| '#condition' 形式1必须独占一行 | `where #id = :id`（行内） | 换行：`where\n  #id = :id` |
| '#and'/'#or' 必须独占一行 | `where #and name = :name` | 换行或改用 `#[and name = :name]` |
| 条件节点块内不允许 #and/#or | `#[id = :id #and name = :name]` | 嵌套：`#[id = :id #[and name = :name]]` |
| '[' 未闭合 | `where[id = :id` | 补充 `]` |
| '=>' 右边只接受 $variable | `in (item:list)=>#{item.id}` | 改用 `$item.id` |
| #when 必须带 guard | `#choose[#when[...]]` | 改用 `#when(expr)[...]` |
| #(expr) 已废弃 | `#(age > 2)[...]` | 改用 `#if(age > 2)[...]` |

---

## 废弃语法

| 旧语法 | 状态 | 替代方案 |
|--------|------|----------|
| `#(expr)[body]` | 已废弃（报错） | `#if(expr)[body]` |
| `#(expr)(body)` | 不支持 | `#if(expr)[body]` |
| `?condition` | 完全废弃 | `#condition` 或 `#[body]` |

---

## 语法速查表

| MGXSQL 语法 | 转换结果 |
|-------------|----------|
| `where` | `<where>...`（到子句关键字或末尾关闭） |
| `where[body]` | `<where>body</where>` |
| `set` | `<set>...`（到 where 或末尾关闭） |
| `set[body]` | `<set>body</set>` |
| `#[body]` | `<if test="isNotEmpty(...)">body</if>` |
| `#if(expr)[body]` | `<if test="expr">body</if>` |
| `#condition`（独占一行） | `<if test="isNotEmpty(...)">condition</if>` |
| `#and condition`（独占一行） | `<if test="isNotEmpty(...)">and condition</if>` |
| `#choose[#when(expr)[body] #otherwise[body]]` | `<choose><when>...</when><otherwise>...</otherwise></choose>` |
| `:param` | `#{param}` |
| `in :list` | `<foreach ...>` |
| `in (item:list)=>$item.x` | `<foreach ...>#{item.x}</foreach>` |
| `#for(item:list)=>expr` | `<foreach ...>` |
| `%:name%` | `<bind>` + 双侧 LIKE |
| `#include[sqlId]` | `<include refid="sqlId"/>` |
| `#bind[name = expr]` | `<bind name="name" value="expr"/>` |
| `$name` | `#{name}` |

## 下一步

- 了解 [MGXQL 对象查询语言](./mgxql) — JOIN、聚合、投影
- 回到 [总览决策指南](./overview) — 选择合适的查询方式
