# MGXQL — MyBatisGX Query Language

## What is MGXQL?

MGXQL (MyBatisGX Query Language) is a simplified, object-oriented query language inspired by HQL. It serves as the **unified intermediate representation (IR)** in MyBatisGX — all query conditions (method names, entity fields, hand-written expressions) are converted to MGXQL before being validated and rendered into MyBatis XML.

Used via `@Statement` annotation on Mapper interface methods.

```
Method Name ──▶ mgxql string ──┐
Entity Fields ──▶ mgxql string ──┤──▶ MgxqlStatement ──▶ Syntax/Semantic Check ──▶ MyBatis XML
@Statement ──▶ mgxql string ──┘
```

### Condition Priority

```
mapper.xml definition  →  Highest priority (framework does not process)
@Statement annotation  →  High priority
Entity/QueryEntity     →  Medium priority
Method name derivation →  Default behavior
```

---

## Statement Types

### INSERT

```sql
insert EntityName
```

- No parameters, no conditions — declares insert target entity only
- Entity data comes from method parameter

```java
@Statement("insert User")
int insert(User user);
```

### DELETE

```sql
delete EntityName where conditions
```

- WHERE clause **required** (safety requirement)
- Logical delete auto-applied if entity has `@LogicDelete`

```java
@Statement("delete User where id = :id")
int deleteById(@Param("id") Long id);
```

### UPDATE

```sql
update EntityName where conditions
```

- WHERE clause **required** (safety requirement)
- Update data comes from entity method parameter
- Optimistic lock condition auto-appended if entity has `@Version`

```java
@Statement("update User where id = :id")
int updateById(@Param("id") Long id, User entity);
```

### SELECT

```sql
select select_items from entities [where conditions] [group by fields] [having conditions] [order by fields] [limit offset, size]
```

The most feature-rich statement type. See following sections.

---

## SELECT Clause

### Select All Columns

```sql
select * from User
select u.* from User u left join Role r on u = r
```

| Syntax | Meaning |
|--------|---------|
| `*` | All columns from all entities in FROM/JOIN |
| `alias.*` | All columns from specified alias entity |

### Select Specific Columns

```sql
select name, age from User
select u.name, u.age from User u
```

- Field reference: `fieldName` or `alias.fieldName`
- No nested property chains (e.g., `user.role.name` not supported)

### Aggregate Functions

```sql
select count(*) from User
select count(id) from User where status = :status
select count(1) from User
select max(age) from User
select min(age), avg(age), sum(salary) from User
```

| Function | Allowed Arguments | Notes |
|----------|-------------------|-------|
| `count(field)` | Field | Count non-null rows |
| `count(*)` | Asterisk | Count all rows |
| `count(1)` | Number | Count all rows |
| `max(field)` | Field only | Maximum |
| `min(field)` | Field only | Minimum |
| `avg(field)` | Field only | Average |
| `sum(field)` | Field only | Sum |

> **Restriction**: `max/min/avg/sum` only accept field arguments. `*` and numbers are NOT allowed.

---

## FROM / JOIN Clause

### Single Entity

```sql
from User           -- no alias
from User u         -- with alias
```

- Entity name: `UPPER_NAME` format (e.g., `User`, `UserDetail`)
- Alias: `LOWER_NAME` (e.g., `u`, `ud`) or backtick-quoted

### LEFT JOIN

```sql
from User u left join Role r on u = r
from User u left join Role r on u = r left join Menu m on r = m
```

- **Only `left join`** supported (no inner/right/cross join)
- **ON condition**: only `alias1 = alias2` (entity relationship equality)
- Framework auto-derives FK join condition from JPA annotations (`@OneToOne`, `@OneToMany`, `@ManyToMany`)
- Many-to-many: junction table auto-inserted

### Alias Requirements

| Scenario | Alias Required? |
|----------|----------------|
| Single entity, no JOIN | Optional |
| Multiple entities (with JOIN) | **Required** for ALL entities |
| Alias uniqueness | **Must be unique** within query |

---

## WHERE Clause

### Comparison Operators

| Operator | Example |
|----------|---------|
| `=` | `name = :name` |
| `!=` | `status != :status` |
| `<` / `<=` | `age < :age` |
| `>` / `>=` | `age >= :minAge` |

