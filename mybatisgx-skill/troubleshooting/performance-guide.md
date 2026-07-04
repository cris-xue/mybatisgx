# MyBatisGX Performance Optimization Guide

This document provides strategies and best practices for optimizing MyBatisGX application performance.

## Understanding Performance Bottlenecks

```
┌─────────────────────────────────────────────────────────────────┐
│                 Performance Bottleneck Sources                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. N+1 Query Problem                 [Association Queries]    │
│  2. Loading Unnecessary Data          [Over-fetching]          │
│  3. Missing Database Indexes          [Slow Queries]           │
│  4. Inefficient Batch Operations      [Loop Inserts/Updates]   │
│  5. Improper Transaction Management   [Lock Contention]        │
│  6. Large Result Sets                 [Memory Issues]          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 1. Solving N+1 Query Problem

### Problem

```java
// This causes N+1 queries
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.SIMPLE)  // ❌ Problem
private List<User> userList;

// Query 100 orgs
List<Org> orgs = orgDao.findList(query);
// Executes: 1 + 100 = 101 SQL queries
```

### Solutions

#### Solution A: Use BATCH Mode (Recommended)

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)  // ✅ Solution
private List<User> userList;

// Query 100 orgs
List<Org> orgs = orgDao.findList(query);
// Executes: 1 + 1 = 2 SQL queries
```

#### Solution B: Use JOIN Mode (Small Result Sets)

```java
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)  // ✅ For small datasets
private List<User> userList;

// Executes: 1 + 1 = 2 SQL queries (with JOINs)
```

#### Solution C: Use Lazy Loading

```java
@OneToMany(fetch = FetchType.LAZY)  // ✅ Load only when needed
private List<User> userList;

// Only loads when accessed
@Transactional(readOnly = true)
public void process() {
    Org org = orgDao.findById(1L);
    // Users loaded here
    List<User> users = org.getUserList();
}
```

### Performance Comparison

Query 100 organizations, each with 10 users:

| Mode | SQL Count | Performance | Result Set |
|------|-----------|-------------|------------|
| SIMPLE | 101 | ❌ Poor | Normal (1000 rows) |
| BATCH | 2 | ✅ Good | Normal (1000 rows) |
| JOIN | 2 | ✅ Good | ⚠️ Inflated (10000 rows) |

## 2. Loading Only Necessary Data

### Use Selective Methods

```java
// ❌ Bad: Updates all fields including nulls
user.setName("New Name");
userDao.updateById(user);  // Sets other fields to null

// ✅ Good: Only updates non-null fields
user.setName("New Name");
userDao.updateByIdSelective(user);  // Other fields unchanged
```

### Use Projection for Partial Fields

Define DTO for partial data:

```java
// DTO for name and age only
public class UserNameAgeDTO {
    private String name;
    private Integer age;
    // getters/setters
}

// Custom DAO method
@Select("SELECT name, age FROM user WHERE id = #{id}")
UserNameAgeDTO findNameAndAgeById(Long id);
```

### Lazy Load Associations

```java
// ❌ Bad: Always loads associated data
@ManyToOne(fetch = FetchType.EAGER)
private Org org;

// ✅ Good: Loads only when needed
@ManyToOne(fetch = FetchType.LAZY)
private Org org;
```

## 3. Database Optimization

### Add Indexes on Query Fields

```sql
-- Create indexes for frequently queried columns
CREATE INDEX idx_user_name ON user(name);
CREATE INDEX idx_user_age ON user(age);
CREATE INDEX idx_user_create_time ON user(create_time);

-- Composite index for multiple conditions
CREATE INDEX idx_user_name_age ON user(name, age);

-- Index for foreign keys
CREATE INDEX idx_user_org_id ON user(org_id);
```

### Analyze Query Performance

```sql
-- Use EXPLAIN to analyze query
EXPLAIN SELECT * FROM user WHERE name LIKE '%zhang%' AND age > 20;

-- Check if indexes are used
-- Look for "Using index" in Extra column
```

### Optimize Query Conditions

```java
// ❌ Bad: LIKE with leading wildcard (can't use index)
query.setNameLike("%zhang");

// ✅ Good: LIKE with trailing wildcard (can use index)
query.setNameStartingWith("zhang");

// ✅ Good: Exact match (best for index)
query.setName("zhang");
```

## 4. Batch Operations

### Use Batch Methods

```java
// ❌ Bad: Loop inserts (100 database calls)
for (User user : users) {
    userDao.insert(user);
}

// ✅ Good: Batch insert (1-2 database calls)
userDao.insertBatch(users, 500);
```

### Optimal Batch Size

```java
// Test different batch sizes for your scenario
// Typical range: 100-1000

// Too small: More network overhead
userDao.insertBatch(users, 10);

// Too large: May exceed packet limit or cause memory issues
userDao.insertBatch(users, 50000);

// Recommended: 500-1000
userDao.insertBatch(users, 500);
```

### Custom Batch Operations

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @BatchOperation
    int updateBatch(@BatchData List<User> users, @BatchSize int batchSize);

    @BatchOperation
    int deleteBatchByIds(@BatchData List<Long> ids, @BatchSize int batchSize);
}
```

## 5. Pagination

### Always Paginate Large Result Sets

```java
// ❌ Bad: Load all data (may cause OOM)
List<User> allUsers = userDao.findList(query);

