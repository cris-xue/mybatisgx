# MyBatisGX Basic Features

This document covers the core features that you'll use daily with MyBatisGX: Entity mapping, DAO interfaces, CRUD operations, method name queries, QueryEntity, pagination, and dynamic SQL.

## Entity Definition

### Basic Entity

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // getters/setters
}
```

### Key Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Entity` | Mark class as entity | `@Entity` |
| `@Table` | Specify table name | `@Table(name = "user")` |
| `@Id` | Mark primary key field | `@Id` |
| `@Column` | Specify column name | `@Column(name = "create_time")` |

### Field Naming

- Java property names use camelCase: `createTime`
- Database columns use snake_case: `create_time`
- Auto mapping enabled with `map-underscore-to-camel-case: true`

## DAO Interface

### SimpleDao Interface

Provides basic CRUD operations:

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
    // User: Entity type
    // UserQuery: QueryEntity type
    // Long: Primary key type
}
```

### Built-in Methods

**Insert Methods:**
```java
void insert(User user);                      // Insert all fields
void insertSelective(User user);             // Insert non-null fields only
void insertBatch(List<User> users);          // Batch insert (default 1000)
void insertBatch(List<User> users, int batchSize);  // Custom batch size
```

**Query Methods:**
```java
User findById(Long id);                      // Find by primary key
User findOne(UserQuery query);               // Find single record
List<User> findList(UserQuery query);        // Find list
Page<User> findPage(UserQuery query, Pageable pageable);  // Pagination
```

**Update Methods:**
```java
void updateById(User user);                  // Update all fields
void updateByIdSelective(User user);         // Update non-null fields only
void updateBatchById(List<User> users);      // Batch update
```

**Delete Methods:**
```java
void deleteById(Long id);                    // Delete by primary key
void deleteBatchById(List<Long> ids);        // Batch delete
```

### CrudDao and SelectDao

```java
// If you only need select operations
public interface UserDao extends SelectDao<User, UserQuery, Long> {
}

// If you need full CRUD
public interface UserDao extends CrudDao<User, UserQuery, Long> {
}
```

## Method Name Query

### How It Works: Unified MGXQL Pipeline

Method name queries are **syntax sugar** that get converted to MGXQL before SQL generation:

```
findByNameLikeAndAgeGt
    ↓ MethodSyntaxProcessor (ANTLR4 parse)
BaseStatement (QueryStatement)
    ↓ toMgxql()
"select * from User where name like :name and age > :age"
    ↓ MgxqlSyntaxProcessor (ANTLR4 parse + validate)
MgxqlStatement (unified IR)
    ↓ Template rendering
MyBatis XML SQL
```

The method name is just one of three entry points into the MGXQL pipeline. Entity/QueryEntity fields and `@Statement` annotations follow the same path. See [MGXQL Syntax Reference](mgxql.md) for details.

### Basic Format

MyBatisGX generates SQL automatically from method names:

```java
// Query methods (find/get/query/select)
List<User> findByName(String name);
User getByName(String name);
List<User> queryByName(String name);
List<User> selectByName(String name);

// Update methods
int updateNameById(String name, Long id);

// Delete methods
int deleteByName(String name);
```

### Query Keywords

**Comparison Operators:**

| Keyword | SQL | Example |
|---------|-----|---------|
| `Eq` / `Equal` | `=` | `findByNameEq(String name)` |
| `Lt` | `<` | `findByAgeLt(Integer age)` |
| `Lteq` | `<=` | `findByAgeLteq(Integer age)` |
| `Gt` | `>` | `findByAgeGt(Integer age)` |
| `Gteq` | `>=` | `findByAgeGteq(Integer age)` |

**Fuzzy Operators:**

| Keyword | SQL Pattern | Example |
|---------|-------------|---------|
| `Like` | `LIKE '%?%'` | `findByNameLike(String name)` |
| `StartingWith` | `LIKE '?%'` | `findByNameStartingWith(String name)` |
| `EndingWith` | `LIKE '%?'` | `findByNameEndingWith(String name)` |

**Range Operators:**

| Keyword | SQL | Example |
|---------|-----|---------|
| `Between` | `BETWEEN ? AND ?` | `findByAgeBetween(Integer min, Integer max)` |
| `In` | `IN (?)` | `findByIdIn(List<Long> ids)` |

**Null Operators:**

| Keyword | SQL | Example |
|---------|-----|---------|
| `IsNull` | `IS NULL` | `findByNameIsNull()` |
| `IsNotNull` / `NotNull` | `IS NOT NULL` | `findByNameNotNull()` |

**Logic Operators:**

```java
// AND condition
List<User> findByNameAndAge(String name, Integer age);