### Matching Operators

| Operator | Example | Notes |
|----------|---------|-------|
| `like` | `name like :name` | Fuzzy match |
| `left like` | `name left like :name` | Left fuzzy (`%value`) |
| `right like` | `name right like :name` | Right fuzzy (`value%`) |
| `in` | `id in :ids` | Contains |
| `not in` | `id not in :ids` | Not contains |
| `between` | `age between :ageRange` | Range |
| `not between` | `age not between :ageRange` | Not in range |
| `not like` | `name not like :name` | Not fuzzy |

### NULL Checks

```sql
where name is null
where name is not null
```

### Logical Operators

- `and` — higher precedence
- `or` — lower precedence
- `()` — override precedence

```sql
where name = :name and (age < :age or status = :status)
```

### Parameter References

```sql
:paramName              -- simple parameter (@Param)
:paramName.nestedField  -- nested property path
```

Number literals also allowed: `age > 18`

---

## Dynamic Condition Blocks

MGXQL supports dynamic conditions in WHERE clause. When parameters are null/empty, conditions are automatically skipped.

### `#[body]` — Auto-guard Condition

Auto-extracts `:param` from body, generates `isNotEmpty()` OGNL test:

```sql
select * from User where #[name = :name] #[and age > :age]
```

- Parameter null/empty → condition not generated
- `and`/`or` prefix inside body connects to previous condition
- Nested `#[...]` params don't bubble up to outer guard

```java
@Statement("select * from User where #[name = :name] #[and age > :age]")
List<User> search(@Param("name") String name, @Param("age") Integer age);
// name=null, age=25 → WHERE age > 25
```

### `#if(expr)[body]` — Custom Guard

```sql
select * from User where #if(:age != null)[age >= :age]
select * from User where #if(:minAge != null && :maxAge != null)[age between :ageRange]
```

- `expr` is custom OGNL expression
- `:param` in guard auto-strips colon
- Supports `&&`, `||`, `==`, `!=`, `>`, `<`

### `#choose/#when/#otherwise` — Multi-branch Mutual Exclusion

```sql
select * from User where #choose[
  #when(:type == 'vip')[level = :level]
  #when(:type == 'normal')[status = :status]
  #otherwise[code = :code]
]
```

- Maps to MyBatis `<choose>/<when>/<otherwise>`
- `#when(expr)` MUST have guard expression
- `#otherwise` optional
- Body follows `#[body]` rules (`:param`, `in :list`, `%:name%` allowed; `#{}`/`${}`/`<xml>` forbidden)

> **Restriction**: Dynamic block body does NOT support nested dynamic gates.

---

## IN Shorthand

```sql
-- Simple collection → <foreach>
where id in :idList

-- Complex object collection (property access)
where id in (item:userList)=>$item.id
```

Framework auto-generates `<foreach>` tag.

---

## LIKE Shorthand

```sql
-- Both-side fuzzy → LIKE '%keyword%'
where #[name like %:name%]

-- Right-side fuzzy → LIKE 'keyword%'
where #[name like :name%]

-- Left-side fuzzy → LIKE '%keyword'
where #[name like %:name]
```

Framework auto-generates `<bind>` tag for pattern concatenation.

---

## Syntax Domains (where[] / set[])

MGXQL WHERE and SET clauses render as bounded MGXSQL subsets:

- `where[body]` → MGXSQL generates `<where>` tag
- `set[body]` → MGXSQL generates `<set>` tag (for UPDATE)

Dynamic blocks (`#[...]`, `#if(...)[...]`, `#choose[...]`) inside WHERE/SET are processed by the MGXSQL engine.

---

## GROUP BY Clause

```sql
select count(u.id) from User u group by u.code
select count(u.id) from User u group by u.code, u.dept
```

---

## HAVING Clause

```sql
having count(u.id) > :minCount
having count(u.id) > :minCount and max(u.age) > :maxAge
having (count(u.id) > :minCount or max(u.id) > :minId) and avg(u.age) > :avgAge
```

- Left side MUST be aggregate function
- Right side: parameter reference (`:param`) or number literal
- Supports `and`/`or` and parentheses

---

## ORDER BY Clause

```sql
order by name desc
order by name desc, age asc
```

Default direction: `asc`. Multiple fields comma-separated.

