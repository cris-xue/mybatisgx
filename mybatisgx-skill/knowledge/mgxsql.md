# MGXSQL — MyBatisGX Dynamic SQL Syntax

## What is MGXSQL?

MGXSQL is a simplified dynamic SQL syntax for MyBatisGX. It uses a state-machine scanner (`MgxsqlScanner`) to convert concise syntax into standard MyBatis XML dynamic tags (`<where>`, `<set>`, `<if>`, `<foreach>`, `<bind>`, `<choose>`, `<include>`).

```
"select * from t_user where #[name = :name] and age = :age"
    │ MgxsqlScanner.process()
    ▼
"select * from t_user <where><if test="isNotEmpty(name)">name = #{name}</if> and age = #{age}</where>"
```

Used via `@Lang(MgxsqlLanguageDriver.class)` + `@Select/@Update/@Delete`, or via global configuration.

> **Note**: MGXSQL is different from MGXQL (the query language used in `@Statement`). MGXSQL uses real table/column names; MGXQL uses entity/property names. See [MGXQL vs MGXSQL](#mgxsql-vs-mgxql-when-to-use-which) below.

---

## Scope Blocks vs Condition Node Blocks

MGXSQL has two types of "blocks" with different rules:

| | Scope Block | Condition Node Block |
|---|---|---|
| **Representatives** | `where` / `where[]` / `set` / `set[]` | `#[body]` / `#if(expr)[body]` / `#condition` / `#and` / `#or` |
| **Purpose** | Delimit SQL clause scope | Generate `<if>` dynamic conditions |
| `#{param}` | Preserved as-is | Syntax error (use `:param`) |
| `${param}` | Preserved as-is | Syntax error |
| `<xml-tag>` | Preserved as-is | Syntax error |
| `:param` | Converts to `#{param}` | Converts to `#{param}` |
| `$variable` | Only on `=>` right side | Only on `=>` right side |

---

## Scope Tags

### `where` → `<where>...</where>`

| Syntax | Description |
|--------|-------------|
| `where` | Auto-open, closes at clause keyword or end |
| `where[body]` | Bounded, closes at `]` |

**Auto-close triggers:** `order by`, `group by`, `having`, `limit`, `union`, `union all`, `intersect`, `except`, `for update`

```sql
-- Unbounded (closes at end)
select * from t_user where id = :id
→ select * from t_user <where>id = #{id}</where>

-- Unbounded (closes at order by)
select * from t_user where #[id = :id] order by id
→ <where><if test="isNotEmpty(id)">id = #{id}</if></where> order by id

-- Bounded
select * from t_user where[id = :id] order by id
→ <where>id = #{id}</where> order by id

-- Bounded + conditions
select * from t_user where[id = :id #[and name = :name]] order by id
→ <where>id = #{id} <if test="isNotEmpty(name)">and name = #{name}</if></where> order by id
```

### `set` → `<set>...</set>`

| Syntax | Description |
|--------|-------------|
| `set` | Auto-open, closes at `where` or end |
| `set[body]` | Bounded, closes at `]` |

```sql
update t_user set name = :name where id = :id
→ update t_user <set>name = #{name}</set><where>id = #{id}</where>

update t_user set[#[name = :name], #[age = :age]] where id = :id
→ <set><if test="isNotEmpty(name)">name = #{name}</if>, <if test="isNotEmpty(age)">age = #{age}</if></set><where>id = #{id}</where>
```

---

## Condition Tags

### `#[body]` — Auto-guard condition

Auto-extracts `:param` from direct children, generates `isNotEmpty()` OGNL test. Nested conditions have their own guards (params don't bubble up).

```sql
where #[name = :name]
→ <where><if test="isNotEmpty(name)">name = #{name}</if></where>

-- With and/or prefix
where id = :id #[and status = :status]
→ <where>id = #{id} <if test="isNotEmpty(status)">and status = #{status}</if></where>

-- Multi-param body (and-joined in test)
where #[name = :name and age = :age]
→ <if test="isNotEmpty(name) and isNotEmpty(age)">name = #{name} and age = #{age}</if>

-- Nested (inner params don't bubble)
where #[id = :id #[and name = :name]]
→ <if test="isNotEmpty(id)">id = #{id} <if test="isNotEmpty(name)">and name = #{name}</if></if>
```

### `#if(expr)[body]` — Custom guard condition

`expr` is a custom OGNL expression. `:param` in guard auto-strips colon.

```sql
where #if(:age > 2 && :age < 18)[or(name like :name and age = :age)]
→ <if test="age > 2 && age < 18">or(name like #{name} and age = #{age})</if>

-- Guard without colon
where #if(age > 2)[name = :name]
→ <if test="age > 2">name = #{name}</if>

-- Empty guard (same as #[body])
where #if()[and status = :status]
→ <if test="isNotEmpty(status)">and status = #{status}</if>
```

### `#condition` — Form 1 (must be on its own line)

`#` followed by an identifier. **Must occupy its own line** (only whitespace before `#` since last newline).

```sql
where
  #id = :id
→ <where><if test="isNotEmpty(id)">id = #{id}</if></where>

-- ERROR: inline
where #id = :id
→ mgxsql syntax error: '#condition' form 1 must be on its own line
```

### `#and` / `#or` — Line-prefix conditions (must be on their own line)

Include `and`/`or` inside the `<if>` tag. Only valid in scope block top-level.

```sql
where
  #id = :id
  #and name = :name
→ <where><if test="isNotEmpty(id)">id = #{id}</if><if test="isNotEmpty(name)">and name = #{name}</if></where>

-- SET domain with comma prefix
update t_user set
  #name = :name
  #,age = :age
where id = :id
→ <set><if test="isNotEmpty(name)">name = #{name}</if><if test="isNotEmpty(age)">, age = #{age}</if></set>
```

> **Restriction**: `#and`/`#or` are NOT allowed inside `#[...]` / `#if(expr)[...]`. Use nested `#[and ...]` instead.

### `#choose/#when/#otherwise` — Multi-branch mutual exclusion

Maps to MyBatis `<choose>/<when>/<otherwise>`.

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

- `#when(expr)` MUST have guard expression
- `#otherwise` optional (0 or 1)
- Body follows condition node block rules (`:param`/`in :list`/`%:name%` allowed; `#{}`/`${}`/`<xml>` forbidden)
- Supports nesting (same-system only)
- Only effective in WHERE/SET scope

---

## IN Clause

### Simple collection

```sql
where id in :idList
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item}</foreach>

-- Parenthesized (equivalent)
where id in (:idList)
```

### Complex object collection

```sql
where id in (item:idList)=>$item.id
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item.id}</foreach>
```

> **Restriction**: `=>` right side only accepts `$variable`, not `#{}` or `${}`.

### Tuple IN (composite key)

```sql
where (id, status) in (item:list)=>[$item.id,$item.status]
→ (id, status) in <foreach ... separator="),(">#{item.id},#{item.status}</foreach>
```

### MyBatis native (not translated)

```sql
where id in #{idList}
→ preserved as-is, no <foreach> generated
```

---

## LIKE Patterns

| Syntax | Pattern | Generated bind value |
|--------|---------|----------------------|
| `%:name%` | Both-side fuzzy | `'%' + name + '%'` |
| `:name%` | Right-side fuzzy | `name + '%'` |
| `%:name` | Left-side fuzzy | `'%' + name` |

Auto-generates `<bind>` tag with `_like_paramName` variable.

```sql
where #[name like %:name%]
→ <if test="isNotEmpty(name)">name like <bind name="_like_name" value="'%' + name + '%'"/>#{_like_name}</if>
```

---

## `#for(item:collection)=>expr` — Standalone Foreach Directive

Decoupled from `in` keyword, directly generates `<foreach>`:

```sql
-- Simple iteration
status in #for(item:statusList)=>$item
→ status in <foreach item="item" collection="statusList" ...>#{item}</foreach>

-- Property access
id in #for(item:ObjectList)=>$item.id
→ id in <foreach ...>#{item.id}</foreach>

-- Single token ([] optional for single item)
status in #for(item:statusList)=>[$item]
→ same as =>$item

-- Composite key (tuple IN, separator="),(")
(id, type) in #for(item:list)=>[$item.id,$item.type]
→ (id, type) in <foreach ... separator="),(">#{item.id},#{item.type}</foreach>
```

Coexists with `in :list` sugar. Use `in :list` for simple cases, `#for` for composite keys or explicit control.

---

## `#include[sqlId]` — Fragment Reference

Generates `<include refid="sqlId"/>`.

```sql
select * from t_user where id = :id
#include[commonConditions]
```

- refid: static identifier only (no `:param`)
- Valid in: top-level, where/set scope, `#[...]` body, `#if(...)[...]` body
- Forbidden inside native XML tags

---

## `#bind[name = expr]` and `$name` — Computed Variables

```sql
-- Declaration
#bind[age2 = :age + :age]
→ <bind name="age2" value="age + age"/>

-- Reference ($name → #{name})
#if(:age != null)[id = $age2]
→ <if test="age != null">id = #{age2}</if>

-- Declaration inside condition body
#if(:x != null)[#bind[b = :y] k = $b]

-- String/number literals allowed
#bind[p = :code + '_x']
```

**Rules:**
- `:param` in value auto-strips colon → OGNL expression
- Declaration MUST precede reference (parser-enforced)
- Name unique per select/update scope
- Value only accepts: `:param` + operators + number/string literals
- Forbidden in value: `$var`, bare identifiers, `#{}`, `${}`
- `$var` does NOT contribute to auto-guard: `#[id = $age2]` → `<if test="true">`

---

## Parameter Binding

| Syntax | Converts to | Scope Block | Condition Node Block |
|--------|-------------|-------------|----------------------|
| `:param` | `#{param}` | Allowed | Allowed |
| `:user.name` | `#{user.name}` | Allowed | Allowed |
| `#{param}` | Preserved as-is | Allowed | **Error** |
| `${param}` | Preserved as-is | Allowed | **Error** |
| `$variable` | `#{variable}` | Only on `=>` right | Only on `=>` right |

---

## Native XML Tag Handling

### Three-tag descent (`<where>`/`<set>`/`<trim>`)

Tags preserved with all attributes; **inner text** undergoes MGXSQL translation:

```sql
select * from user <where> #[name = :name] <if test="status != null">and status = #{status}</if> </where>
→ <where><if test="isNotEmpty(name)">name = #{name}</if> <if test="status != null">and status = #{status}</if></where>
```

### Other tags: block pass-through (`<if>`/`<foreach>`/`<choose>` etc.)

Entire block preserved as-is, inner text NOT translated:

```sql
where <if test="id != null">id = #{id}</if> and #[name = :name]
→ <if test="id != null">id = #{id}</if> (pass-through) + <if test="isNotEmpty(name)">name = #{name}</if> (translated)
```

> **Atomicity rule**: Native tags internally forbid `#[`/`#if(` (mixing error).

---

## String Literals

Single-quoted strings: all MGXSQL syntax ignored inside `'...'`:

```sql
where name = 'where something' and #[code = :code]
→ 'where something' does not trigger scope tag
```

Supports SQL escaped quotes (`''`).

---

## Usage

### Method 1: `@Lang` annotation (per-method)

```java
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where\n  #id = :id\n  #and name like %:name%")
List<User> findByIdAndName(@Param("id") Long id, @Param("name") String name);
```

### Method 2: Global configuration

```yaml
mybatis:
  configuration:
    default-scripting-language: com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver
```

---

## OGNL Test Expression Generation

`#[body]` and `#condition` auto-extract `:param` paths from direct children:

| Body params | Generated test |
|-------------|---------------|
| `:name` | `isNotEmpty(name)` |
| `:user.name` | `isNotEmpty(user) and isNotEmpty(user.name)` |
| `:name` + `:age` | `isNotEmpty(name) and isNotEmpty(age)` |

Uses `@com.mybatisgx.utils.ObjectUtils@isNotEmpty()`. When no params extracted, test is `true`.

---

## Syntax Quick Reference

| MGXSQL Syntax | Converted Result |
|---------------|------------------|
| `where` | `<where>...` (closes at clause keyword or end) |
| `where[body]` | `<where>body</where>` |
| `set` | `<set>...` (closes at where or end) |
| `set[body]` | `<set>body</set>` |
| `#[body]` | `<if test="isNotEmpty(...)">body</if>` |
| `#if(expr)[body]` | `<if test="expr">body</if>` |
| `#condition` (own line) | `<if test="isNotEmpty(...)">condition</if>` |
| `#and condition` (own line) | `<if test="isNotEmpty(...)">and condition</if>` |
| `#or condition` (own line) | `<if test="isNotEmpty(...)">or condition</if>` |
| `#choose[#when(expr)[body] #otherwise[body]]` | `<choose><when>...</when><otherwise>...</otherwise></choose>` |
| `:param` | `#{param}` |
| `in :list` | `<foreach ...>` |
| `in (item:list)=>$item.x` | `<foreach ...>#{item.x}</foreach>` |
| `#for(item:list)=>expr` | `<foreach ...>` |
| `%:name%` | `<bind>` + both-side LIKE |
| `#include[sqlId]` | `<include refid="sqlId"/>` |
| `#bind[name = expr]` | `<bind name="name" value="expr"/>` |
| `$name` | `#{name}` |
| `<where>/<set>/<trim>` | Descent: tag preserved + inner translated |
| `<if>/<foreach>/other` | Block pass-through, inner not translated |
| `'string literal'` | Preserved, internal syntax ignored |

---

## Common Syntax Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `条件节点块内不允许使用 #{param}` | `#[id = #{id}]` | Use `:param`: `#[id = :id]` |
| `条件节点块内不允许使用 ${param}` | `#[id = ${id}]` | Use `:param` |
| `条件节点块内不允许使用 XML 标签` | `#[<if>...</if>]` | Use `#if(expr)[body]` |
| `'#condition' 形式1必须独占一行` | `where #id = :id` (inline) | Newline: `where\n  #id = :id` |
| `'#and'/'#or' 必须独占一行` | `where #and name = :name` | Newline or use `#[and name = :name]` |
| `条件节点块内不允许 #and/#or` | `#[id = :id #and name = :name]` | Nested: `#[id = :id #[and name = :name]]` |
| `'[' 未闭合` | `where[id = :id` | Close bracket |
| `'=>' 右边只接受 $variable` | `in (item:list)=>#{item.id}` | Use `$item.id` |
| `#when 必须带 guard` | `#choose[#when[...]]` | Use `#when(expr)[...]` |
| `#(expr) 已废弃` | `#(age > 2)[...]` | Use `#if(age > 2)[...]` |
| `#bind 声明先于引用` | `$name` before `#bind[name = ...]` | Move declaration before reference |

---

## Deprecated Syntax

| Old Syntax | Status | Replacement |
|------------|--------|-------------|
| `#(expr)[body]` | Deprecated (parser error) | `#if(expr)[body]` |
| `#(expr)(body)` | Not supported | `#if(expr)[body]` |
| `?condition` | Fully deprecated | `#condition` or `#[body]` |

---

## MGXSQL vs MGXQL: When to Use Which

| Dimension | MGXQL (@Statement) | MGXSQL (@Lang + @Select) |
|-----------|-------------------|--------------------------|
| Positioning | Object query language, HQL-like | Dynamic SQL syntax sugar |
| SQL basis | Entity/field names | Real table/column names |
| Dynamic conditions | `#[body]`, `#if`, `#choose` | `#[body]`, `#if`, `#condition`, `#and/#or`, `#choose` |
| JOIN | Supported | Not supported |
| Aggregation | Supported | Not supported |
| IN/LIKE shorthand | Supported | Supported + `#for` directive |
| Include/Bind | Not supported | `#include[sqlId]`, `#bind[name = expr]` |
| Use case | Complex queries, multi-table | Dynamic conditions, optional params |

See [MGXQL Syntax Reference](mgxql.md) for complete MGXQL documentation.
