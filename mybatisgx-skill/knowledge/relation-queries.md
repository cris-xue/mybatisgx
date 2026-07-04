# MyBatisGX Relation Queries

This document covers association queries in MyBatis GX: one-to-one, one-to-many, many-to-one, many-to-many relationships, and fetch modes to solve N+1 problems.

## Overview

MyBatisGX supports declarative association query configuration through annotations. Define relationships between entities, and the framework automatically handles associated data fetching.

## Association Types

```
┌─────────────────────────────────────────────────────────────────┐
│                      Association Types                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  One-to-One (OneToOne)                                          │
│  ┌────────┐         ┌────────────┐                              │
│  │  User  │ ──────▶ │ UserDetail │                              │
│  └────────┘  1:1    └────────────┘                              │
│                                                                 │
│  One-to-Many (OneToMany)                                        │
│  ┌────────┐         ┌────────┐                                  │
│  │  Org   │ ──────▶ │  User  │                                  │
│  └────────┘  1:N    └────────┘                                  │
│                                                                 │
│  Many-to-One (ManyToOne)                                        │
│  ┌────────┐         ┌────────┐                                  │
│  │  User  │ ──────▶ │  Org   │                                  │
│  └────────┘  N:1    └────────┘                                  │
│                                                                 │
│  Many-to-Many (ManyToMany)                                      │
│  ┌────────┐         ┌──────────┐         ┌────────┐             │
│  │  User  │ ──────▶ │ UserRole │ ◀────── │  Role  │             │
│  └────────┘  M:N    └──────────┘         └────────┘             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Annotation System

| Annotation | Purpose |
|------------|---------|
| `@OneToOne` | One-to-one association |
| `@OneToMany` | One-to-many association |
| `@ManyToOne` | Many-to-one association |
| `@ManyToMany` | Many-to-many association |
| `@JoinColumn` | Foreign key column configuration |
| `@JoinTable` | Join table configuration (for many-to-many) |
| `@Fetch` | Fetch mode configuration |
| `FetchMode` | Fetch mode enum (SIMPLE/BATCH/JOIN) |

## One-to-One Association

### Unidirectional One-to-One

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // User knows about UserDetail
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;
}

@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    private String address;
    private String phone;
}
```

### Bidirectional One-to-One

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    // mappedBy indicates the relationship is managed by the other side
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;
}

@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    private Long id;

    // The owner side with @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
```

## One-to-Many Association

### Unidirectional One-to-Many

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // Organization has many users
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // No reference to Org
}
```

**Database Schema:**
```sql
CREATE TABLE org (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    org_id BIGINT  -- Foreign key
);
```

### Bidirectional One-to-Many

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // mappedBy indicates User.org field manages the relationship
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // Owner side with @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;
}
```

## Many-to-One Association

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // Many users belong to one organization
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.SIMPLE)
    private Org org;
}

@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;
}
```

## Many-to-Many Association

### With Join Table

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}

@Entity
@Table(name = "role")
public class Role {

    @Id
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roleList")
    private List<User> userList;
}
```

**Database Schema:**
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE role (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);
```

## Fetch Strategies

### FetchType

| Strategy | Description |
|----------|-------------|
| `LAZY` | Lazy loading - query when accessed |
| `EAGER` | Eager loading - query immediately with main entity |

```java
// Eager loading
@OneToOne(fetch = FetchType.EAGER)
private UserDetail userDetail;

// Lazy loading
@OneToMany(fetch = FetchType.LAZY)
private List<User> userList;
```

### FetchMode (Solving N+1 Problem)

MyBatisGX provides three fetch modes to address association query performance:

```
┌─────────────────────────────────────────────────────────────────┐
│                      Fetch Mode Comparison                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  SIMPLE: N+1 Problem                                            │
│  ┌────────┐      ┌────────┐      ┌────────┐                    │
│  │ Query  │ ───▶ │ Query  │ ───▶ │ Query  │  ...               │
│  │ Main   │      │ Assoc 1│      │ Assoc 2│                    │
│  └────────┘      └────────┘      └────────┘                    │
│  1 + N = N+1 queries                                            │
│                                                                 │
│  BATCH: Batch Query (Recommended)                               │
│  ┌────────┐      ┌────────────────────────────────┐            │
│  │ Query  │ ───▶ │ Batch query all (WHERE id IN)  │            │
│  │ Main   │      └────────────────────────────────┘            │
│  └────────┘                                                     │
│  1 + M = 1+M queries (M = number of association tables)        │
│                                                                 │
│  JOIN: Join Query                                               │
│  ┌────────────────────────────────────────────────┐            │
│  │ SELECT * FROM main LEFT JOIN assoc ON ...      │            │
│  └────────────────────────────────────────────────┘            │
│  1 + 1 = 2 queries (main + unlimited JOINs)                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### SIMPLE Mode

**Characteristics:**
- Simple query mode
- Each associated record queried separately
- Has N+1 problem
- Supports lazy loading

**Use Cases:**
- Very small result sets
- Associated data rarely accessed

**Example:**
```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.SIMPLE)
@JoinColumn(name = "org_id")
private List<User> userList;
```

**SQL Behavior:**
```sql
-- Query 10 organizations
SELECT * FROM org;