// OR condition
List<User> findByNameOrAge(String name, Integer age);

// NOT combination
List<User> findByNameNotLike(String name);
List<User> findByAgeNotGt(Integer age);
```

### Sorting

```java
// Single field sort
List<User> findByNameOrderByAgeDesc(String name);

// Multiple field sort
List<User> findByNameOrderByAgeDescNameAsc(String name);
```

### Limiting Results

```java
// Top N results
List<User> findTop10ByAgeGt(Integer age);

// First result
User findFirstByName(String name);
User findTopByName(String name);
```

### Count Queries

```java
Long countByAgeGt(Integer age);
Long countByNameLike(String name);
```

### Method Naming Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                     Method Name Structure                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  (Operation)(Conditions)(Sorting)                               │
│                                                                 │
│  find    ByNameAndAge      OrderByAgeDesc                       │
│  ├───    └────────────┘    └─────────────┘                      │
│  Action    Conditions        Sorting                            │
│                                                                 │
│  update  NameByAgeAndDept                                       │
│  ──────  └──┘└────────────┘                                     │
│  Action  SET   WHERE                                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## QueryEntity

### Purpose

QueryEntity decouples query conditions from the Service layer to the DAO layer. Use QueryEntity when queries have multiple conditions.

### Basic Definition

```java
@QueryEntity(User.class)
public class UserQuery extends User {

    private String nameLike;    // name LIKE '%?%'

    private Integer ageGt;      // age > ?

    private List<Long> idIn;    // id IN (?)

    // getters/setters
}
```

### Field Naming Convention

Field name = **property name + condition suffix**

| Suffix | SQL | Java Field | Generated SQL |
|--------|-----|------------|---------------|
| (none) | `=` | `name` | `name = ?` |
| `Like` | `LIKE '%?%'` | `nameLike` | `name LIKE '%?%'` |
| `StartingWith` | `LIKE '?%'` | `nameStartingWith` | `name LIKE '?%'` |
| `EndingWith` | `LIKE '%?'` | `nameEndingWith` | `name LIKE '%?'` |
| `Lt` | `<` | `ageLt` | `age < ?` |
| `Lteq` | `<=` | `ageLteq` | `age <= ?` |
| `Gt` | `>` | `ageGt` | `age > ?` |
| `Gteq` | `>=` | `ageGteq` | `age >= ?` |
| `Between` | `BETWEEN` | `ageBetween` (List) | `age BETWEEN ? AND ?` |
| `In` | `IN` | `idIn` (List) | `id IN (?)` |
| `IsNull` | `IS NULL` | `nameIsNull` (Boolean) | `name IS NULL` |
| `NotNull` | `IS NOT NULL` | `nameNotNull` (Boolean) | `name IS NOT NULL` |

### Usage Example

```java
UserQuery query = new UserQuery();
query.setNameLike("Zhang");
query.setAgeGt(20);
query.setIdIn(Arrays.asList(1L, 2L, 3L));

// Generated SQL:
// WHERE name LIKE '%Zhang%' AND age > 20 AND id IN (1, 2, 3)
List<User> users = userDao.findList(query);
```

### Dynamic Conditions

QueryEntity works with `@Dynamic` annotation - null fields are automatically skipped:

```java
UserQuery query = new UserQuery();
// Only set nameLike, ageGt is null and will be skipped
query.setNameLike("Zhang");

// Generated SQL: WHERE name LIKE '%Zhang%'
List<User> users = userDao.findList(query);
```

### QueryEntity vs Method Name

| Approach | Best For | Pros | Cons |
|----------|----------|------|------|
| Method name | Simple queries | Concise, intuitive | Long method names with many conditions |
| QueryEntity | Complex queries | Reusable, composable | Requires extra class definition |

**Method Name Approach:**
```java
// Method name becomes very long
List<User> findByNameLikeAndAgeGtAndAgeLtAndDept(String name, Integer ageGt, Integer ageLt, String dept);
```

**QueryEntity Approach:**
```java
// Clean and organized
UserQuery query = new UserQuery();
query.setNameLike(name);
query.setAgeGt(ageGt);
query.setAgeLt(ageLt);
query.setDept(dept);

