# MyBatisGX Core Concepts

## What is MyBatisGX?

> Retain MyBatis controllability, provide JPA-like development efficiency

MyBatisGX is an enhanced ORM framework based on MyBatis. It is NOT a replacement for MyBatis-Plus, nor is it a JPA imitator. It's designed for developers who "hate black boxes but don't want to write repetitive SQL".

### Core Philosophy

```
MyBatisGX = MyBatis Controllability + JPA Development Efficiency
```

## The Three Pillars Problem

Traditional persistence frameworks face an irreconcilable "iron triangle" dilemma:

### 1. Business Logic "Intrusion" Burden

Current generic Mapper solutions (MyBatis-Plus/Flex) or JPA's Specification often require complex Wrapper or Criteria construction in the Service layer. This causes **severe persistence logic leakage into the business layer**.

### 2. "Boilerplate Code" vs "Flexibility" Trade-off

Native MyBatis has extreme SQL control but requires lengthy XML even for basic single-table CRUD. Developers need a **"zero-config start, unlimited customization"** balanced experience.

### 3. Object Association Query "Engineering Challenge"

Simple object associations (1:1, 1:N) in traditional MyBatis require manual ResultMap and Join SQL writing, which is tedious and prone to N+1 query performance issues.

## Relationship with JPA

**MyBatisGX is NOT a JPA replacement, nor a compromise between MyBatis and JPA.**

We chose to reuse mature, long-verified metadata expressions from JPA (entity mapping and association annotations) to unify object models and relational descriptions, NOT to introduce JPA's runtime mechanism.

In MyBatisGX:

- ❌ No EntityManager or Session lifecycle
- ❌ No Persistence Context
- ❌ No implicit SQL execution, dirty checking, or auto Flush
- ✅ All SQL generation and execution timing is explicit, predictable, and overridable

**JPA annotations in MyBatisGX only play a "structure description" role, NOT "behavior control".**

All SQL still follows MyBatis execution model:
- SQL can be pre-generated
- SQL can be fully controlled by mapper.xml
- SQL's final form is always visible and modifiable

## MGXQL: Unified Intermediate Representation

MyBatisGX uses **MGXQL (MyBatisGX Query Language)** as a unified intermediate representation (IR). All query conditions — regardless of source — are converted to MGXQL before being validated and rendered into MyBatis XML.

```
┌──────────────────────────────────────────────────────────────────┐
│                    MGXQL Unified Pipeline                        │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Method Name ──▶ BaseStatement ──▶ mgxql string ──┐            │
│  (findByNameAndAgeGt)                               │            │
│                                                     ▼            │
│  Entity/QueryEntity ──▶ synthetic method name ──▶ MgxqlStatement │
│  (fields → findByNameAndAge)                 (unified IR)        │
│                                                     │            │
│  @Statement ──▶ mgxql string ──────────────────────┘            │
│  (hand-written)                                                  │
│                                                     │            │
│                                        Syntax + Semantic Check   │
│                                                     │            │
│                                                     ▼            │
│                                              MyBatis XML SQL     │
└──────────────────────────────────────────────────────────────────┘
```

### MGXQL Source Types

| Source | Enum | Description |
|--------|------|-------------|
| Method name | `METHOD_NAME` | Derived from method name DSL |
| Entity fields | `ENTITY` | Derived from Entity/QueryEntity field metadata |
| Hand-written | `MANUAL` | Directly written in `@Statement` annotation |

MGXQL is a simplified, object-oriented query language inspired by HQL. It covers the most common 80% of query patterns while keeping the language easy to learn. See [MGXQL Syntax Reference](mgxql.md) for complete documentation.

## Design Philosophy

### SQL Controllability

#### 1. XML Has Highest Priority

In MyBatisGX, SQL defined in mapper.xml always has the highest priority:

- Framework-generated SQL can be completely overridden by XML
- Developers always retain ultimate control
- During debugging, you can view generated XML or manually modify it

#### 2. SQL Pre-generation

```
┌─────────────────────────────────────────────────────────────────┐
│                   SQL Pre-generation Flow                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. MyBatis loads mapper.xml                                    │
│  2. Scan all entities and parse field dependencies             │
│  3. Scan DAO interfaces and parse method names                 │
│  4. Generate MyBatis XNode based on entity and method info      │
│  5. Register XNode to MyBatis                                   │
│                                                                 │
│  → After startup, all SQL is generated, no runtime overhead    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### 3. No Implicit Behavior

MyBatisGX will NOT:

- Execute SQL implicitly
- Perform automatic dirty checking
- Auto Flush
- Introduce persistence context

Any SQL execution is explicit and predictable.

### Service Layer Responsibility Separation

#### The Problem: Persistence Logic Leakage

In many enterprise projects, query conditions are dynamically assembled in the Service layer as Wrapper, Criteria, or Specification:

```java
// Typical Service layer query assembly (NOT recommended)
public List<User> searchUsers(String name, Integer age, String dept) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.isNotBlank(name)) {
        wrapper.like(User::getName, name);
    }
    if (age != null) {
        wrapper.gt(User::getAge, age);
    }
    if (StringUtils.isNotBlank(dept)) {
        wrapper.eq(User::getDept, dept);
    }
    return userMapper.selectList(wrapper);
}
```

Problems:
- Query semantics scattered across multiple Service methods
- Business code filled with field names and database details
- Query logic difficult to reuse and evolve

#### The Solution: Query Convergence to DAO Layer

MyBatisGX's design stance:

- **Queries themselves are stable business capabilities, not implementation details**
- **Service layer only expresses business processes, not database query semantics**
- **All queries converge to DAO layer as method definitions and QueryEntity**

```java
// Recommended: QueryEntity encapsulation
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
    private String dept;
}

