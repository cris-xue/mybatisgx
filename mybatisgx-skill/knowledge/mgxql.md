# MGXQL — MyBatisGX Query Language

## What is MGXQL?

MGXQL (MyBatisGX Query Language) is a simplified, object-oriented query language inspired by HQL (Hibernate Query Language). It serves as the **unified intermediate representation (IR)** in MyBatisGX — all query conditions (method names, entity fields, hand-written expressions) are converted to MGXQL before being validated and rendered into MyBatis XML.

```
┌──────────────────────────────────────────────────────────────────┐
│                    MGXQL: Unified IR Architecture                │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Method Name ──▶ BaseStatement ──▶ mgxql string ──┐            │
│  (findByNameAndAgeGt)                               │            │
│                                                     │            │
│  Entity/QueryEntity ──▶ synthetic method name ─────┤            │
│  (fields → findByNameAndAge)                        │            │
│                                                     ▼            │
│                                              MgxqlStatement      │
│  @Statement ──▶ mgxql string ──────────────────▶ (unified IR)   │
│  (hand-written)                                     │            │
│                                                     ▼            │
│                                        Syntax + Semantic Check   │
│                                                     │            │
│                                                     ▼            │
│                                              MyBatis XML SQL     │
└──────────────────────────────────────────────────────────────────┘
```

### MGXQL Source Types

| Source | Enum | Description |
|--------|------|-------------|
| Method name | `METHOD_NAME` | Derived from method name DSL (e.g., `findByNameAndAgeGt`) |
| Entity fields | `ENTITY` | Derived from Entity/QueryEntity field metadata |
| Hand-written | `MANUAL` | Directly written in `@Statement` annotation |

### Condition Priority

```
mapper.xml definition  ───────────────────▶  Highest priority
     ↓
@Statement annotation  ───────────────────▶  High priority
     ↓
Entity/QueryEntity     ───────────────────▶  Medium priority
     ↓
Method name derivation ───────────────────▶  Default behavior
```

Any level can be overridden by higher levels.

---

## Statement Types

MGXQL supports four statement types:

### INSERT

```sql
insert
```

Simple insert — no MGXQL expression needed. The framework auto-generates INSERT SQL from entity metadata.

### DELETE

```sql
delete EntityName where conditions
```

- Must include WHERE clause (safety requirement)
- No alias prefix allowed in WHERE fields
- Logical delete auto-applied if entity has `@LogicDelete`

```java
@Statement("delete User where name = :name")
void deleteByName(@Param("name") String name);
```

### UPDATE

```sql
update EntityName where conditions
```

- Must include WHERE clause (safety requirement)
- No alias prefix allowed in WHERE fields
- Optimistic lock condition auto-appended if entity has `@Version`

```java
@Statement("update User where name = :name")
int updateByName(@Param("name") String name, User entity);
```

### SELECT

```sql
select [items] from [entities] [where conditions] [group by fields] [having conditions] [order by fields] [limit offset,size]
```

The most feature-rich statement type. See following sections for each clause.

---

## SELECT Clause

### Select All Columns

```sql
select * from User where name = :name
select user.* from User user where user.name = :name
```

### Select Specific Columns

```sql
select id, name, age from User where name = :name
select user.id, user.name, role.name from User user left join Role role on user = role
```

### Aggregate Functions

| Function | Syntax | Argument Types |
|----------|--------|---------------|
| COUNT | `count(*)`, `count(1)`, `count(field)` | `*`, number, or field |
| MAX | `max(field)` | Field only |
| MIN | `min(field)` | Field only |
| AVG | `avg(field)` | Field only |
| SUM | `sum(field)` | Field only |

> **Important**: Only COUNT supports `*` and number (e.g., `count(1)`) arguments. MAX, MIN, AVG, SUM must use a field reference.

```sql
select count(*) from User where age > :age
select max(age) from User where dept = :dept
select avg(salary) from User group by dept having avg(salary) > :minSalary
```