List<User> users = userDao.findList(query);
```

## Pagination

### Basic Usage

```java
// Build pagination parameters
Pageable pageable = new Pageable(1, 10);  // Page 1, 10 per page

// Build query conditions
UserQuery query = new UserQuery();
query.setNameLike("Zhang");

// Execute paginated query
Page<User> page = userDao.findPage(query, pageable);

// Get results
long total = page.getTotal();       // Total count
List<User> list = page.getList();   // Current page data
```

### Pageable Object

**Constructor:**
```java
Pageable pageable = new Pageable(pageNo, pageSize);

// Examples
Pageable pageable = new Pageable(1, 10);   // Page 1, 10 per page
Pageable pageable = new Pageable(2, 20);   // Page 2, 20 per page
```

**Adding Sorting:**
```java
Pageable pageable = new Pageable(1, 10);

// Add sorting (use database column names, not Java property names)
pageable.addSort("create_time", "DESC");
pageable.addSort("name", "ASC");
```

**Properties:**

| Property | Type | Description |
|----------|------|-------------|
| `pageNo` | Integer | Page number (starts from 1) |
| `pageSize` | Integer | Records per page |
| `sorts` | List<Sort> | Sort conditions |

### Page Object

**Properties:**

| Property | Type | Description |
|----------|------|-------------|
| `total` | long | Total record count |
| `list` | List<T> | Current page data |

**Usage:**
```java
Page<User> page = userDao.findPage(query, pageable);

// Total records
long total = page.getTotal();

// Current page data
List<User> users = page.getList();

// Calculate total pages
int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());
```

### Custom Paginated Methods

Define custom paginated methods in DAO interface:

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // Method name derived pagination
    Page<User> findByNameLike(String name, Pageable pageable);

    // With conditions and sorting
    Page<User> findByAgeGtOrderByCreateTimeDesc(Integer age, Pageable pageable);
}
```

### PageHelper Integration

MyBatisGX pagination uses PageHelper plugin. You can also use PageHelper directly:

```java
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

// Using PageHelper
PageHelper.startPage(1, 10);
List<User> users = userDao.findList(query);
PageInfo<User> pageInfo = new PageInfo<>(users);
```

**Comparison:**

| Approach | Pros | Cons |
|----------|------|------|
| Built-in pagination | Type-safe, returns Page object | Requires Pageable parameter |
| PageHelper | Flexible, doesn't change method signature | Runtime setting, manual result wrapping |

### Complete Pagination Example

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Page<User> searchUsers(String name, Integer minAge, int pageNo, int pageSize) {
        // Build query conditions
        UserQuery query = new UserQuery();
        if (StringUtils.isNotBlank(name)) {
            query.setNameLike(name);
        }
        if (minAge != null) {
            query.setAgeGteq(minAge);
        }

        // Build pagination parameters
        Pageable pageable = new Pageable(pageNo, pageSize);
        pageable.addSort("create_time", "DESC");

        // Execute query
        return userDao.findPage(query, pageable);
    }
}
```

## Dynamic SQL

### @Dynamic Annotation

Fields with null values are automatically skipped in WHERE clauses:

```java
UserQuery query = new UserQuery();
query.setName("Zhang");
// age is null, will be skipped

// Generated SQL: WHERE name = 'Zhang'
// (age condition is not included)
List<User> users = userDao.findList(query);
```

### Built-in Dynamic Support

Select/Update methods in SimpleDao have built-in `@Dynamic` support:
- `findOne`
- `findList`
- `findPage`
- Update methods with selective suffix

## Priority Rules

### SQL Generation Priority

```
┌─────────────────────────────────────────────────────────────────┐
│                      Priority Order                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  mapper.xml definition        ───────▶  Highest (overrides all) │
│       ↓                                                         │
│  @Statement annotation        ───────▶  High                    │
│       ↓                                                         │
│  Entity/QueryEntity fields    ───────▶  Medium                  │
│       ↓                                                         │
│  Method name keywords         ───────▶  Lowest (default)        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Method Parameter Priority

```
@Param annotation > Entity fields > QueryEntity fields > Simple parameters
```

## @Statement Annotation

When method names or QueryEntity cannot express complex queries, use `@Statement` to write MGXQL directly:

### SELECT with @Statement