// ✅ Good: Paginate
Page<User> page = userDao.findPage(query, new Pageable(1, 100));
```

### Optimize COUNT Queries

```java
// For large tables, COUNT(*) can be slow
// Consider:
// 1. Caching total count
// 2. Using approximate count for UI
// 3. Limiting max pages displayed
```

### Cursor-Based Pagination for Large Datasets

```java
// For very large datasets, use cursor-based pagination
// Instead of OFFSET/LIMIT

// DAO method
List<User> findByIdGtOrderById(Long lastId, int limit);

// Usage
Long lastId = 0L;
while (true) {
    List<User> batch = userDao.findByIdGtOrderById(lastId, 1000);
    if (batch.isEmpty()) break;

    // Process batch
    processBatch(batch);

    lastId = batch.get(batch.size() - 1).getId();
}
```

## 6. Transaction Management

### Use Read-Only Transactions

```java
// Read-only transactions allow optimizations
@Transactional(readOnly = true)
public User getUser(Long id) {
    return userDao.findById(id);
}
```

### Keep Transactions Short

```java
// ❌ Bad: Long transaction holding locks
@Transactional
public void processOrder(Order order) {
    orderDao.insert(order);
    // Expensive external API call
    externalService.notify(order);  // Holds lock during call
    inventoryDao.update(order);
}

// ✅ Good: Minimize transaction scope
public void processOrder(Order order) {
    // Transaction 1: Insert order
    orderService.insertOrder(order);

    // Non-transactional: External call
    externalService.notify(order);

    // Transaction 2: Update inventory
    inventoryService.updateInventory(order);
}
```

### Transaction Isolation Levels

```java
// Use appropriate isolation level
@Transactional(isolation = Isolation.READ_COMMITTED)  // Default, good for most cases
public void updateUser(User user) {
    userDao.updateById(user);
}

// For critical operations
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void processPayment(Payment payment) {
    // Ensures data consistency
}
```

## 7. Caching Strategies

### Use MyBatis Second-Level Cache

```yaml
mybatisgx:
  configuration:
    cache-enabled: true
```

```java
// Enable cache for specific DAO
@CacheNamespace(
    implementation = org.mybatis.caches.redis.RedisCache.class,
    eviction = LRU.class,
    flushInterval = 60000,
    size = 1024
)
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

### Application-Level Caching

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        return userDao.findById(id);
    }

    @CacheEvict(value = "users", key = "#user.id")
    public void updateUser(User user) {
        userDao.updateById(user);
    }
}
```

## 8. Connection Pool Optimization

### HikariCP Configuration

```yaml
spring:
  datasource:
    hikari:
      # Connection pool size
      maximum-pool-size: 20        # Max connections
      minimum-idle: 5              # Min idle connections

      # Timeouts
      connection-timeout: 30000    # 30 seconds
      idle-timeout: 600000         # 10 minutes
      max-lifetime: 1800000        # 30 minutes

      # Performance
      connection-test-query: SELECT 1
      leak-detection-threshold: 60000  # 60 seconds
```

### Pool Size Formula

```
optimal_pool_size = ((core_count * 2) + effective_spindle_count)

Example:
- 4 CPU cores
- 1 disk (effective_spindle_count = 1)
- Optimal: (4 * 2) + 1 = 9 connections
```

## 9. Monitoring and Profiling

### Enable SQL Logging

```yaml
# Development
mybatisgx:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Production (use SLF4J)
mybatisgx:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

logging:
  level:
    com.example.dao: DEBUG
```

### Measure Query Performance

```java
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public List<User> search(UserQuery query) {
        long start = System.currentTimeMillis();

        List<User> users = userDao.findList(query);

        long duration = System.currentTimeMillis() - start;
        log.info("Query completed in {}ms, returned {} records", duration, users.size());

        return users;
    }
}
```

### Use Database Monitoring Tools

- MySQL: `SHOW PROCESSLIST`, `SHOW STATUS`
- PostgreSQL: `pg_stat_statements`
- Database-specific monitoring tools

## 10. Best Practices Summary

### DO

✅ Use BATCH fetch mode for collections
✅ Paginate large result sets
✅ Use selective update/insert methods
✅ Add indexes on frequently queried columns
✅ Use read-only transactions for queries
✅ Keep transactions short
✅ Use batch operations for multiple records
✅ Enable SQL logging in development
✅ Monitor query performance
✅ Use appropriate fetch types (LAZY/EAGER)

### DON'T

❌ Use SIMPLE fetch mode for large collections
❌ Load all data without pagination
❌ Use wildcard LIKE with leading %
❌ Perform batch operations in loops
❌ Hold transactions during external API calls
❌ Fetch associations when not needed
❌ Ignore database indexes
❌ Use overly large batch sizes
❌ Keep connections open unnecessarily
❌ Ignore slow query warnings

## Performance Testing Checklist

Before deploying to production:

- [ ] Test with realistic data volumes
- [ ] Check for N+1 queries with SQL logging
- [ ] Verify database indexes exist
- [ ] Test pagination with large datasets
- [ ] Monitor memory usage
- [ ] Check transaction isolation levels
- [ ] Verify batch operation performance
- [ ] Test connection pool under load
- [ ] Profile slow queries
- [ ] Review lazy loading behavior

## Quick Performance Wins

1. **Enable BATCH mode** for all OneToMany/ManyToMany
2. **Add pagination** to all list queries
3. **Use selective methods** (insertSelective, updateByIdSelective)
4. **Add indexes** on foreign keys and query fields
5. **Enable read-only transactions** for queries

These five changes can significantly improve performance with minimal effort!