---

## FROM / JOIN Clause

### Single Entity (no alias)

```sql
select * from User where name = :name
```

### Single Entity (with alias)

```sql
select * from User user where user.name = :name
```

### LEFT JOIN

```sql
select user.name, role.name
from User user
left join Role role on user = role
where user.name = :name
```

**ON Syntax Simplification**: Unlike SQL where you write `ON user.id = role.user_id`, MGXQL uses a simplified form:

```sql
on alias1 = alias2
```

The framework **automatically derives** the foreign key join condition from entity relationship metadata (`@JoinColumn`, `@JoinTable`). You only specify which two entity aliases are related.

### Alias Requirements

| Scenario | Alias Required? |
|----------|----------------|
| Single entity, no JOIN | Optional |
| Multiple entities (with JOIN) | **Required** for ALL entities |
| Alias uniqueness | **Must be unique** within a query |

### Many-to-Many Auto-Derivation

For `@ManyToMany` relationships, the framework automatically inserts the middle/junction table:

```sql
-- MGXQL (you write)
select user.*, menu.*
from User user
left join Role role on user = role
left join Menu menu on role = menu
where user.id = :id

-- Generated SQL (framework expands)
select user.*, menu.*
from user user
left join user_role user_role on user.id = user_role.user_id
left join role role on user_role.role_id = role.id
left join role_menu role_menu on role.id = role_menu.role_id
left join menu menu on role_menu.menu_id = menu.id
where user.id = #{id}
```

---

## WHERE Clause

### Comparison Operators

| Operator | SQL | Example |
|----------|-----|---------|
| `=` | `=` | `name = :name` |
| `!=` | `!=` | `status != :status` |
| `<` | `<` | `age < :age` |
| `<=` | `<=` | `age <= :maxAge` |
| `>` | `>` | `age > :minAge` |
| `>=` | `>=` | `age >= :minAge` |
| `like` | `like` | `name like :name` |
| `left like` | `like 'value%'` | `name left like :name` |
| `right like` | `like '%value'` | `name right like :name` |
| `in` | `in` | `id in :ids` |
| `not in` | `not in` | `id not in :ids` |
| `between` | `between` | `age between :minAge` |
| `is null` | `is null` | `name is null` |
| `is not null` | `is not null` | `name is not null` |

### Logical Operators

- `and` — higher precedence (evaluated first)
- `or` — lower precedence
- Parentheses `()` — override precedence

```sql
where name = :name and (age < :age or status = :status)
```

### Optional Conditions (`?` prefix)

Prefix a condition with `?` to make it optional. Optional conditions generate MyBatis `<if>` tags:

```sql
-- MGXQL
where ?name = :name and age > :age

-- Generated MyBatis XML
<where>
  <if test="name != null"> name = #{name} </if>
  <if test="age != null"> and age > #{age} </if>
</where>
```

### Parameter References

Use `:` prefix to reference method parameters:

```sql
where name = :name                          -- simple parameter
where user.name = :user.name                -- nested path
```

Parameters are resolved by priority:
1. `@Param`-annotated method parameter matching the path
2. Entity/QueryEntity field matching the path
3. Positional `arg0`, `arg1`, etc.

### Field References

- Without alias: `name` (for single-entity queries)
- With alias: `user.name` (**required** for multi-entity queries)

> **Important**: In multi-entity queries (with JOIN), ALL field references in WHERE MUST use alias prefix. Example: `where user.name = :name`, not `where name = :name`.

---

## GROUP BY Clause

```sql
select * from User where age > :age group by dept
select * from User where age > :age group by dept, status
select user.dept from User user group by user.dept
```

---

## HAVING Clause

HAVING supports aggregate function comparisons with AND/OR nesting:

```sql
having max(age) > :age
having count(id) > :minCount and avg(salary) > :minSalary
having (max(age) > :maxAge or min(age) < :minAge) and count(id) > 10
```