```java
@Statement("select * from User where name = :name and age > :age")
List<User> findActiveUsers(@Param("name") String name, @Param("age") Integer age);

@Statement("select user.name, role.name from User user left join Role role on user = role where user.dept = :dept")
List<UserRoleProjection> findUserRoles(@Param("dept") String dept);

@Statement("select count(*) from User where dept = :dept")
long countByDept(@Param("dept") String dept);
```

### DELETE/UPDATE with @Statement

```java
@Statement("delete User where name = :name")
void deleteByName(@Param("name") String name);

@Statement("update User where name = :name")
int updateByName(@Param("name") String name, User entity);
```

### Optional Conditions in @Statement

Use `?` prefix for optional conditions (generates MyBatis `<if>` tags):

```java
@Statement("select * from User where ?name = :name and ?age > :age")
List<User> search(@Param("name") String name, @Param("age") Integer age);
```

### @Statement vs Method Name vs QueryEntity

| Aspect | Method Name | QueryEntity | @Statement |
|--------|-------------|-------------|------------|
| Syntax | `findByNameAndAge` | Java class fields | MGXQL string |
| JOIN support | ❌ | ❌ | ✅ |
| Aggregation | `countBy` only | ❌ | ✅ (count/max/min/avg/sum) |
| Dynamic conditions | Limited | ✅ (null-skipping) | ✅ (`?` prefix) |
| Projection | Entity only | Entity only | Custom columns |
| Complexity | Simple | Medium | Complex |
| Readability | High | High | Medium |

## Best Practices

### 1. When to Use Method Name vs QueryEntity

**Use Method Name when:**
- Query has 1-3 conditions
- Query is simple and straightforward
- Method name is readable

**Use QueryEntity when:**
- Query has 4+ conditions
- Multiple queries share similar conditions
- Need to compose conditions dynamically

### 2. Service Layer Cleanliness

**❌ Don't Do This:**
```java
// Service layer building query conditions (BAD)
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

**✅ Do This:**
```java
// Query logic in DAO layer (GOOD)
public List<User> search(UserQuery query) {
    return userDao.findList(query);
}
```

### 3. Naming Consistency

- Entity fields: `createTime` (camelCase)
- Database columns: `create_time` (snake_case)
- QueryEntity fields: `nameLike`, `ageGt` (camelCase + suffix)
- Method names: `findByNameLike` (camelCase)

### 4. NULL Handling

- Use `insertSelective` and `updateByIdSelective` to skip NULL fields
- Use QueryEntity with @Dynamic for dynamic WHERE conditions
- Built-in query methods already handle NULL values properly

## Common Patterns

### Pattern 1: Simple CRUD

```java
// Create
User user = new User();
user.setName("Zhang San");
user.setAge(25);
userDao.insert(user);

// Read
User found = userDao.findById(user.getId());

// Update
found.setAge(26);
userDao.updateById(found);

// Delete
userDao.deleteById(found.getId());
```

### Pattern 2: Conditional Query

```java
UserQuery query = new UserQuery();
query.setNameLike("Zhang");
query.setAgeGteq(20);
query.setAgeLteq(40);

List<User> users = userDao.findList(query);
```

### Pattern 3: Paginated Search

```java
public Page<User> search(String keyword, int pageNo, int pageSize) {
    UserQuery query = new UserQuery();
    query.setNameLike(keyword);

    Pageable pageable = new Pageable(pageNo, pageSize);
    pageable.addSort("create_time", "DESC");

    return userDao.findPage(query, pageable);
}
```

## Entity/QueryEntity to MGXQL Pipeline

When a method has an Entity or QueryEntity parameter, the framework converts entity fields into a synthetic method name, then feeds it through the same MGXQL pipeline:

```
UserQuery fields (nameLike, ageGt, dept)
    ↓ entityToMethodName()
"findByNameLikeAndAgeGtAndDept"
    ↓ same pipeline as method name
MgxqlStatement → MyBatis XML SQL
```

Fields annotated with `@QueryColumn(ignore=true)` are excluded from the synthetic method name — they only serve as parameter carriers for `@Statement` or mapper.xml methods.

### @QueryColumn(ignore)

By default, all QueryEntity fields participate in auto-generated query conditions. Use `@QueryColumn(ignore=true)` to exclude a field:

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;

    @QueryColumn(ignore = true)  // Not included in auto-conditions
    private String sortField;    // Only used in @Statement or mapper.xml
}
```

## Next Steps

- Learn about relation queries: associations and fetch modes
- Explore advanced features: logic delete, optimistic lock, audit
- Understand troubleshooting: common errors and performance optimization