// Clean Service layer
public List<User> searchUsers(UserQuery query) {
    return userDao.findList(query);
}
```

### AI Era Advantages

#### Context Burden

Current LLMs have limited context. We should reduce unnecessary code in precious context space, allowing it to carry more meaningful tokens.

#### Code Volume Comparison

**Traditional Way**
```java
// Service layer + Wrapper (lots of field names and condition logic)
public List<User> search(String name, Integer age) {
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.isNotBlank(name)) {
        wrapper.like(User::getName, name);
    }
    if (age != null) {
        wrapper.gt(User::getAge, age);
    }
    return userMapper.selectList(wrapper);
}
```

**MyBatisGX Way**
```java
// DAO interface definition (one line)
List<User> findByNameLikeAndAgeGt(String name, Integer age);

// Or QueryEntity way
List<User> findList(UserQuery query);
```

**Comparison Summary**

| Aspect | Traditional | MyBatisGX |
|--------|------------|-----------|
| Code Volume | High | Low |
| Context Usage | High | Low |
| Readability | Scattered | Centralized |
| Reusability | Low | High |

## Comparison with Other Frameworks

### Performance Dimension

| Framework | Performance | Runtime Overhead |
|-----------|-------------|------------------|
| MyBatis | ⭐⭐⭐⭐⭐ | None |
| MyBatisGX | ⭐⭐⭐⭐⭐ | None (pre-generated) |
| MyBatis-Plus | ⭐⭐⭐⭐ | Wrapper building |
| JPA | ⭐⭐⭐ | Reflection, dirty checking, auto-flush |

**MyBatisGX Performance Advantages:**

```
┌─────────────────────────────────────────────────────────────────┐
│               SQL Pre-generation Mechanism                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Startup Phase                   Runtime Phase                  │
│  ┌─────────────┐               ┌─────────────┐                 │
│  │ Scan DAO    │               │             │                 │
│  │ Parse names │ ───────────▶  │ Direct exec │                 │
│  │ Generate XML│               │ No overhead │                 │
│  └─────────────┘               └─────────────┘                 │
│                                                                 │
│  vs JPA runtime reflection/dirty-check/auto-flush              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Ease of Use Dimension

**Code Volume Comparison:**

| Framework | Code Example | Lines |
|-----------|--------------|-------|
| MyBatis | XML mapper | ~10 lines |
| MyBatis-Plus | Wrapper in Service | ~8 lines |
| JPA | Specification | ~10 lines |
| **MyBatisGX** | **Method name** | **1 line** |

```java
// MyBatisGX: method name = query
List<User> findByNameAndAge(String name, Integer age);
```

### Controllability Dimension

**SQL Visibility:**

| Framework | SQL Visibility | Description |
|-----------|----------------|-------------|
| MyBatis | Fully visible | Explicitly defined in XML |
| MyBatisGX | Fully visible | Generated XML at startup, viewable |
| MyBatis-Plus | Partially visible | Dynamic Wrapper construction |
| JPA | Not visible | Generated at runtime, hard to predict |

**SQL Override Mechanism:**

```
┌─────────────────────────────────────────────────────────────────┐
│                      SQL Priority                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  mapper.xml definition  ───────────────────▶  Highest priority  │
│       ↓                                                         │
│  @Statement annotation  ───────────────────▶  High priority     │
│       ↓                                                         │
│  Entity/QueryEntity     ───────────────────▶  Medium priority   │
│       ↓                                                         │
│  Method name derivation ───────────────────▶  Default behavior  │
│                                                                 │
│  Any level can be overridden by higher levels                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**No Implicit Behavior:**

| Behavior | MyBatisGX | JPA |
|----------|-----------|-----|
| Implicit SQL execution | ❌ No | ✅ Yes |
| Auto dirty checking | ❌ No | ✅ Yes |
| Lazy loading trigger | Explicit call | May trigger implicitly |
| Transaction Flush | Manual control | Auto Flush |

## When to Choose MyBatisGX

### Choose MyBatisGX if you:

- Need to reduce boilerplate code without sacrificing SQL controllability
- Want query logic to converge in DAO layer, not invade Service layer
- Need declarative association queries without JPA complexity
- Want to reduce code volume and context burden in AI era
- Hate JPA's black-box runtime but appreciate its object modeling design

### Choose MyBatis if you:

- Need absolute control over every SQL statement
- Project is simple, no ORM enhancement needed

### Choose MyBatis-Plus if you:

- Accustomed to Wrapper-style query building
- Have existing project ecosystem

### Choose JPA if you:

- Fully object-oriented modeling
- Can accept black-box runtime
- Need advanced features like change tracking

## Supported Databases

- MySQL
- Oracle
- PostgreSQL

## Key Design Decisions Summary

| Decision | Reason |
|----------|--------|
| SQL pre-generation | No runtime overhead, optimal performance |
| XML highest priority | Retain absolute control |
| QueryEntity decoupling | Clear Service layer responsibilities |
| Reuse JPA annotations | Mature semantics, low learning cost |
| No JPA runtime | Avoid N+1, dirty checking, implicit behaviors |

## Next Steps

To learn more about MyBatisGX features:
- Basic features: CRUD operations, method name queries, pagination
- Relation queries: Associations and fetch modes
- Advanced features: Logic delete, optimistic lock, audit, composite keys
