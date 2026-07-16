# MGXSQL — MyBatisGX Dynamic SQL Syntax

## What is MGXSQL?

MGXSQL is a simplified dynamic SQL syntax for MyBatisGX. It uses a state-machine scanner (`MgxsqlScanner`) to convert concise syntax into standard MyBatis XML dynamic tags (`<where>`, `<set>`, `<if>`, `<foreach>`, `<bind>`).

```
┌──────────────────────────────────────────────────────────────┐
│                      mgxsql 文本                              │
│  "select * from t_user where #[name = :name] and age = :age" │
└──────────────────────┬───────────────────────────────────────┘
                       │ MgxsqlScanner.process()
                       ▼
┌──────────────────────────────────────────────────────────────┐
│                  标准 MyBatis XML 动态标签                     │
│  "select * from t_user <where><if test="...">name = #{name}  │
│   </if> and age = #{age}</where>"                             │
└──────────────────────────────────────────────────────────────┘
```

> **Note**: MGXSQL is different from MGXQL (the query language used in `@Statement`). See [MGXQL vs MGXSQL](#mgxsql-vs-mgxql-when-to-use-which) below.

---

## Scope Blocks vs Condition Node Blocks

MGXSQL has two types of "blocks" with different rules for `#{}`/`${}`/`<xml>`:

```
┌──────────────────────────┬──────────────────────────────────┐
│ Scope Block              │ Condition Node Block              │
│ (where / where[])        │ #[body]                           │
│ (set / set[])            │ #if(expr)[body]                     │
│                          │ #condition                        │
│                          │ #and / #or                        │
├──────────────────────────┼──────────────────────────────────┤
│ ✅ #{param} preserved    │ ❌ #{param} syntax error          │
│ ✅ ${param} preserved    │ ❌ ${param} syntax error          │
│ ✅ <xml-tag> preserved   │ ❌ <xml-tag> syntax error         │
│ ✅ #[...] conditions     │ ✅ :param → #{param} (auto)       │
│ ✅ :param → #{param}     │ ✅ $variable only on => right     │
└──────────────────────────┴──────────────────────────────────┘
```

---

## Scope Tags

### `where` → `<where>...</where>`

| Syntax | Description |
|--------|-------------|
| `where` | Auto-open `<where>`, closes at clause keyword or end |
| `where[body]` | Bounded, closes at `]` |

**Auto-close triggers:** `order by`, `group by`, `having`, `limit`, `union`, `union all`, `intersect`, `except`, `for update`

```sql
-- Unbounded (closes at end)
select * from t_user where id = :id
→ select * from t_user <where>id = #{id}</where>

-- Unbounded (closes at order by)
select * from t_user where #[id = :id] order by id
→ <where><if test="isNotEmpty(id)"> id = #{id}</if></where> order by id

-- Bounded
select * from t_user where[id = :id] order by id
→ <where>id = #{id}</where> order by id

-- Bounded + conditions
select * from t_user where[id = :id #[and name = :name]] order by id
→ <where>id = #{id} <if test="isNotEmpty(name)"> and name = #{name}</if></where> order by id
```

### `set` → `<set>...</set>`

| Syntax | Description |
|--------|-------------|
| `set` | Auto-open `<set>`, closes at `where` or end |
| `set[body]` | Bounded, closes at `]` |

```sql
-- set closes at where
update t_user set name = :name where id = :id
→ update t_user <set>name = #{name}</set><where>id = #{id}</where>

-- set with conditions
update t_user set[#[name = :name], #[age = :age]] where id = :id
→ <set><if test="isNotEmpty(name)"> name = #{name}</if>, <if test="isNotEmpty(age)"> age = #{age}</if></set><where>id = #{id}</where>
```

---

## Condition Tags

### `#[body]` — Auto-guard condition

Auto-extracts `:param` from direct children, generates `isNotEmpty()` OGNL test. Nested conditions have their own guards.

```sql
where #[name = :name]
→ <where><if test="isNotEmpty(name)"> name = #{name}</if></where>

-- With and/or prefix
where id = :id #[and status = :status]
→ <where>id = #{id} <if test="isNotEmpty(status)"> and status = #{status}</if></where>

-- Multi-line body
where #[
    or (name like :name
        and age = :age)
]
→ <where><if test="isNotEmpty(name) and isNotEmpty(age)"> or (name like #{name} and age = #{age})</if></where>

-- Nested (inner params don't bubble up)
where #[id = :id #[and name = :name]]
→ <if test="isNotEmpty(id)"> id = #{id} <if test="isNotEmpty(name)"> and name = #{name}</if></if>
```

### `#if(expr)[body]` — Custom guard condition

`expr` is a custom OGNL expression. `:param` in guard auto-strips colon.

```sql
where #if(:age > 2 && :age < 18)[or(name like :name and age = :age)]
→ <if test="age > 2 && age < 18"> or(name like #{name} and age = #{age})</if>

-- Guard without colon
where #if(age > 2)[name = :name]
→ <if test="age > 2"> name = #{name}</if>

-- Empty guard (same as #[body])
where #if()[and status = :status]
→ <if test="isNotEmpty(status)"> and status = #{status}</if>
```

### `#condition` — Form 1 (must be on its own line)

`#` followed by an identifier. **Must occupy its own line** (only whitespace before `#` since last newline). Auto-generates `isNotEmpty()` guard.

```sql
where
  #id = :id
→ <where><if test="isNotEmpty(id)"> id = #{id}</if></where>

-- ❌ Inline error
where #id = :id
→ mgxsql syntax error: '#condition' form 1 must be on its own line
```

### `#and` / `#or` — Line-prefix conditions (must be on their own line)

Include `and`/`or` inside the `<if>` tag. **Must be on their own line**. Only valid in scope block top-level.

```sql
where
  #id = :id
  #and name = :name
→ <where><if test="isNotEmpty(id)"> id = #{id}</if><if test="isNotEmpty(name)"> and name = #{name}</if></where>

-- SET domain with comma prefix
update t_user set
  #name = :name
  #,age = :age
where id = :id
→ <set><if test="isNotEmpty(name)"> name = #{name}</if><if test="isNotEmpty(age)"> , age = #{age}</if></set>
```

> **Important**: `#and`/`#or` are NOT allowed inside `#[...]` / `#if(expr)[...]`. Use nested `#[and ...]` instead.

---

## IN Clause

| Syntax | Description |
|--------|-------------|
| `in :list` | Simple collection → `<foreach>` |
| `in (:list)` | Parenthesized, equivalent to above |
| `in (item:list)=>$item.id` | Complex type with property access |
| `in ((item:list)=>$item.id)` | Outer parens consumed, equivalent |
| `in #{list}` | MyBatis native, preserved as-is |

```sql
where id in :idList
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item}</foreach>

where id in (item:idList)=>$item.id
→ id in <foreach item="item" collection="idList" open="(" close=")" separator=",">#{item.id}</foreach>
```

> **Restriction**: `=>` right side only accepts `$variable`, not `#{}` or `${}`.

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
→ <if test="isNotEmpty(name)"> name like <bind name="_like_name" value="'%' + name + '%'"/>#{_like_name}</if>

where #[name like :name%]
→ <if test="isNotEmpty(name)"> name like <bind name="_like_name" value="name + '%'"/>#{_like_name}</if>
```

---

## Parameter Binding

| Syntax | Converts to | Scope Block | Condition Node Block |
|--------|-------------|-------------|----------------------|
| `:param` | `#{param}` | ✅ | ✅ |
| `:user.name` | `#{user.name}` | ✅ | ✅ |
| `#{param}` | Preserved as-is | ✅ | ❌ Error |
| `${param}` | Preserved as-is | ✅ | ❌ Error |
| `$variable` | `#{variable}` | Only on `=>` right | Only on `=>` right |

---

## XML Pass-through & String Literals

- `<`-prefixed XML tags in **scope blocks** are preserved as-is (not parsed by MGXSQL)
- XML tags in **condition node blocks** are syntax errors
- Single-quoted strings: all MGXSQL syntax is ignored inside `'...''

```sql
-- XML in scope block (OK)
where id in :idList and <if test="id != null">id = #{id}</if>
→ <where>id in <foreach .../> and <if test="id != null">id = #{id}</if></where>

-- String literal (OK)
where name = 'where something' and #[code = :code]
→ 'where something' doesn't trigger scope tag
```

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

## Syntax Quick Reference

```
┌───────────────────────────┬──────────────────────────────────────────────────┐
│ MGXSQL Syntax             │ Converted Result                                 │
├───────────────────────────┼──────────────────────────────────────────────────┤
│ where                     │ <where>... (closes at clause keyword or end)     │
│ where[body]               │ <where>body</where> (closes at ])                │
│ set                       │ <set>... (closes at where or end)                │
│ set[body]                 │ <set>body</set> (closes at ])                    │
│ #[body]                   │ <if test="isNotEmpty(...)"> body</if>            │
│ #if(expr)[body]             │ <if test="expr"> body</if>                       │
│ #condition                │ <if test="isNotEmpty(...)"> condition</if>       │
│ #and condition            │ <if test="isNotEmpty(...)"> and condition</if>   │
│ #or condition             │ <if test="isNotEmpty(...)"> or condition</if>    │
│ :param                    │ #{param}                                         │
│ :user.name                │ #{user.name}                                     │
│ #{param}                  │ Preserved (scope block) / Error (node block)     │
│ ${param}                  │ Preserved (scope block) / Error (node block)     │
│ $variable                 │ #{variable} (only on => right side)              │
│ in :list                  │ <foreach item="item" collection="list" ...>      │
│ in (item:list)=>$item.x   │ <foreach item="item" collection="list" ...>      │
│ in #{list}                │ Preserved (MyBatis native)                        │
│ %:name%                   │ <bind> + both-side LIKE                          │
│ :name%                    │ <bind> + right-side LIKE                         │
│ %:name                    │ <bind> + left-side LIKE                          │
│ <xml-tag>                 │ Preserved (scope block) / Error (node block)     │
│ 'string literal'          │ Preserved, internal syntax ignored                │
└───────────────────────────┴──────────────────────────────────────────────────┘
```

---

## MGXSQL vs MGXQL: When to Use Which

```
┌─────────────────────────────────────────────────────────────────┐
│               Dynamic Condition Decision Tree                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Need JOIN / aggregation / custom projection?                    │
│     ├── Yes → @Statement (MGXQL) + optional ? prefix            │
│     └── No ↓                                                    │
│                                                                 │
│  Need dynamic WHERE / SET conditions?                            │
│     ├── Simple (1-3) → Method name query or QueryEntity         │
│     ├── Complex dynamic → MGXSQL (@Lang + @Select/@Update)      │
│     └── Dynamic + JOIN → @Statement (MGXQL) with ? prefix       │
│                                                                 │
│  Need <foreach>/<bind> generation?                               │
│     └── Yes → MGXSQL (in :list / %:name% syntax)               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