**Restrictions**:
- Left side MUST be an aggregate function (`count`, `max`, `min`, `avg`, `sum`)
- Right side MUST be a parameter reference (`:param`) or a number literal
- Field references are NOT allowed as comparison values in HAVING

---

## ORDER BY Clause

```sql
select * from User where name = :name order by name desc
select * from User where name = :name order by name desc, age asc
select user.name from User user order by user.name
```

Default direction is `asc` if not specified.

---

## LIMIT Clause

```sql
select * from User where name = :name limit 0, 10
```

- Syntax: `limit offset, size`
- Offset starts from 0
- Only supports **fixed** pagination — for dynamic pagination, use `Pageable` parameter or PageHelper

---

## Three Condition Sources Comparison

### Method Name → MGXQL

```
findByNameLikeAndAgeGtOrderByNameDesc
    ↓ MethodSyntaxProcessor (ANTLR parse)
BaseStatement (QueryStatement)
    ↓ toMgxql()
"select * from User where name like :name and age > :age order by name desc"
    ↓ MgxqlSyntaxProcessor
MgxqlStatement → MyBatis XML
```

**When to use**: Simple single-entity queries with 1-3 conditions. Quick and readable.

### Entity/QueryEntity Fields → MGXQL

```
UserQuery fields (nameLike, ageGt, dept)
    ↓ entityToMethodName()
"findByNameLikeAndAgeGtAndDept"
    ↓ same pipeline as method name
MgxqlStatement → MyBatis XML
```

**When to use**: Dynamic multi-condition queries where some conditions may be null. Supports `@Dynamic` and `?` optional conditions natively.

### @Statement → MGXQL

```
@Statement("select * from User where name = :name")
    ↓ MgxqlSyntaxProcessor (direct)
MgxqlStatement → MyBatis XML
```

**When to use**: Complex queries — JOINs, aggregations, specific projections, or conditions that method names cannot express.

### Decision Guide

| Scenario | Recommended Approach |
|----------|---------------------|
| Simple CRUD (findById, findAll) | Built-in SimpleDao methods |
| 1-3 conditions, single entity | Method name query |
| Dynamic conditions, null-skipping | QueryEntity |
| Multi-entity JOIN | @Statement |
| Aggregation + GROUP BY + HAVING | @Statement |
| Custom projection (DTO return) | @Statement |
| Very complex SQL | mapper.xml (highest priority) |

---

## MGXQL vs HQL Comparison

### Similarities

| Aspect | MGXQL | HQL |
|--------|-------|-----|
| Based on entity/object model | ✅ | ✅ |
| Uses entity name (not table name) | ✅ | ✅ |
| Uses field name (not column name) | ✅ | ✅ |
| Parameter binding | ✅ `:param` | ✅ `:param` |
| JOIN based on entity relationships | ✅ | ✅ |
| Aggregate functions | ✅ | ✅ |

### Differences

| Aspect | MGXQL | HQL |
|--------|-------|-----|
| ON clause | Simplified `alias = alias`, FK auto-derived | Full `ON alias.col = alias.col` |
| Subqueries | ❌ Not supported | ✅ |
| SELECT NEW (DTO) | ❌ Not supported (use ResultMap) | ✅ |
| UPDATE/DELETE with JOIN | ❌ Single entity only | ✅ |
| Fetch join | ❌ (use FetchMode) | ✅ |
| Polymorphic queries | ❌ | ✅ |
| Collection filtering | ❌ | ✅ |
| Named queries | Via @Statement | Via @NamedQuery |

MGXQL intentionally keeps a minimal feature set — it's a "weakened HQL" that covers the most common 80% of query patterns while keeping the language easy to learn and the parser fast.

---

## Full Syntax Reference