-- Query users for each org separately (10 times)
SELECT * FROM user WHERE org_id = 1;
SELECT * FROM user WHERE org_id = 2;
...
SELECT * FROM user WHERE org_id = 10;

-- Total: 1 + 10 = 11 queries
```

#### BATCH Mode (Recommended)

**Characteristics:**
- Batch query mode
- Collects all foreign keys then batch queries
- Solves N+1 problem
- Supports lazy loading
- **Default recommended mode**

**Use Cases:**
- Large result sets
- Need to access all associated data

**Example:**
```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
@JoinColumn(name = "org_id")
private List<User> userList;
```

**SQL Behavior:**
```sql
-- Query 10 organizations
SELECT * FROM org;

-- Batch query all users
SELECT * FROM user WHERE org_id IN (1, 2, 3, ..., 10);

-- Total: 1 + 1 = 2 queries
```

#### JOIN Mode

**Characteristics:**
- Join query mode
- First level: single table query to get primary keys
- Second level: unlimited JOIN query
- May cause result inflation
- Does NOT support lazy loading

**Use Cases:**
- Very small result sets
- Need all associated data at once

**Example:**
```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
@JoinColumn(name = "org_id")
private List<User> userList;
```

**SQL Behavior:**
```sql
-- Step 1: Single table query to get IDs
SELECT id FROM org;

-- Step 2: Unlimited JOIN query
SELECT *
FROM org
LEFT JOIN user ON user.org_id = org.id
LEFT JOIN user_detail ON user_detail.user_id = user.id
...
WHERE org.id IN (1, 2, 3, ...);

-- Total: 1 + 1 = 2 queries
```

**Result Inflation Problem:**
```sql
-- One-to-many may cause result set inflation
-- 1 org + 100 users = 100 rows returned
-- Requires deduplication in application layer
```

## Fetch Mode Selection Guide

| Scenario | Recommended Mode | Reason |
|----------|------------------|--------|
| Small result set | JOIN | One query, good performance |
| Large result set | BATCH | Avoids N+1, controlled result set |
| Lazy loading needed | SIMPLE / BATCH | JOIN doesn't support lazy loading |
| Deep association levels | BATCH | JOIN may cause excessive inflation |
| Default choice | BATCH | Best balance of performance and flexibility |

## Performance Comparison

Query 100 organizations, each with an average of 10 users:

| Mode | SQL Count | Performance | Result Set Size |
|------|-----------|-------------|-----------------|
| SIMPLE | 1 + 100 = 101 | Poor | Normal |
| BATCH | 1 + 1 = 2 | Good | Normal |
| JOIN | 1 + 1 = 2 | Good | Inflated (1000 rows) |

## Combining FetchType and FetchMode

```java
// Eager loading + Batch query (Recommended)
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
private List<User> userList;

// Lazy loading + Simple query
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.SIMPLE)
private List<User> userList;