| Dimension | MGXQL (@Statement) | MGXSQL (@Lang + @Select) |
|-----------|-------------------|--------------------------|
| Positioning | Object query language, HQL-like | Dynamic SQL syntax sugar |
| SQL basis | Entity/field names | Real table/column names |
| Dynamic conditions | `?` prefix (null check) | `#[body]` (isNotEmpty), `#if(expr)[body]` (custom) |
| JOIN | ✅ | ❌ |
| Aggregation | ✅ | ❌ |
| IN/LIKE shorthand | ❌ | ✅ (auto foreach/bind) |
| Use case | Complex queries, multi-table | Dynamic conditions, optional params |

### `?` prefix vs `#[body]`

Both make conditions optional, but with different mechanisms:

```java
// MGXQL ? prefix — null check
@Statement("select * from User where ?name = :name and ?age > :age")
// → <if test="name != null"> name = #{name} </if>
// → <if test="age != null"> and age > #{age} </if>

// MGXSQL #[body] — isNotEmpty check (also rejects empty string/collection)
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[name = :name] #[and age > :age]")
// → <if test="isNotEmpty(name)"> name = #{name} </if>
// → <if test="isNotEmpty(age)"> and age > #{age} </if>
```

| Aspect | MGXQL `?` | MGXSQL `#[body]` |
|--------|-----------|-------------------|
| Check type | `param != null` | `isNotEmpty(param)` (rejects empty string/collection) |
| Combine multiple params | Each `?` generates separate `<if>` | All params in one `<if>` with `and` |
| Custom expression | Not supported | Use `#if(expr)[body]` |
| Auto foreach/bind | ❌ | ✅ |