```
sql_statement        = insert_statement | delete_statement | update_statement | select_statement

insert_statement     = "insert"
delete_statement     = "delete" entity_name where_clause
update_statement     = "update" entity_name where_clause

select_statement     = "select" select_item_clause select_from_clause
                       [where_clause] [group_by_clause] [having_clause]
                       [order_by_clause] [limit_clause]

select_item_clause   = select_item ("," select_item)*
select_item          = select_column_all | select_column_custom | aggregate_function
select_column_all    = "*" | entity_name_alias "." "*"
select_column_custom = field_reference
aggregate_function   = func_name "(" aggregate_argument ")"
func_name            = "count" | "max" | "min" | "avg" | "sum"
aggregate_argument   = field_reference | number | "*"

select_from_clause   = "from" select_primary_entity select_join_entity*
select_primary_entity = entity_name [entity_name_alias]
select_join_entity   = "left" "join" entity_name [entity_name_alias] "on" entity_name_alias "=" entity_name_alias

where_clause         = "where" condition_or_expression
condition_or_expression = condition_and_expression ("or" condition_and_expression)*
condition_and_expression = condition_term ("and" condition_term)*
condition_term       = condition_comparison | "(" condition_or_expression ")"
condition_comparison = ["?"] field_reference (comparison_param | comparison_null)
comparison_param     = (relational_op | matching_op) condition_value
comparison_null      = "is null" | "is not null"
condition_value      = parameter_reference | number

group_by_clause      = "group" "by" field_reference ("," field_reference)*
having_clause        = "having" having_or_expression
having_or_expression = having_and_expression ("or" having_and_expression)*
having_and_expression = having_term ("and" having_term)*
having_term          = having_comparison | "(" having_or_expression ")"
having_comparison    = aggregate_function relational_op (parameter_reference | number)
order_by_clause      = "order" "by" order_by_expression ("," order_by_expression)*
order_by_expression  = field_reference ["asc" | "desc"]
limit_clause         = "limit" number "," number

field_reference      = field_name | entity_name_alias "." field_name
parameter_reference  = ":" field_name ("." field_name)*
entity_name          = UPPER_CASE_START (e.g., User, OrderItem)
entity_name_alias    = lower_case_start | `quoted` (e.g., user, `order`)
field_name           = lower_case_start | `quoted` (e.g., name, `order`)

relational_op        = "=" | "!=" | "<" | "<=" | ">" | ">="
matching_op          = ["not"] ("between" | "in" | "like" | "left like" | "right like")
```

> **Note**: This syntax reference is based on `MgxqlLexer.g4` and `MgxqlParser.g4` in `mybatisgx-core/src/main/resources/antlr/mgxql/`.

---

## MGXQL vs MGXSQL: When to Use Which

MyBatisGX has two complementary SQL simplification features. MGXQL is an object query language for `@Statement`; MGXSQL is a dynamic SQL syntax for `@Select`/`@Update`/`@Delete` with `@Lang`. They serve different purposes.

### Decision Tree

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

### Core Comparison

| Dimension | MGXQL (@Statement) | MGXSQL (@Lang + @Select) |
|-----------|-------------------|--------------------------|
| Positioning | Object query language, HQL-like | Dynamic SQL syntax sugar |
| SQL basis | Entity/field names (e.g., `User`, `name`) | Real table/column names (e.g., `t_user`, `name`) |
| Dynamic conditions | `?` prefix (null check) | `#[body]` (isNotEmpty), `#(expr)[body]` (custom) |
| JOIN | ✅ `left join ... on alias = alias` | ❌ |
| Aggregation | ✅ `count(*)`, `max(field)`, etc. | ❌ |
| IN/LIKE shorthand | ❌ Manual foreach/bind | ✅ Auto-generates `<foreach>` and `<bind>` |
| Use case | Complex queries, multi-table joins | Dynamic conditions, optional params |

### `?` prefix vs `#[body]`

Both make conditions optional, but with different check mechanisms:

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
| Custom expression | Not supported | Use `#(expr)[body]` |
| Auto foreach/bind | ❌ | ✅ |

See [MGXSQL Syntax Reference](mgxsql.md) for complete MGXSQL documentation.