---

## LIMIT Clause

```sql
limit 0, 10       -- skip 0, take 10
limit 5, 5        -- skip 5, take 5
```

- Fixed pagination only (number literals)
- For dynamic pagination, use `Pageable` parameter

---

## `?` Prefix Migration Guide

The `?` prefix for optional conditions is **deprecated**. Migrate to dynamic condition blocks:

| Old Syntax | New Syntax | Notes |
|------------|------------|-------|
| `?name = :name` | `#[name = :name]` | Auto isNotEmpty guard |
| `and ?age >= :age` | `#[and age >= :age]` | Connector inside body |
| `or ?code like :code` | `#[or code like %:code%]` | LIKE pattern shorthand |
| Custom null check | `#if(:age != null)[age >= :age]` | Custom guard |
| Multi-branch | `#choose[#when(...)[...] #otherwise[...]]` | Choose/when/otherwise |

---

## Naming Rules

| Type | Format | Examples |
|------|--------|----------|
| Entity name | UPPER_CASE_START | `User`, `UserDetail`, `Role` |
| Alias | lower_case_start or backtick | `u`, `ud`, `` `user` `` |
| Field name | lower_case_start or backtick | `name`, `id`, `` `inputTime` `` |
| Parameter | `:` prefix | `:id`, `:user.name` |

Field names correspond to Java entity **property names**, not database column names.

---

## Validation Rules

### Syntax Validation (SyntaxCheckerChain)

| Checker | Rule |
|---------|------|
| WhereRequiredChecker | DELETE/UPDATE must have WHERE |
| AliasRequirementChecker | JOIN queries: field refs must use alias |
| AggregateArgumentChecker | max/min/avg/sum: field only; count: also `*` and number |
| FieldAliasChecker | Alias in field ref must be declared in FROM/JOIN |
| OnAliasChecker | Alias in ON must be declared in FROM/JOIN |

### Semantic Validation (SemanticCheckerChain)

| Checker | Rule |
|---------|------|
| EntityChecker | Entity name must be registered |
| FieldChecker | Field must exist on entity |
| JoinRelationChecker | JOIN must have JPA relationship annotation |
| SelectFieldChecker | SELECT fields must belong to FROM/JOIN entities |
| WhereFieldChecker | WHERE fields must belong to FROM/JOIN entities |
| DmlAliasPrefixChecker | DML field refs must use correct alias prefix |

---

## MGXQL vs HQL Comparison

### Similarities

- Entity/field names (not table/column names)
- `:param` parameter binding
- JOIN based on entity relationships
- Aggregate functions

### Differences

| Aspect | MGXQL | HQL |
|--------|-------|-----|
| ON clause | Simplified `alias = alias` | Full `ON alias.col = alias.col` |
| Subqueries | Not supported | Supported |
| SELECT NEW (DTO) | Not supported | Supported |
| UPDATE/DELETE with JOIN | Single entity only | Supported |
| Dynamic conditions | `#[body]`, `#if`, `#choose` | Not built-in |

---

## Full Example

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @Statement("select * from User where id = :id")
    User findUser(@Param("id") Long id);

    @Statement("select u.* from User u left join UserDetail ud on u = ud where u.id = :id")
    User findWithDetail(@Param("id") Long id);

    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount")
    List<Map<String, Object>> groupByCode(@Param("minCount") long minCount);

    @Statement("select * from User where #[name like %:name%] #[and age > :age] #[and id in :idList]")
    List<User> search(@Param("name") String name, @Param("age") Integer age, @Param("idList") List<Long> idList);

    @Statement("select * from User where #choose[#when(:type == 'vip')[level >= :level] #otherwise[status = :status]]")
    List<User> findByType(@Param("type") String type, @Param("level") Integer level, @Param("status") Integer status);

    @Statement("delete User where id = :id")
    int deleteById(@Param("id") Long id);

    @Statement("update User where code = :code")
    int updateByCode(@Param("code") String code, User entity);

    @Statement("insert User")
    int insert(User user);

    @Statement("select * from User order by id desc limit 0, 10")
    List<User> findTop10();
}
```

---

## MGXQL vs MGXSQL: When to Use Which

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

See [MGXSQL Syntax Reference](mgxsql.md) for complete MGXSQL documentation.