---

## Common Syntax Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `条件节点块内不允许使用 #{param}` | `#[id = #{id}]` | Use `:param`: `#[id = :id]` |
| `条件节点块内不允许使用 ${param}` | `#[id = ${id}]` | Use `:param` |
| `条件节点块内不允许使用 XML 标签` | `#[<if test="...">...</if>]` | Use `#if(expr)[body]` |
| `'#condition' 形式1必须独占一行` | `where #id = :id` (inline) | Newline: `where\n  #id = :id` |
| `'#and'/'#or' 必须独占一行` | `where #and name = :name` | Newline or use `#[and name = :name]` |
| `条件节点块内不允许 #and/#or` | `#[id = :id #and name = :name]` | Nested: `#[id = :id #[and name = :name]]` |
| `'[' 未闭合` | `where[id = :id` | Close bracket: `where[id = :id]` |
| `'=>' 右边只接受 $variable` | `in (item:list)=>#{item.id}` | Use `$item.id` |

> For complete syntax reference, see [docs/mgxsql.md](../../docs/mgxsql.md).

---

## v6 Additions (2026-07)

v6 adds 4 capabilities and renames the custom guard (`#(expr)`→`#if(expr)`):

- **`#for(item:collection)=>expr`** — standalone `<foreach>` directive (decoupled from `in`); supports composite `[$a,$b]` (tuple IN, `separator="),("`). Coexists with `in :list` sugar.
- **`#include[sqlId]`** — `<include refid="sqlId"/>`; static refid only; valid in top-level / where-set / `#[...]` / `#if(...)[...]` body.
- **`#bind[name = :param expr]` + `$name`** — explicit computed local var; value is colon-stripped OGNL (`:age + :age`→`age + age`); `$name`→`#{name}`; declaration must precede reference (parser-enforced); name unique per select/update scope.
- **`#if(expr)[body]`** replaces `#(expr)[body]` (deprecated → parser errors). Minimal-form space after `#` is optional (`#and x`≡`# and x`), non-breaking.
- **General rule**: OGNL param access in directive content (guard / body / bind value) MUST use `:param`. `#bind` value is stricter — only `:param` + operators + number/string literals (no `$var`, no bare id, no `#{}`/`${}`). `$var` does NOT contribute to auto-guard (`#[id = $x]`→`<if test="true">`).