// Eager loading + Join query
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
private List<User> userList;
```

## Key Annotation Attributes

### @OneToOne / @OneToMany / @ManyToOne / @ManyToMany

| Attribute | Type | Description | Default |
|-----------|------|-------------|---------|
| `fetch` | FetchType | Fetch strategy (LAZY/EAGER) | LAZY |
| `mappedBy` | String | Field name on relationship owner side | "" |

### @JoinColumn

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Foreign key column name |
| `referencedColumnName` | String | Referenced primary key column name |

### @JoinTable (for many-to-many)

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Join table name |
| `joinColumns` | JoinColumn[] | Join columns (current entity side) |
| `inverseJoinColumns` | JoinColumn[] | Inverse join columns (other entity side) |

## Important Notes

### 1. Auto-Fetch Deactivation

When mapper.xml defines a corresponding method, auto-fetch is **disabled**:

```java
// If UserMapper.xml defines findById
// MyBatisGX will NOT automatically handle association fetching
// Must manually define association query in XML
```

### 2. Collection Initialization

Initialize collections to avoid NullPointerException:

```java
private List<User> userList = new ArrayList<>();
```

### 3. Lazy Loading Requirements

Lazy loading requires access within transaction or Session context:

```java
@Transactional
public void processOrg() {
    Org org = orgDao.findById(1L);
    // Access within transaction
    List<User> users = org.getUserList();
}
```

### 4. Circular References

Be careful with bidirectional associations to avoid infinite loops:

```java
// Add @JsonIgnore or @JsonBackReference to prevent serialization issues
@OneToMany(mappedBy = "org")
@JsonIgnore
private List<User> userList;
```

## Complete Example

```java
@Entity
@Table(name = "org")
public class Org {

    @Id
    private Long id;

    private String name;

    // Recommended: Eager loading + Batch query
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList = new ArrayList<>();

    // Lazy loading parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Org parent;

    // Eager loading children
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<Org> children = new ArrayList<>();

    // Small result set: JOIN mode
    @OneToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "manager_id")
    private User manager;
}

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList = new ArrayList<>();
}
```

## Best Practices

### 1. Use BATCH Mode by Default

Unless you have specific reasons, use BATCH mode for best performance:

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
private List<User> userList;
```

### 2. Use Lazy Loading for Large Associations

For large collections, use lazy loading to avoid unnecessary queries:

```java
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.BATCH)
private List<Order> orders;
```

### 3. Use mappedBy for Bidirectional Associations

Let the "many" side manage the relationship:

```java
// Org side
@OneToMany(mappedBy = "org")
private List<User> userList;

// User side (owner)
@ManyToOne
@JoinColumn(name = "org_id")
private Org org;
```

### 4. Initialize Collections

Always initialize collection fields:

```java
private List<User> userList = new ArrayList<>();
```

## MGXQL LEFT JOIN Queries

In addition to FetchMode-based auto association queries, MyBatisGX supports explicit JOIN queries via `@Statement` with MGXQL syntax.

### Basic LEFT JOIN

```java
@Statement("select user.name, role.name from User user left join Role role on user = role where user.dept = :dept")
List<UserRoleProjection> findUserRoles(@Param("dept") String dept);
```

### ON Syntax Simplification

Unlike SQL where you write `ON user.id = role.user_id`, MGXQL uses a simplified form:

```sql
on alias1 = alias2
```

The framework **automatically derives** the foreign key join condition from entity relationship metadata (`@JoinColumn`, `@JoinTable`). You only specify which two entity aliases are related.

### Many-to-Many Auto-Derivation

For `@ManyToMany` relationships, the framework automatically inserts the middle/junction table:

```sql
-- MGXQL (you write)
select user.*, menu.*
from User user
left join Role role on user = role
left join Menu menu on role = menu
where user.id = :id

-- Generated SQL (framework expands with junction tables)
select user.*, menu.*
from user user
left join user_role user_role on user.id = user_role.user_id
left join role role on user_role.role_id = role.id
left join role_menu role_menu on role.id = role_menu.role_id
left join menu menu on role_menu.menu_id = menu.id
where user.id = #{id}
```

### Alias Requirements

| Scenario | Alias Required? |
|----------|----------------|
| Single entity, no JOIN | Optional |
| Multiple entities (with JOIN) | **Required** for ALL entities |
| Alias uniqueness | **Must be unique** within a query |

### MGXQL LEFT JOIN vs FetchMode

| Aspect | FetchMode (Auto) | @Statement LEFT JOIN |
|--------|-------------------|----------------------|
| Declaration | Entity annotations | DAO method annotation |
| Query control | Framework auto-generates | Developer fully controls |
| Projection | Full entity only | Custom columns/DTO |
| Conditions | Entity field based | Arbitrary MGXQL WHERE |
| N+1 solving | BATCH/JOIN mode | Single JOIN query |
| Use case | Standard associations | Complex/custom queries |

**When to use which**:
- **FetchMode**: Standard entity associations where you want the full entity graph
- **@Statement LEFT JOIN**: Custom projections, filtered associations, or multi-entity queries with specific WHERE conditions

## Next Steps

- Learn about advanced features: logic delete, optimistic lock, audit
- Understand troubleshooting: solving common association issues
- Explore performance optimization techniques
